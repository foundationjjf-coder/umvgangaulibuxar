package com.example.model

import androidx.annotation.DrawableRes

data class BilingualText(
    val hi: String,
    val en: String
) {
    fun get(isHindi: Boolean): String = if (isHindi) hi else en
}

enum class PortalTab(
    val route: String,
    val label: BilingualText
) {
    HOME("home", BilingualText("मुख्य पृष्ठ", "Home")),
    ACADEMICS("academics", BilingualText("शैक्षणिक", "Academics")),
    ADMISSIONS("admissions", BilingualText("नामांकन", "Admissions")),
    STUDENT_HUB("student_hub", BilingualText("छात्र पोर्टल", "Student Hub")),
    NOTICES_CONTACT("notices_contact", BilingualText("सूचना व संपर्क", "Notices")),
    ADMIN_PANEL("admin_panel", BilingualText("एडमिन पैनल", "Admin Panel"))
}

data class FacilityItem(
    val id: String,
    val title: BilingualText,
    val subtitle: BilingualText,
    val description: BilingualText,
    val highlights: List<BilingualText>,
    val timing: BilingualText,
    val inCharge: BilingualText,
    @param:DrawableRes val imageRes: Int? = null,
    val badgeText: BilingualText
)

data class AcademicStream(
    val id: String,
    val classRange: BilingualText,
    val streamTitle: BilingualText,
    val code: String,
    val seats: Int,
    val medium: BilingualText,
    val coreSubjects: List<BilingualText>,
    val electiveSubjects: List<BilingualText>,
    val careerPathways: BilingualText,
    val hodName: BilingualText
)

data class TimetableSlot(
    val period: Int,
    val timeRange: String,
    val subject: BilingualText,
    val teacher: BilingualText,
    val room: BilingualText,
    val isPracticalOrSmartClass: Boolean = false
)

data class ClassRoutineDay(
    val classId: String,
    val dayId: String, // MON, TUE, WED, THU, FRI, SAT
    val slots: List<TimetableSlot>
)

data class FacultyMember(
    val id: String,
    val name: BilingualText,
    val designation: BilingualText,
    val department: String, // Science, Arts, Secondary, Languages, Sports
    val subject: BilingualText,
    val qualification: BilingualText,
    val experience: BilingualText,
    val message: BilingualText,
    val phoneExt: String
)

data class TopperStudent(
    val id: String,
    val name: BilingualText,
    val fatherName: BilingualText,
    val streamClass: BilingualText,
    val percentage: String,
    val marksObtained: String,
    val achievementBadge: BilingualText,
    val village: BilingualText,
    val year: String,
    val rollNo: String
)

data class SubjectMark(
    val subjectName: BilingualText,
    val maxMarks: Int = 100,
    val theoryMarks: Int,
    val practicalMarks: Int,
    val passMarks: Int = 30
) {
    val total: Int get() = theoryMarks + practicalMarks
    val grade: String
        get() = when {
            total >= 80 -> "A+ (Distinction)"
            total >= 60 -> "A (First)"
            total >= 45 -> "B (Second)"
            total >= 30 -> "C (Pass)"
            else -> "D"
        }
}

data class StudentResultRecord(
    val rollCode: String = "73045",
    val rollNo: String,
    val studentName: BilingualText,
    val fatherName: BilingualText,
    val motherName: BilingualText,
    val classStream: BilingualText,
    val examSession: String,
    val village: BilingualText,
    val attendancePercent: Int,
    val subjects: List<SubjectMark>,
    val remarks: BilingualText
) {
    val totalMarksObtained: Int get() = subjects.sumOf { it.total }
    val maxTotalMarks: Int get() = subjects.sumOf { it.maxMarks }
    val percentage: Double
        get() = if (maxTotalMarks > 0) (totalMarksObtained * 100.0) / maxTotalMarks else 0.0
    val division: BilingualText
        get() = when {
            percentage >= 60.0 -> BilingualText("प्रथम श्रेणी (ससम्मान / 1st Division)", "1st Division with Honors")
            percentage >= 45.0 -> BilingualText("द्वितीय श्रेणी (2nd Division)", "2nd Division")
            percentage >= 30.0 -> BilingualText("तृतीय श्रेणी (उत्तीर्ण)", "3rd Division (Pass)")
            else -> BilingualText("पुनः प्रयास आवश्यक", "Needs Improvement")
        }
}

data class NoticeItem(
    val id: String,
    val refNo: String,
    val title: BilingualText,
    val category: String, // ALL, EXAM, SCHOLARSHIP, ADMISSION, CULTURAL
    val categoryLabel: BilingualText,
    val date: BilingualText,
    val isUrgent: Boolean,
    val summary: BilingualText,
    val fullCircular: BilingualText,
    val issuedBy: BilingualText
)

data class ScholarshipScheme(
    val id: String,
    val schemeName: BilingualText,
    val amountBadge: BilingualText,
    val targetClasses: List<String>, // "9", "10", "11", "12"
    val targetGender: String, // "ALL", "FEMALE"
    val minPercentage: Int,
    val description: BilingualText,
    val requiredDocs: List<BilingualText>,
    val portalName: String
)

data class AdmissionApplication(
    val applicationId: String,
    val studentName: String,
    val fatherName: String,
    val motherName: String,
    val classApplied: String,
    val streamApplied: String,
    val prevPercentage: String,
    val mobileNumber: String,
    val villagePanchayat: String,
    val category: String,
    val aadhaarLast4: String,
    val submittedDate: String,
    val status: String
)

data class StudyMaterial(
    val id: String,
    val title: BilingualText,
    val subject: BilingualText,
    val classLevel: String, // "Class 9-10", "Class 11-12 Science", "Class 11-12 Arts"
    val materialType: BilingualText,
    val chaptersOrPages: BilingualText,
    val keyTopics: List<BilingualText>,
    val summaryNotes: BilingualText
)

data class HomeworkItem(
    val id: String,
    val classLevel: String,
    val subject: BilingualText,
    val title: BilingualText,
    val description: BilingualText,
    val assignedBy: BilingualText,
    val dueDate: BilingualText
)

data class LeaveOrFeedbackRequest(
    val id: String,
    val studentName: String,
    val classAndRoll: String,
    val requestType: String, // "LEAVE", "FEEDBACK", "VISIT"
    val message: String,
    val dateSubmitted: String,
    val status: String
)

data class AcademicEvent(
    val id: String,
    val monthBadge: String,
    val dayNum: String,
    val title: BilingualText,
    val subtitle: BilingualText,
    val categoryBadge: BilingualText
)
