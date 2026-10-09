package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ClassResultRecordItem
import com.example.model.*
import com.example.ui.components.SchoolWebFooter
import com.example.ui.components.SectionHeader
import com.example.ui.components.shareTextContent
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun StudentHubScreen(
    isHindi: Boolean,
    rollNumberQuery: String,
    searchedResult: StudentResultRecord?,
    resultSearchError: Boolean,
    allResults: List<StudentResultRecord>,
    cloudLiveResults: List<ClassResultRecordItem> = emptyList(),
    homeworkList: List<HomeworkItem>,
    completedHomeworkIds: Set<String>,
    savedRequests: List<LeaveOrFeedbackRequest>,
    onRollQueryChange: (String) -> Unit,
    onSearchResult: (String) -> Unit,
    onToggleHomework: (String) -> Unit,
    onSubmitRequest: (String, String, String, String) -> Unit,
    onNavigateTab: (PortalTab) -> Unit
) {
    var reqStudentName by remember { mutableStateOf("") }
    var reqClassRoll by remember { mutableStateOf("") }
    var reqType by remember { mutableStateOf("LEAVE") }
    var reqMessage by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("student_hub_screen_list"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. EXAM RESULT & DIGITAL MARKSHEET PORTAL
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(
                    badgeText = if (isHindi) "परीक्षा परिणाम एवं डिजिटल अंक-पत्र • रोल कोड: 73045" else "EXAM RESULT & DIGITAL MARKSHEET • CODE: 73045",
                    title = if (isHindi) "ऑनलाइन अंक-पत्र (Marksheet) पोर्टल" else "Student Result & Marksheet Lookup",
                    subtitle = if (isHindi) {
                        "रोल नंबर या नाम से खोजें अथवा नीचे दिए गए रोल नंबर चिप्स पर टैप करें"
                    } else {
                        "Search by Roll Number or Student Name, or tap any sample Roll Chip below"
                    }
                )

                // Search Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = rollNumberQuery,
                        onValueChange = onRollQueryChange,
                        label = {
                            Text(if (isHindi) "रोल नंबर या नाम (जैसे: 260101)" else "Roll No. or Name (e.g. 260101)")
                        },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("result_roll_input")
                    )

                    Button(
                        onClick = { onSearchResult(rollNumberQuery) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(54.dp)
                            .testTag("search_result_button")
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isHindi) "परिणाम" else "Check")
                    }
                }

                // Quick Roll Number Chips
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allResults.forEach { rec ->
                        FilterChip(
                            selected = searchedResult?.rollNo == rec.rollNo,
                            onClick = { onSearchResult(rec.rollNo) },
                            label = {
                                Text(
                                    "${rec.rollNo} • ${rec.studentName.get(isHindi).substringBefore(" (")}"
                                )
                            },
                            modifier = Modifier.testTag("roll_chip_${rec.rollNo}")
                        )
                    }
                }

                if (resultSearchError) {
                    Surface(
                        color = CrimsonUrgentBg,
                        contentColor = CrimsonUrgent,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isHindi) {
                                "इस रोल नंबर का कोई रिकॉर्ड नहीं मिला। कृपया 260101 से 260105 के बीच चुनें।"
                            } else {
                                "No record found for this Roll Number. Try Roll Numbers 260101 to 260105."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                if (searchedResult != null) {
                    DigitalMarksheetCard(record = searchedResult, isHindi = isHindi)
                }

                // Class-wise Live Published Results from Admin Panel
                val livePublished = remember(cloudLiveResults) {
                    cloudLiveResults.filter { it.isLive }
                }
                if (livePublished.isNotEmpty()) {
                    var selectedLiveClass by remember { mutableStateOf("ALL") }
                    val availableClasses = remember(livePublished) {
                        listOf("ALL") + livePublished.map { it.className }.distinct()
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    SectionHeader(
                        badgeText = if (isHindi) "क्लास-वाइज LIVE परिणाम • एडमिन द्वारा प्रकाशित" else "CLASS-WISE LIVE RESULTS • PUBLISHED BY ADMIN",
                        title = if (isHindi) "कक्षा-वार लाइव परीक्षा परिणाम (${livePublished.size} छात्र)" else "Class-wise Live Exam Results (${livePublished.size} Students)",
                        actionLabel = if (isHindi) "एडमिन कंट्रोल" else "Admin Control",
                        onActionClick = { onNavigateTab(PortalTab.ADMIN_PANEL) }
                    )

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableClasses.forEach { cls ->
                            FilterChip(
                                selected = selectedLiveClass == cls,
                                onClick = { selectedLiveClass = cls },
                                label = {
                                    Text(if (cls == "ALL") (if (isHindi) "सभी कक्षाएं" else "All Classes") else cls)
                                }
                            )
                        }
                    }

                    val shownLive = if (selectedLiveClass == "ALL") livePublished else livePublished.filter { it.className == selectedLiveClass }
                    shownLive.forEach { item ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = EmeraldSuccessBg,
                                        contentColor = EmeraldSuccess,
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = "● LIVE • ${item.className} • Roll: ${item.rollNo}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                    Text(
                                        text = "${item.totalMarks}/${item.maxMarks} (${String.format(Locale.US, "%.1f", item.percentage)}%)",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Text(
                                    text = "${item.studentName} • ${if (isHindi) "पिता:" else "Father:"} ${item.fatherName}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${item.division} — ${item.subjectBreakdown}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. DAILY HOMEWORK & ASSIGNMENT DIARY
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(
                    badgeText = if (isHindi) "दैनिक गृहकार्य एवं असाइनमेंट डायरी" else "DAILY HOMEWORK & LAB DIARY",
                    title = if (isHindi) "आज का गृहकार्य (${completedHomeworkIds.size}/${homeworkList.size} पूर्ण)" else "Student Homework Tracker (${completedHomeworkIds.size}/${homeworkList.size} Done)"
                )

                homeworkList.forEach { hw ->
                    val isDone = completedHomeworkIds.contains(hw.id)
                    Card(
                        onClick = { onToggleHomework(hw.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(
                            1.dp,
                            if (isDone) EmeraldSuccess.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Checkbox(
                                checked = isDone,
                                onCheckedChange = { onToggleHomework(hw.id) }
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${hw.classLevel} • ${hw.subject.get(isHindi)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = hw.dueDate.get(isHindi),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = hw.title.get(isHindi),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = hw.description.get(isHindi),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${if (isHindi) "शिक्षक:" else "By:"} ${hw.assignedBy.get(isHindi)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. STUDENT LEAVE APPLICATION & SUGGESTION BOX
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(
                    badgeText = if (isHindi) "छात्र सहायता प्रकोष्ठ • ई-अवकाश एवं सुझाव" else "STUDENT HELP DESK & LEAVE PORTAL",
                    title = if (isHindi) "ऑनलाइन अवकाश आवेदन / प्राचार्य सुझाव पेटी" else "Submit Leave Request or Feedback"
                )

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = reqType == "LEAVE",
                                onClick = { reqType = "LEAVE" },
                                label = { Text(if (isHindi) "अवकाश आवेदन (Leave)" else "Leave Application") }
                            )
                            FilterChip(
                                selected = reqType == "FEEDBACK",
                                onClick = { reqType = "FEEDBACK" },
                                label = { Text(if (isHindi) "सुझाव / समस्या (Feedback)" else "Feedback / Issue") }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = reqStudentName,
                                onValueChange = { reqStudentName = it },
                                label = { Text(if (isHindi) "विद्यार्थी का नाम" else "Student Name") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = reqClassRoll,
                                onValueChange = { reqClassRoll = it },
                                label = { Text(if (isHindi) "कक्षा एवं रोल नंबर" else "Class & Roll No.") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = reqMessage,
                            onValueChange = { reqMessage = it },
                            label = {
                                Text(
                                    if (isHindi) "अवकाश का कारण अथवा अपना सुझाव लिखें..."
                                    else "Write reason for leave or your suggestion..."
                                )
                            },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (reqStudentName.isNotBlank() && reqMessage.isNotBlank()) {
                                    onSubmitRequest(
                                        reqStudentName,
                                        reqClassRoll.ifBlank { "Class 12 • UMV Gangauli" },
                                        reqType,
                                        reqMessage
                                    )
                                    reqStudentName = ""
                                    reqClassRoll = ""
                                    reqMessage = ""
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("submit_leave_request_btn")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isHindi) "प्राचार्य कक्ष को भेजें" else "Submit to Principal's Office")
                        }
                    }
                }

                savedRequests.forEach { req ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${req.id} • ${req.studentName} (${req.classAndRoll})",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = req.dateSubmitted,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            Text(
                                text = req.message,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Text(
                                text = req.status,
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldSuccess,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            SchoolWebFooter(isHindi = isHindi, onNavigateTab = onNavigateTab)
        }
    }
}

@Composable
private fun DigitalMarksheetCard(
    record: StudentResultRecord,
    isHindi: Boolean
) {
    val context = LocalContext.current
    val formattedPercent = String.format(Locale.US, "%.1f%%", record.percentage)

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.5.dp, SaffronGoldSecondary),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("digital_marksheet_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Official Marksheet Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RoyalNavyPrimary)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (isHindi) "उच्च माध्यमिक विद्यालय गंगौली, सिमरी, बक्सर (बिहार)" else "UCHCH MADHYAMIK VIDYALAYA GANGAULI, SIMRI, BUXAR",
                    style = MaterialTheme.typography.titleMedium,
                    color = SaffronGoldBright,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${record.examSession} • BSEB Code: ${record.rollCode}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Student Info Grid
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        MarksheetMetaRow(
                            if (isHindi) "विद्यार्थी का नाम:" else "Student Name:",
                            record.studentName.get(isHindi)
                        )
                        MarksheetMetaRow(
                            if (isHindi) "रोल कोड / रोल नंबर:" else "Roll Code / Roll No.:",
                            "${record.rollCode} / ${record.rollNo}"
                        )
                        MarksheetMetaRow(
                            if (isHindi) "पिता / माता का नाम:" else "Parents:",
                            "${record.fatherName.get(isHindi)} / ${record.motherName.get(isHindi)}"
                        )
                        MarksheetMetaRow(
                            if (isHindi) "कक्षा एवं संकाय:" else "Class & Stream:",
                            record.classStream.get(isHindi)
                        )
                        MarksheetMetaRow(
                            if (isHindi) "वार्षिक उपस्थिति:" else "Attendance:",
                            "${record.attendancePercent}% (DBT Verified)"
                        )
                    }
                }

                // Subject Marks Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isHindi) "विषय (Subject)" else "Subject",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.8f)
                    )
                    Text(
                        text = if (isHindi) "सैद्धांतिक+प्रायोगिक" else "Th + Pr",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (isHindi) "कुल (100)" else "Total",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.7f)
                    )
                }

                record.subjects.forEach { subj ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = subj.subjectName.get(isHindi),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1.8f)
                        )
                        Text(
                            text = if (subj.practicalMarks > 0) "${subj.theoryMarks} + ${subj.practicalMarks}" else "${subj.theoryMarks} (Th)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${subj.total} (${subj.grade.substringBefore(" ")})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(0.7f)
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                }

                // Grand Total & Division Banner
                Surface(
                    color = EmeraldSuccessBg,
                    contentColor = EmeraldSuccess,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = record.division.get(isHindi),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${if (isHindi) "कुल प्राप्तांक:" else "Grand Total:"} ${record.totalMarksObtained} / ${record.maxTotalMarks}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Surface(
                            color = EmeraldSuccess,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = formattedPercent,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "${if (isHindi) "प्राचार्य टिप्पणी:" else "Principal's Remarks:"} ${record.remarks.get(isHindi)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedButton(
                    onClick = {
                        shareTextContent(
                            context = context,
                            title = "Marksheet - ${record.studentName.get(isHindi)} (Roll: ${record.rollNo})",
                            body = "UMV Gangauli, Simri, Buxar\nStudent: ${record.studentName.get(isHindi)}\nRoll Code: ${record.rollCode} | Roll No: ${record.rollNo}\nTotal Marks: ${record.totalMarksObtained}/${record.maxTotalMarks} ($formattedPercent)\nResult: ${record.division.get(isHindi)}"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isHindi) "डिजिटल अंक-पत्र शेयर करें" else "Share Digital Marksheet")
                }
            }
        }
    }
}

@Composable
private fun MarksheetMetaRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
