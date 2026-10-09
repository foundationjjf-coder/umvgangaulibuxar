package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PortalUiState(
    val isHindi: Boolean = true,
    val isDarkTheme: Boolean = false,
    val currentTab: PortalTab = PortalTab.HOME,
    val activeTickerIndex: Int = 0,

    // Cloud Firestore Real-Time State (Admin Panel + Live Portal)
    val cloudGalleryPhotos: List<GalleryPhotoItem> = emptyList(),
    val cloudAdmissions: List<AdmissionRecordItem> = emptyList(),
    val schoolAccountConfig: SchoolAccountConfigItem = SchoolAccountConfigItem(),
    val cloudLiveResults: List<ClassResultRecordItem> = emptyList(),
    val cloudTeachers: List<TeacherRecordItem> = emptyList(),

    // Academics Tab State
    val selectedTimetableClass: String = "CLASS_12_SCI",
    val selectedTimetableDay: String = "MON",
    val selectedStudyClassFilter: String = "ALL",
    val facultySearchQuery: String = "",
    val selectedFacultyDepartment: String = "ALL",

    // Admissions & Scholarship Tab State
    val savedAdmissions: List<AdmissionApplication> = emptyList(),
    val latestGeneratedAdmission: AdmissionApplication? = null,
    val checkedDocIds: Set<String> = emptySet(),
    val scholarshipFilterClass: String = "12",
    val scholarshipFilterGender: String = "FEMALE",
    val scholarshipFilterPercent: Int = 75,

    // Student Hub & Result State
    val rollNumberQuery: String = "260101",
    val searchedResult: StudentResultRecord? = null,
    val resultSearchError: Boolean = false,
    val completedHomeworkIds: Set<String> = emptySet(),
    val savedRequests: List<LeaveOrFeedbackRequest> = emptyList(),

    // Notices & Contact State
    val selectedNoticeCategory: String = "ALL",
    val noticeSearchQuery: String = "",
    val bookmarkedNoticeIds: Set<String> = emptySet(),
    val showBookmarkedOnly: Boolean = false,

    // Interactive Modals / Sheets
    val selectedFacilityModal: FacilityItem? = null,
    val selectedNoticeModal: NoticeItem? = null,
    val selectedStudyMaterialModal: StudyMaterial? = null,
    val showCampusTourModal: Boolean = false,
    val bannerToastMessage: String? = null
)

class SchoolViewModel(application: Application) : AndroidViewModel(application) {

    val repository = SchoolRepository(application.applicationContext)
    private val firestoreRepository = SchoolFirestoreRepository(application.applicationContext)
    private var firestoreSyncJob: Job? = null
    private var syncedUserId: String? = null

    private val _uiState = MutableStateFlow(
        PortalUiState(
            isHindi = repository.getLanguagePreference(),
            isDarkTheme = repository.getDarkThemePreference(),
            savedAdmissions = repository.getSavedAdmissions(),
            checkedDocIds = repository.getCheckedDocIds(),
            searchedResult = repository.studentResults.firstOrNull(),
            completedHomeworkIds = repository.getCompletedHomeworkIds(),
            savedRequests = repository.getSavedRequests(),
            bookmarkedNoticeIds = repository.getBookmarkedNoticeIds()
        )
    )
    val uiState: StateFlow<PortalUiState> = _uiState.asStateFlow()

    init {
        startLiveNoticeTicker()
    }

    fun onUserAuthenticated(userId: String) {
        if (syncedUserId == userId && firestoreSyncJob?.isActive == true) return
        syncedUserId = userId
        firestoreSyncJob?.cancel()

        firestoreSyncJob = viewModelScope.launch {
            firestoreRepository.seedInitialDataIfEmpty()

            launch {
                firestoreRepository.observeGalleryPhotos(userId)
                    .catch { e -> Log.w("SchoolVM", "Gallery stream error", e) }
                    .collect { list ->
                        _uiState.update { it.copy(cloudGalleryPhotos = list) }
                    }
            }

            launch {
                firestoreRepository.observeAdmissions(userId)
                    .catch { e -> Log.w("SchoolVM", "Admissions stream error", e) }
                    .collect { list ->
                        _uiState.update { it.copy(cloudAdmissions = list) }
                    }
            }

            launch {
                firestoreRepository.observeSchoolAccountConfig(userId)
                    .catch { e -> Log.w("SchoolVM", "Account config stream error", e) }
                    .collect { cfg ->
                        if (cfg != null) {
                            _uiState.update { it.copy(schoolAccountConfig = cfg) }
                        }
                    }
            }

            launch {
                firestoreRepository.observeLiveResults(userId)
                    .catch { e -> Log.w("SchoolVM", "Live results stream error", e) }
                    .collect { list ->
                        _uiState.update { it.copy(cloudLiveResults = list) }
                    }
            }

            launch {
                firestoreRepository.observeTeachers(userId)
                    .catch { e -> Log.w("SchoolVM", "Teachers stream error", e) }
                    .collect { list ->
                        _uiState.update { it.copy(cloudTeachers = list) }
                    }
            }
        }
    }

    fun onUserSignedOut() {
        firestoreSyncJob?.cancel()
        firestoreSyncJob = null
        syncedUserId = null
    }

    // ------------------------------------------------------------------------
    // ADMIN PANEL ACTIONS (CLOUD FIRESTORE)
    // ------------------------------------------------------------------------
    fun adminAddGalleryPhoto(
        occasionTheme: String,
        title: String,
        imageSource: String,
        dateLabel: String
    ) {
        viewModelScope.launch {
            val res = firestoreRepository.addGalleryPhoto(occasionTheme, title, imageSource, dateLabel)
            if (res.isSuccess) {
                showToast(
                    "📸 अवसर '$occasionTheme' की फोटो गैलरी में सफलतापूर्वक जोड़ी गई!",
                    "📸 Photo added under '$occasionTheme' in School Gallery!"
                )
            } else {
                showToast(
                    "फोटो जोड़ने में त्रुटि: ${res.exceptionOrNull()?.localizedMessage}",
                    "Failed to add photo: ${res.exceptionOrNull()?.localizedMessage}"
                )
            }
        }
    }

    fun adminDeleteGalleryPhoto(photoId: String) {
        viewModelScope.launch {
            val res = firestoreRepository.deleteGalleryPhoto(photoId)
            if (res.isSuccess) {
                showToast(
                    "🗑️ फोटो गैलरी से हटा दी गई।",
                    "🗑️ Photo removed from gallery."
                )
            }
        }
    }

    fun adminCreateAdmission(
        studentName: String,
        fatherName: String,
        classApplied: String,
        streamApplied: String,
        prevPercent: String,
        mobile: String,
        village: String
    ) {
        viewModelScope.launch {
            val res = firestoreRepository.createAdmission(
                studentName = studentName,
                fatherName = fatherName,
                classApplied = classApplied,
                streamApplied = streamApplied,
                prevPercentage = prevPercent,
                mobileNumber = mobile,
                village = village
            )
            if (res.isSuccess) {
                showToast(
                    "✅ नया नामांकन आवेदन (${res.getOrNull()}) पंजीकृत किया गया!",
                    "✅ New admission application (${res.getOrNull()}) created!"
                )
            }
        }
    }

    fun adminUpdateAdmissionStatus(admissionId: String, status: String) {
        viewModelScope.launch {
            val res = firestoreRepository.updateAdmissionStatus(admissionId, status)
            if (res.isSuccess) {
                val msgHi = if (status == "ACCEPTED") {
                    "✔ नामांकन आवेदन ($admissionId) स्वीकार (ACCEPTED) कर लिया गया!"
                } else {
                    "✖ नामांकन आवेदन ($admissionId) रद्द (CANCELLED) कर दिया गया।"
                }
                val msgEn = if (status == "ACCEPTED") {
                    "✔ Admission ($admissionId) marked as ACCEPTED!"
                } else {
                    "✖ Admission ($admissionId) marked as CANCELLED."
                }
                showToast(msgHi, msgEn)
            }
        }
    }

    fun adminDeleteAdmission(admissionId: String) {
        viewModelScope.launch {
            firestoreRepository.deleteAdmission(admissionId)
            showToast("नामांकन रिकॉर्ड हटा दिया गया।", "Admission record deleted.")
        }
    }

    fun adminSaveSchoolAccountConfig(
        holderName: String,
        bankName: String,
        accountNumber: String,
        ifscCode: String,
        upiId: String,
        qrTitle: String,
        qrImageUri: String
    ) {
        viewModelScope.launch {
            val res = firestoreRepository.saveSchoolAccountConfig(
                accountHolderName = holderName,
                bankName = bankName,
                accountNumber = accountNumber,
                ifscCode = ifscCode,
                upiId = upiId,
                qrCodeTitle = qrTitle,
                qrCustomImageUri = qrImageUri
            )
            if (res.isSuccess) {
                showToast(
                    "🏦 विद्यालय बैंक खाता एवं पेमेंट QR कोड सफलतापूर्वक अपडेट हो गया!",
                    "🏦 School Bank Account & Payment QR Code updated!"
                )
            }
        }
    }

    fun adminPublishClassResult(
        rollNo: String,
        studentName: String,
        fatherName: String,
        className: String,
        totalMarks: Int,
        maxMarks: Int,
        division: String,
        subjectBreakdown: String,
        isLive: Boolean
    ) {
        viewModelScope.launch {
            val res = firestoreRepository.publishClassResult(
                rollNo = rollNo,
                studentName = studentName,
                fatherName = fatherName,
                className = className,
                totalMarks = totalMarks,
                maxMarks = maxMarks,
                division = division,
                subjectBreakdown = subjectBreakdown,
                isLive = isLive
            )
            if (res.isSuccess) {
                showToast(
                    "📊 $className (Roll: $rollNo) का रिजल्ट पोर्टल पर प्रकाशित कर दिया गया!",
                    "📊 Result for $className (Roll: $rollNo) published!"
                )
            }
        }
    }

    fun adminToggleResultLive(resultId: String, isLive: Boolean) {
        viewModelScope.launch {
            val res = firestoreRepository.toggleResultLiveStatus(resultId, isLive)
            if (res.isSuccess) {
                showToast(
                    if (isLive) "📡 छात्र परिणाम अब पोर्टल पर LIVE है!" else "परिणाम पोर्टल से छिपा (Hidden) दिया गया।",
                    if (isLive) "📡 Result is now LIVE on Student Portal!" else "Result hidden from Student Portal."
                )
            }
        }
    }

    fun adminSetClassAllLive(className: String, isLive: Boolean) {
        viewModelScope.launch {
            val res = firestoreRepository.setClassResultsLiveState(className, isLive)
            if (res.isSuccess) {
                val count = res.getOrDefault(0)
                showToast(
                    if (isLive) "📡 $className के सभी $count परिणाम LIVE कर दिए गए!" else "$className के सभी $count परिणाम रोक (Hidden) दिए गए।",
                    if (isLive) "📡 All $count results for $className are now LIVE!" else "All $count results for $className hidden."
                )
            }
        }
    }

    fun adminDeleteClassResult(resultId: String) {
        viewModelScope.launch {
            firestoreRepository.deleteClassResult(resultId)
            showToast("रिजल्ट रिकॉर्ड हटा दिया गया।", "Result record removed.")
        }
    }

    fun adminRegisterTeacher(
        name: String,
        designation: String,
        department: String,
        subject: String,
        qualification: String,
        experience: String,
        phone: String
    ) {
        viewModelScope.launch {
            val res = firestoreRepository.registerTeacher(
                name = name,
                designation = designation,
                department = department,
                subject = subject,
                qualification = qualification,
                experience = experience,
                phone = phone
            )
            if (res.isSuccess) {
                showToast(
                    "👨‍🏫 शिक्षक '$name' का पंजीकरण सफलतापूर्वक संपन्न हुआ!",
                    "👨‍🏫 Teacher '$name' registered in school directory!"
                )
            }
        }
    }

    fun adminDeleteTeacher(teacherId: String) {
        viewModelScope.launch {
            firestoreRepository.deleteTeacher(teacherId)
            showToast("शिक्षक रिकॉर्ड हटा दिया गया।", "Teacher record removed.")
        }
    }

    // ------------------------------------------------------------------------
    // GENERAL PORTAL ACTIONS
    // ------------------------------------------------------------------------
    private fun startLiveNoticeTicker() {
        viewModelScope.launch {
            while (true) {
                delay(4500L)
                _uiState.update { state ->
                    val nextIdx = (state.activeTickerIndex + 1) % repository.liveTickerNotices.size
                    state.copy(activeTickerIndex = nextIdx)
                }
            }
        }
    }

    fun toggleLanguage() {
        _uiState.update { current ->
            val updated = !current.isHindi
            repository.setLanguagePreference(updated)
            current.copy(isHindi = updated)
        }
    }

    fun toggleDarkTheme() {
        _uiState.update { current ->
            val updated = !current.isDarkTheme
            repository.setDarkThemePreference(updated)
            current.copy(isDarkTheme = updated)
        }
    }

    fun selectTab(tab: PortalTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun setTimetableClass(classId: String) {
        _uiState.update { it.copy(selectedTimetableClass = classId) }
    }

    fun setTimetableDay(dayId: String) {
        _uiState.update { it.copy(selectedTimetableDay = dayId) }
    }

    fun setStudyClassFilter(filter: String) {
        _uiState.update { it.copy(selectedStudyClassFilter = filter) }
    }

    fun setFacultySearchQuery(query: String) {
        _uiState.update { it.copy(facultySearchQuery = query) }
    }

    fun setFacultyDepartment(dept: String) {
        _uiState.update { it.copy(selectedFacultyDepartment = dept) }
    }

    fun openFacilityModal(facility: FacilityItem?) {
        _uiState.update { it.copy(selectedFacilityModal = facility) }
    }

    fun openNoticeModal(notice: NoticeItem?) {
        _uiState.update { it.copy(selectedNoticeModal = notice) }
    }

    fun openStudyMaterialModal(material: StudyMaterial?) {
        _uiState.update { it.copy(selectedStudyMaterialModal = material) }
    }

    fun setShowCampusTourModal(show: Boolean) {
        _uiState.update { it.copy(showCampusTourModal = show) }
    }

    fun dismissAdmissionReceiptModal() {
        _uiState.update { it.copy(latestGeneratedAdmission = null) }
    }

    fun clearBannerToast() {
        _uiState.update { it.copy(bannerToastMessage = null) }
    }

    fun showToast(msgHi: String, msgEn: String) {
        val msg = if (_uiState.value.isHindi) msgHi else msgEn
        _uiState.update { it.copy(bannerToastMessage = msg) }
    }

    fun toggleDocumentCheck(docId: String) {
        _uiState.update { state ->
            val updated = state.checkedDocIds.toMutableSet()
            if (updated.contains(docId)) updated.remove(docId) else updated.add(docId)
            repository.saveCheckedDocIds(updated)
            state.copy(checkedDocIds = updated)
        }
    }

    fun updateScholarshipFilter(classStr: String? = null, gender: String? = null, percent: Int? = null) {
        _uiState.update { state ->
            state.copy(
                scholarshipFilterClass = classStr ?: state.scholarshipFilterClass,
                scholarshipFilterGender = gender ?: state.scholarshipFilterGender,
                scholarshipFilterPercent = percent ?: state.scholarshipFilterPercent
            )
        }
    }

    fun submitAdmissionApplication(
        studentName: String,
        fatherName: String,
        motherName: String,
        classApplied: String,
        streamApplied: String,
        prevPercentage: String,
        mobileNumber: String,
        villagePanchayat: String,
        category: String,
        aadhaarLast4: String
    ) {
        val cleanPercent = if (prevPercentage.endsWith("%")) prevPercentage else "$prevPercentage%"
        // Also persist to Cloud Firestore so Admin can Accept/Cancel it in Admin Panel
        viewModelScope.launch {
            firestoreRepository.createAdmission(
                studentName = studentName.trim(),
                fatherName = fatherName.trim(),
                classApplied = classApplied,
                streamApplied = streamApplied,
                prevPercentage = cleanPercent,
                mobileNumber = mobileNumber.trim(),
                village = villagePanchayat
            )
        }

        val randomNum = (2000..9899).random()
        val appId = "UMVG-2026-$randomNum"
        val newApp = AdmissionApplication(
            applicationId = appId,
            studentName = studentName.trim(),
            fatherName = fatherName.trim(),
            motherName = motherName.trim().ifEmpty { "श्रीमती अभिभावक / Guardian" },
            classApplied = classApplied,
            streamApplied = streamApplied,
            prevPercentage = cleanPercent,
            mobileNumber = mobileNumber.trim(),
            villagePanchayat = villagePanchayat,
            category = category,
            aadhaarLast4 = aadhaarLast4.ifBlank { "XXXX" },
            submittedDate = "08 Oct 2026",
            status = if (_uiState.value.isHindi) "पंजीकृत • एडमिन स्वीकृति हेतु प्रेषित" else "Submitted • Pending Admin Approval"
        )
        _uiState.update { state ->
            val updatedList = listOf(newApp) + state.savedAdmissions
            repository.saveAdmissions(updatedList)
            state.copy(
                savedAdmissions = updatedList,
                latestGeneratedAdmission = newApp,
                bannerToastMessage = if (state.isHindi) {
                    "बधाई! नामांकन आवेदन क्रमांक $appId पंजीकृत एवं एडमिन पैनल में भेज दिया गया!"
                } else {
                    "Success! Admission Application $appId submitted to Admin Panel!"
                }
            )
        }
    }

    fun updateRollNumberQuery(roll: String) {
        _uiState.update { it.copy(rollNumberQuery = roll, resultSearchError = false) }
    }

    fun searchResultByRoll(roll: String = _uiState.value.rollNumberQuery) {
        val cleanRoll = roll.trim()
        val match = repository.studentResults.find {
            it.rollNo.equals(cleanRoll, ignoreCase = true) ||
                it.studentName.hi.contains(cleanRoll, ignoreCase = true) ||
                it.studentName.en.contains(cleanRoll, ignoreCase = true)
        }
        _uiState.update {
            it.copy(
                rollNumberQuery = cleanRoll,
                searchedResult = match,
                resultSearchError = match == null
            )
        }
    }

    fun toggleHomeworkCompleted(hwId: String) {
        _uiState.update { state ->
            val updated = state.completedHomeworkIds.toMutableSet()
            if (updated.contains(hwId)) updated.remove(hwId) else updated.add(hwId)
            repository.saveCompletedHomeworkIds(updated)
            state.copy(completedHomeworkIds = updated)
        }
    }

    fun submitStudentRequest(
        studentName: String,
        classAndRoll: String,
        requestType: String,
        message: String
    ) {
        val reqId = "REQ-${(101..999).random()}"
        val newReq = LeaveOrFeedbackRequest(
            id = reqId,
            studentName = studentName.trim(),
            classAndRoll = classAndRoll.trim(),
            requestType = requestType,
            message = message.trim(),
            dateSubmitted = "08 Oct 2026",
            status = if (_uiState.value.isHindi) "प्राचार्य कार्यालय में प्राप्त (विचाराधीन)" else "Received at Principal's Desk"
        )
        _uiState.update { state ->
            val updatedList = listOf(newReq) + state.savedRequests
            repository.saveRequests(updatedList)
            state.copy(
                savedRequests = updatedList,
                bannerToastMessage = if (state.isHindi) {
                    "आपका आवेदन/सुझाव ($reqId) प्राचार्य कक्ष को भेज दिया गया है।"
                } else {
                    "Your request ($reqId) has been submitted to the Principal's Desk."
                }
            )
        }
    }

    fun setNoticeCategory(category: String) {
        _uiState.update { it.copy(selectedNoticeCategory = category) }
    }

    fun setNoticeSearchQuery(query: String) {
        _uiState.update { it.copy(noticeSearchQuery = query) }
    }

    fun toggleBookmarkNotice(noticeId: String) {
        _uiState.update { state ->
            val updated = state.bookmarkedNoticeIds.toMutableSet()
            if (updated.contains(noticeId)) updated.remove(noticeId) else updated.add(noticeId)
            repository.saveBookmarkedNoticeIds(updated)
            state.copy(bookmarkedNoticeIds = updated)
        }
    }

    fun toggleShowBookmarkedOnly() {
        _uiState.update { it.copy(showBookmarkedOnly = !it.showBookmarkedOnly) }
    }
}
