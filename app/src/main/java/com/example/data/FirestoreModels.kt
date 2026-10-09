package com.example.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import org.json.JSONArray
import org.json.JSONObject

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

fun handleFirestoreError(
    exception: Exception,
    operationType: OperationType,
    path: String?
): String {
    val auth = try {
        FirebaseAuth.getInstance()
    } catch (_: Exception) {
        null
    }
    val currentUser = auth?.currentUser

    val providerInfoList = currentUser?.providerData?.map { provider ->
        JSONObject().apply {
            put("providerId", provider.providerId)
            put("email", provider.email)
        }
    } ?: emptyList()

    val authInfoJson = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("emailVerified", currentUser?.isEmailVerified)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", JSONArray(providerInfoList))
    }

    val errorInfoJson = JSONObject().apply {
        put("error", exception.message ?: exception.toString())
        put("operationType", operationType.value)
        put("path", path)
        put("authInfo", authInfoJson)
    }

    val jsonString = errorInfoJson.toString()
    Log.e("FirestoreError", "Firestore Error: $jsonString")
    return jsonString
}

data class GalleryPhotoItem(
    val id: String = "",
    val userId: String = "",
    val occasionTheme: String = "",
    val title: String = "",
    val imageSource: String = "PRESET_CAMPUS",
    val dateLabel: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "id" to id,
        "userId" to userId,
        "occasionTheme" to occasionTheme,
        "title" to title,
        "imageSource" to imageSource,
        "dateLabel" to dateLabel,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

data class AdmissionRecordItem(
    val id: String = "",
    val userId: String = "",
    val studentName: String = "",
    val fatherName: String = "",
    val classApplied: String = "",
    val streamApplied: String = "",
    val prevPercentage: String = "",
    val mobileNumber: String = "",
    val village: String = "",
    val status: String = "PENDING", // PENDING, ACCEPTED, CANCELLED
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "id" to id,
        "userId" to userId,
        "studentName" to studentName,
        "fatherName" to fatherName,
        "classApplied" to classApplied,
        "streamApplied" to streamApplied,
        "prevPercentage" to prevPercentage,
        "mobileNumber" to mobileNumber,
        "village" to village,
        "status" to status,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

data class SchoolAccountConfigItem(
    val id: String = "",
    val userId: String = "",
    val accountHolderName: String = "VSS Uchch Madhyamik Vidyalaya Gangauli",
    val bankName: String = "State Bank of India (SBI) - Simri Branch, Buxar",
    val accountNumber: String = "38492010485",
    val ifscCode: String = "SBIN0004821",
    val upiId: String = "umvgangauli73045@sbi",
    val qrCodeTitle: String = "Official Admission & Development Fee QR (BSEB Code: 73045)",
    val qrCustomImageUri: String = "DEFAULT_QR",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "id" to id,
        "userId" to userId,
        "accountHolderName" to accountHolderName,
        "bankName" to bankName,
        "accountNumber" to accountNumber,
        "ifscCode" to ifscCode,
        "upiId" to upiId,
        "qrCodeTitle" to qrCodeTitle,
        "qrCustomImageUri" to qrCustomImageUri,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )

    fun toUpdateMap(): Map<String, Any> = mapOf(
        "accountHolderName" to accountHolderName,
        "bankName" to bankName,
        "accountNumber" to accountNumber,
        "ifscCode" to ifscCode,
        "upiId" to upiId,
        "qrCodeTitle" to qrCodeTitle,
        "qrCustomImageUri" to qrCustomImageUri,
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

data class ClassResultRecordItem(
    val id: String = "",
    val userId: String = "",
    val rollNo: String = "",
    val studentName: String = "",
    val fatherName: String = "",
    val className: String = "",
    val totalMarks: Long = 0L,
    val maxMarks: Long = 500L,
    val division: String = "1st Division",
    val subjectBreakdown: String = "",
    val isLive: Boolean = true,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    val percentage: Double
        get() = if (maxMarks > 0L) (totalMarks.toDouble() * 100.0) / maxMarks.toDouble() else 0.0

    fun toCreateMap(): Map<String, Any> = mapOf(
        "id" to id,
        "userId" to userId,
        "rollNo" to rollNo,
        "studentName" to studentName,
        "fatherName" to fatherName,
        "className" to className,
        "totalMarks" to totalMarks,
        "maxMarks" to maxMarks,
        "division" to division,
        "subjectBreakdown" to subjectBreakdown,
        "isLive" to isLive,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

data class TeacherRecordItem(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val designation: String = "",
    val department: String = "",
    val subject: String = "",
    val qualification: String = "",
    val experience: String = "",
    val phone: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "id" to id,
        "userId" to userId,
        "name" to name,
        "designation" to designation,
        "department" to department,
        "subject" to subject,
        "qualification" to qualification,
        "experience" to experience,
        "phone" to phone,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}
