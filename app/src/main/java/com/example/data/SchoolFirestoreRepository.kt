package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SchoolFirestoreRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    // Secondary constructor resolving the custom named database ID from firebase_applet_config.xml
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    private fun cleanDocId(prefix: String): String {
        val randomSuffix = UUID.randomUUID().toString().replace("-", "").take(10)
        return "${prefix}_$randomSuffix"
    }

    // ------------------------------------------------------------------------
    // 1. ADMIN REGISTRATION & VERIFICATION
    // ------------------------------------------------------------------------
    suspend fun ensureAdminRegistered(): Result<Unit> = runCatching {
        val user = auth.currentUser ?: error("Not signed in")
        val uid = user.uid
        val email = user.email ?: "admin@umvgangauli.in"
        val docRef = db.collection("admins").document(uid)
        val snap = try {
            docRef.get().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            throw e
        }
        if (!snap.exists()) {
            val payload = mapOf(
                "userId" to uid,
                "email" to email,
                "createdAt" to FieldValue.serverTimestamp()
            )
            try {
                docRef.set(payload).await()
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.CREATE, docRef.path)
                throw e
            }
        }
    }

    // ------------------------------------------------------------------------
    // 2. PHOTO GALLERY (ADD WITH OCCASION/THEME & DELETE)
    // ------------------------------------------------------------------------
    fun observeGalleryPhotos(userId: String = requireUserId()): Flow<List<GalleryPhotoItem>> = callbackFlow {
        val path = "gallery_photos"
        val registration = db.collection(path)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.toObjects(
                        GalleryPhotoItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                    trySend(items)
                }
            }
        awaitClose { registration.remove() }
    }

    suspend fun addGalleryPhoto(
        occasionTheme: String,
        title: String,
        imageSource: String,
        dateLabel: String
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val photoId = cleanDocId("photo")
        val item = GalleryPhotoItem(
            id = photoId,
            userId = uid,
            occasionTheme = occasionTheme.trim().take(160),
            title = title.trim().take(200),
            imageSource = imageSource.trim().ifEmpty { "PRESET_CAMPUS" }.take(2000),
            dateLabel = dateLabel.trim().ifEmpty { "08 Oct 2026" }.take(80)
        )
        val docRef = db.collection("gallery_photos").document(photoId)
        try {
            docRef.set(item.toCreateMap()).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
        photoId
    }

    suspend fun deleteGalleryPhoto(photoId: String): Result<Unit> = runCatching {
        requireUserId()
        val docRef = db.collection("gallery_photos").document(photoId)
        try {
            docRef.delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    // ------------------------------------------------------------------------
    // 3. ADMISSIONS MANAGEMENT (CREATE, ACCEPT, CANCEL, DELETE)
    // ------------------------------------------------------------------------
    fun observeAdmissions(userId: String = requireUserId()): Flow<List<AdmissionRecordItem>> = callbackFlow {
        val path = "admissions"
        val registration = db.collection(path)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.toObjects(
                        AdmissionRecordItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                    trySend(items)
                }
            }
        awaitClose { registration.remove() }
    }

    suspend fun createAdmission(
        studentName: String,
        fatherName: String,
        classApplied: String,
        streamApplied: String,
        prevPercentage: String,
        mobileNumber: String,
        village: String
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val admissionId = "UMVG-2026-${(1000..9999).random()}"
        val item = AdmissionRecordItem(
            id = admissionId,
            userId = uid,
            studentName = studentName.trim().take(150),
            fatherName = fatherName.trim().take(150),
            classApplied = classApplied.trim().take(100),
            streamApplied = streamApplied.trim().take(100),
            prevPercentage = prevPercentage.trim().ifEmpty { "80%" }.take(30),
            mobileNumber = mobileNumber.trim().ifEmpty { "9431000000" }.take(20),
            village = village.trim().ifEmpty { "Gangauli" }.take(120),
            status = "PENDING"
        )
        val docRef = db.collection("admissions").document(admissionId)
        try {
            docRef.set(item.toCreateMap()).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
        admissionId
    }

    suspend fun updateAdmissionStatus(
        admissionId: String,
        newStatus: String // "ACCEPTED" or "CANCELLED" or "PENDING"
    ): Result<Unit> = runCatching {
        requireUserId()
        val docRef = db.collection("admissions").document(admissionId)
        try {
            docRef.update(
                mapOf(
                    "status" to newStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteAdmission(admissionId: String): Result<Unit> = runCatching {
        requireUserId()
        val docRef = db.collection("admissions").document(admissionId)
        try {
            docRef.delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    // ------------------------------------------------------------------------
    // 4. SCHOOL BANK ACCOUNT & PAYMENT QR CODE CONFIGURATION
    // ------------------------------------------------------------------------
    fun observeSchoolAccountConfig(userId: String = requireUserId()): Flow<SchoolAccountConfigItem?> = callbackFlow {
        val path = "school_config"
        val registration = db.collection(path)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val item = snapshot.toObjects(
                        SchoolAccountConfigItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: 0L }.firstOrNull()
                    trySend(item)
                }
            }
        awaitClose { registration.remove() }
    }

    suspend fun saveSchoolAccountConfig(
        accountHolderName: String,
        bankName: String,
        accountNumber: String,
        ifscCode: String,
        upiId: String,
        qrCodeTitle: String,
        qrCustomImageUri: String
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val configId = cleanDocId("cfg")
        val docRef = db.collection("school_config").document(configId)
        val item = SchoolAccountConfigItem(
            id = configId,
            userId = uid,
            accountHolderName = accountHolderName.trim().take(160),
            bankName = bankName.trim().take(160),
            accountNumber = accountNumber.trim().take(40),
            ifscCode = ifscCode.trim().uppercase().take(30),
            upiId = upiId.trim().take(120),
            qrCodeTitle = qrCodeTitle.trim().take(160),
            qrCustomImageUri = qrCustomImageUri.trim().ifEmpty { "DEFAULT_QR" }.take(2000)
        )
        try {
            docRef.set(item.toCreateMap()).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
        configId
    }

    // ------------------------------------------------------------------------
    // 5. CLASS-WISE LIVE RESULTS MANAGEMENT
    // ------------------------------------------------------------------------
    fun observeLiveResults(userId: String = requireUserId()): Flow<List<ClassResultRecordItem>> = callbackFlow {
        val path = "live_results"
        val registration = db.collection(path)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.toObjects(
                        ClassResultRecordItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedBy { it.rollNo }
                    trySend(items)
                }
            }
        awaitClose { registration.remove() }
    }

    suspend fun publishClassResult(
        rollNo: String,
        studentName: String,
        fatherName: String,
        className: String,
        totalMarks: Int,
        maxMarks: Int = 500,
        division: String,
        subjectBreakdown: String,
        isLive: Boolean = true
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val resultId = cleanDocId("res_${rollNo.replace("[^a-zA-Z0-9]".toRegex(), "")}")
        val item = ClassResultRecordItem(
            id = resultId,
            userId = uid,
            rollNo = rollNo.trim().take(40),
            studentName = studentName.trim().take(150),
            fatherName = fatherName.trim().take(150),
            className = className.trim().take(80),
            totalMarks = totalMarks.coerceIn(0, 1000).toLong(),
            maxMarks = maxMarks.coerceIn(100, 1000).toLong(),
            division = division.trim().take(80),
            subjectBreakdown = subjectBreakdown.trim().take(600),
            isLive = isLive
        )
        val docRef = db.collection("live_results").document(resultId)
        try {
            docRef.set(item.toCreateMap()).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
        resultId
    }

    suspend fun toggleResultLiveStatus(resultId: String, isLive: Boolean): Result<Unit> = runCatching {
        requireUserId()
        val docRef = db.collection("live_results").document(resultId)
        try {
            docRef.update(
                mapOf(
                    "isLive" to isLive,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun setClassResultsLiveState(
        className: String,
        isLive: Boolean
    ): Result<Int> = runCatching {
        val uid = requireUserId()
        val path = "live_results"
        val querySnapshot = try {
            db.collection(path)
                .whereEqualTo("userId", uid)
                .get()
                .await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, path)
            throw e
        }

        val matchingDocs = querySnapshot.documents.filter {
            it.getString("className").equals(className, ignoreCase = true)
        }
        matchingDocs.forEach { doc ->
            try {
                doc.reference.update(
                    mapOf(
                        "isLive" to isLive,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.UPDATE, doc.reference.path)
                throw e
            }
        }
        matchingDocs.size
    }

    suspend fun deleteClassResult(resultId: String): Result<Unit> = runCatching {
        requireUserId()
        val docRef = db.collection("live_results").document(resultId)
        try {
            docRef.delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    // ------------------------------------------------------------------------
    // 6. TEACHER REGISTRATION & DIRECTORY MANAGEMENT
    // ------------------------------------------------------------------------
    fun observeTeachers(userId: String = requireUserId()): Flow<List<TeacherRecordItem>> = callbackFlow {
        val path = "teachers"
        val registration = db.collection(path)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    handleFirestoreError(error, OperationType.LIST, path)
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.toObjects(
                        TeacherRecordItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedByDescending { it.createdAt?.seconds ?: 0L }
                    trySend(items)
                }
            }
        awaitClose { registration.remove() }
    }

    suspend fun registerTeacher(
        name: String,
        designation: String,
        department: String,
        subject: String,
        qualification: String,
        experience: String,
        phone: String
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val teacherId = cleanDocId("tchr")
        val item = TeacherRecordItem(
            id = teacherId,
            userId = uid,
            name = name.trim().take(150),
            designation = designation.trim().take(120),
            department = department.trim().take(80),
            subject = subject.trim().take(120),
            qualification = qualification.trim().take(160),
            experience = experience.trim().take(80),
            phone = phone.trim().ifEmpty { "06183-245101" }.take(40)
        )
        val docRef = db.collection("teachers").document(teacherId)
        try {
            docRef.set(item.toCreateMap()).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
        teacherId
    }

    suspend fun deleteTeacher(teacherId: String): Result<Unit> = runCatching {
        requireUserId()
        val docRef = db.collection("teachers").document(teacherId)
        try {
            docRef.delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    // ------------------------------------------------------------------------
    // 7. SEED INITIAL SCHOOL DATA FOR NEW ADMIN SESSION
    // ------------------------------------------------------------------------
    suspend fun seedInitialDataIfEmpty() {
        val uid = auth.currentUser?.uid ?: return
        try {
            ensureAdminRegistered()

            // 1. Seed Gallery Photos if empty
            val existingPhotos = db.collection("gallery_photos")
                .whereEqualTo("userId", uid)
                .limit(1)
                .get()
                .await()
            if (existingPhotos.isEmpty) {
                addGalleryPhoto(
                    occasionTheme = "गणतंत्र दिवस एवं ध्वजारोहण समारोह (Republic Day)",
                    title = "उच्च माध्यमिक विद्यालय गंगौली के मुख्य प्रांगण में 77वां गणतंत्र दिवस समारोह एवं मार्च पास्ट",
                    imageSource = "PRESET_CAMPUS",
                    dateLabel = "26 Jan 2026"
                )
                addGalleryPhoto(
                    occasionTheme = "उन्नयन बिहार स्मार्ट क्लास एवं विज्ञान प्रदर्शनी (Science & AI Fest)",
                    title = "75-इंच इंटरएक्टिव स्मार्ट बोर्ड पर 3D विज्ञान मॉडल का अध्ययन करते विद्यार्थी",
                    imageSource = "PRESET_SMART_CLASS",
                    dateLabel = "15 Sep 2026"
                )
                addGalleryPhoto(
                    occasionTheme = "वार्षिक खेलकूद 'तरंग एवं मशाल' प्रतियोगिता (Annual Sports Meet)",
                    title = "वीर कुंवर सिंह खेल मैदान में सिमरी प्रखंड स्तरीय एथलेटिक्स एवं वॉलीबॉल चैंपियनशिप",
                    imageSource = "PRESET_SPORTS",
                    dateLabel = "02 Oct 2026"
                )
                addGalleryPhoto(
                    occasionTheme = "विज्ञान प्रयोगशाला प्रायोगिक सत्र (STEM Practical Workshop)",
                    title = "भौतिकी एवं रसायन प्रयोगशाला में सूक्ष्मदर्शी व उपकरणों से प्रयोग करते कक्षा 12 के छात्र",
                    imageSource = "PRESET_SCIENCE_LAB",
                    dateLabel = "06 Oct 2026"
                )
            }

            // 2. Seed Admissions if empty
            val existingAdmissions = db.collection("admissions")
                .whereEqualTo("userId", uid)
                .limit(1)
                .get()
                .await()
            if (existingAdmissions.isEmpty) {
                createAdmission(
                    studentName = "अंकिता कुमारी (Ankita Kumari)",
                    fatherName = "श्री राजेश कुमार राय",
                    classApplied = "Class 11 (+2 Intermediate)",
                    streamApplied = "Science (I.Sc. - PCM)",
                    prevPercentage = "89.4%",
                    mobileNumber = "9431084512",
                    village = "Gangauli (गंगौली, सिमरी)"
                )
                createAdmission(
                    studentName = "सुमित कुमार ओझा (Sumit Kumar Ojha)",
                    fatherName = "श्री महेंद्र ओझा",
                    classApplied = "Class 9 (Secondary)",
                    streamApplied = "Secondary General (Matric)",
                    prevPercentage = "84.0%",
                    mobileNumber = "9934218765",
                    village = "Simri (सिमरी, बक्सर)"
                )
            }

            // 3. Seed School Account & Payment QR if empty
            val existingConfig = db.collection("school_config")
                .whereEqualTo("userId", uid)
                .limit(1)
                .get()
                .await()
            if (existingConfig.isEmpty) {
                saveSchoolAccountConfig(
                    accountHolderName = "VSS Uchch Madhyamik Vidyalaya Gangauli",
                    bankName = "State Bank of India (SBI) - Simri Branch, Buxar",
                    accountNumber = "38492010485",
                    ifscCode = "SBIN0004821",
                    upiId = "umvgangauli73045@sbi",
                    qrCodeTitle = "आधिकारिक विद्यालय विकास एवं नामांकन शुल्क QR (BSEB Code: 73045)",
                    qrCustomImageUri = "DEFAULT_QR"
                )
            }

            // 4. Seed Class-wise Results if empty
            val existingResults = db.collection("live_results")
                .whereEqualTo("userId", uid)
                .limit(1)
                .get()
                .await()
            if (existingResults.isEmpty) {
                publishClassResult(
                    rollNo = "260101",
                    studentName = "आदित्य कुमार राय (Aditya Kumar Rai)",
                    fatherName = "श्री मनोज कुमार राय",
                    className = "Class 12 Science",
                    totalMarks = 474,
                    maxMarks = 500,
                    division = "1st Division (Distinction)",
                    subjectBreakdown = "Physics: 95 | Chemistry: 94 | Math: 99 | English: 92 | Hindi: 94",
                    isLive = true
                )
                publishClassResult(
                    rollNo = "260102",
                    studentName = "पल्लवी कुमारी पाण्डेय (Pallavi Kumari Pandey)",
                    fatherName = "श्री संजय पाण्डेय",
                    className = "Class 12 Science",
                    totalMarks = 468,
                    maxMarks = 500,
                    division = "1st Division (Distinction)",
                    subjectBreakdown = "Biology: 98 | Physics: 92 | Chemistry: 93 | English: 91 | Hindi: 94",
                    isLive = true
                )
                publishClassResult(
                    rollNo = "260103",
                    studentName = "प्रियांशु कुमार ओझा (Priyanshu Kumar Ojha)",
                    fatherName = "श्री विनोद कुमार ओझा",
                    className = "Class 10",
                    totalMarks = 476,
                    maxMarks = 500,
                    division = "1st Division (School Topper)",
                    subjectBreakdown = "Math: 98 | Science: 98 | Social Sci: 95 | Hindi: 93 | Sanskrit: 92",
                    isLive = true
                )
                publishClassResult(
                    rollNo = "260104",
                    studentName = "साक्षी कुमारी चौबे (Sakshi Kumari Choubey)",
                    fatherName = "श्री दिवाकर चौबे",
                    className = "Class 12 Arts",
                    totalMarks = 457,
                    maxMarks = 500,
                    division = "1st Division (Arts Topper)",
                    subjectBreakdown = "History: 96 | Pol. Sci: 92 | Geography: 92 | Economics: 88 | Hindi: 89",
                    isLive = true
                )
                publishClassResult(
                    rollNo = "260105",
                    studentName = "आयुष कुमार ठाकुर (Ayush Kumar Thakur)",
                    fatherName = "श्री अरविंद ठाकुर",
                    className = "Class 9",
                    totalMarks = 464,
                    maxMarks = 500,
                    division = "1st Division (INSPIRE Scholar)",
                    subjectBreakdown = "Math: 95 | Science: 96 | Social Sci: 92 | Hindi: 91 | Sanskrit: 90",
                    isLive = true
                )
            }

            // 5. Seed Registered Teachers if empty
            val existingTeachers = db.collection("teachers")
                .whereEqualTo("userId", uid)
                .limit(1)
                .get()
                .await()
            if (existingTeachers.isEmpty) {
                registerTeacher(
                    name = "डॉ. राकेश कुमार पाण्डेय (Dr. Rakesh Kumar Pandey)",
                    designation = "प्राचार्य (Principal & Headmaster)",
                    department = "Administration",
                    subject = "Educational Leadership & Physics",
                    qualification = "M.Sc. (Physics), Ph.D., B.Ed. (BPSC Senior Cadre)",
                    experience = "22 Years",
                    phone = "06183-245101"
                )
                registerTeacher(
                    name = "डॉ. राजीव नयन मिश्रा (Dr. Rajeev Nayan Mishra)",
                    designation = "वरिष्ठ प्रवक्ता (PGT Physics)",
                    department = "Science",
                    subject = "भौतिक विज्ञान (Physics)",
                    qualification = "M.Sc. (Gold Medalist), Ph.D., B.Ed., CSIR-NET",
                    experience = "14 Years (BPSC PGT)",
                    phone = "9431024501"
                )
                registerTeacher(
                    name = "श्री आलोक रंजन तिवारी (Mr. Alok Ranjan Tiwari)",
                    designation = "कंप्यूटर साइंस प्रवक्ता (PGT Computer Science)",
                    department = "Science",
                    subject = "कंप्यूटर साइंस एवं स्मार्ट क्लास (ICT & Python)",
                    qualification = "M.C.A., B.Tech (CSE), BPSC TRE Qualified",
                    experience = "9 Years",
                    phone = "9431024508"
                )
            }
        } catch (_: Exception) {
            // Ignore seed errors safely
        }
    }
}
