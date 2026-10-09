package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AdmissionRecordItem
import com.example.data.SchoolAccountConfigItem
import com.example.model.*
import com.example.ui.components.SchoolWebFooter
import com.example.ui.components.SectionHeader
import com.example.ui.components.shareTextContent
import com.example.ui.theme.*

@Composable
fun AdmissionsScreen(
    isHindi: Boolean,
    savedAdmissions: List<AdmissionApplication>,
    cloudAdmissions: List<AdmissionRecordItem> = emptyList(),
    schoolAccountConfig: SchoolAccountConfigItem = SchoolAccountConfigItem(),
    latestGeneratedAdmission: AdmissionApplication?,
    docChecklist: List<Pair<String, BilingualText>>,
    checkedDocIds: Set<String>,
    villages: List<String>,
    scholarshipSchemes: List<ScholarshipScheme>,
    scholarshipFilterClass: String,
    scholarshipFilterGender: String,
    scholarshipFilterPercent: Int,
    onSubmitAdmission: (
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
    ) -> Unit,
    onDismissReceiptModal: () -> Unit,
    onToggleDocCheck: (String) -> Unit,
    onUpdateScholarshipFilter: (String?, String?, Int?) -> Unit,
    onNavigateTab: (PortalTab) -> Unit
) {
    var studentName by remember { mutableStateOf("") }
    var fatherName by remember { mutableStateOf("") }
    var motherName by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf("Class 11 (+2 Intermediate)") }
    var selectedStream by remember { mutableStateOf("Science (I.Sc. - PCM/PCB)") }
    var prevPercent by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var selectedVillage by remember { mutableStateOf(villages.firstOrNull() ?: "Gangauli (गंगौली)") }
    var selectedCategory by remember { mutableStateOf("General / EWS") }
    var aadhaarLast4 by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf<String?>(null) }

    val eligibleScholarships = remember(
        scholarshipSchemes,
        scholarshipFilterClass,
        scholarshipFilterGender,
        scholarshipFilterPercent
    ) {
        scholarshipSchemes.filter { scheme ->
            val classOk = scheme.targetClasses.contains(scholarshipFilterClass)
            val genderOk = scheme.targetGender == "ALL" || scheme.targetGender == scholarshipFilterGender
            val percentOk = scholarshipFilterPercent >= scheme.minPercentage
            classOk && genderOk && percentOk
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admissions_screen_list"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. ONLINE ADMISSION FORM 2026-27
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(
                    badgeText = if (isHindi) "ऑनलाइन नामांकन पोर्टल • सत्र 2026-27" else "ONLINE ADMISSION PORTAL • 2026-27",
                    title = if (isHindi) "उच्च माध्यमिक विद्यालय गंगौली नामांकन फॉर्म" else "Digital Admission Application Form",
                    subtitle = if (isHindi) {
                        "कक्षा 9वीं से 12वीं में प्रवेश हेतु विवरण भरें और तुरंत आधिकारिक प्रोविजनल नामांकन टोकन प्राप्त करें"
                    } else {
                        "Fill in student details below to generate an instant Provisional Admission Token for UMV Gangauli"
                    }
                )

                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    border = BorderStroke(1.dp, SaffronGoldSecondary.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Class Selection Chips
                        Text(
                            text = if (isHindi) "1. कक्षा एवं संकाय चुनें (Select Class & Stream):" else "1. Select Class & Stream:",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        val classList = listOf(
                            "Class 9 (Secondary)",
                            "Class 10 (Transfer)",
                            "Class 11 (+2 Intermediate)",
                            "Class 12 (+2 Direct)"
                        )
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            classList.forEach { cls ->
                                FilterChip(
                                    selected = selectedClass == cls,
                                    onClick = { selectedClass = cls },
                                    label = { Text(cls) }
                                )
                            }
                        }

                        val streamList = listOf(
                            "Science (I.Sc. - PCM/PCB)",
                            "Arts & Humanities (I.A.)",
                            "Commerce & IT (I.Com.)",
                            "Secondary General (Matric)"
                        )
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            streamList.forEach { st ->
                                FilterChip(
                                    selected = selectedStream == st,
                                    onClick = { selectedStream = st },
                                    label = { Text(st) }
                                )
                            }
                        }

                        // Student & Parent Inputs
                        OutlinedTextField(
                            value = studentName,
                            onValueChange = {
                                studentName = it
                                formError = null
                            },
                            label = {
                                Text(if (isHindi) "विद्यार्थी का पूरा नाम (Student Full Name) *" else "Student Full Name *")
                            },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admission_student_name_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = fatherName,
                                onValueChange = {
                                    fatherName = it
                                    formError = null
                                },
                                label = { Text(if (isHindi) "पिता का नाम *" else "Father's Name *") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("admission_father_name_input")
                            )

                            OutlinedTextField(
                                value = motherName,
                                onValueChange = { motherName = it },
                                label = { Text(if (isHindi) "माता का नाम" else "Mother's Name") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = prevPercent,
                                onValueChange = { prevPercent = it },
                                label = { Text(if (isHindi) "पिछली कक्षा के % अंक *" else "Prev. Marks % *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("admission_percent_input")
                            )

                            OutlinedTextField(
                                value = mobileNumber,
                                onValueChange = {
                                    if (it.length <= 10) mobileNumber = it
                                    formError = null
                                },
                                label = { Text(if (isHindi) "मोबाइल नंबर (10 अंक) *" else "Mobile No. *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("admission_mobile_input")
                            )
                        }

                        // Village Selector Chips (Simri Block Villages)
                        Text(
                            text = if (isHindi) "2. ग्राम / पंचायत चुनें (Simri Block Village):" else "2. Select Village / Panchayat:",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            villages.forEach { v ->
                                AssistChip(
                                    onClick = { selectedVillage = v },
                                    label = {
                                        Text(
                                            text = v,
                                            fontWeight = if (selectedVillage == v) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = if (selectedVillage == v) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                                    )
                                )
                            }
                        }

                        // Category & Aadhaar Last 4
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val categories = listOf("General / EWS", "BC / EBC", "SC / ST")
                            Column(modifier = Modifier.weight(1.3f)) {
                                Text(
                                    text = if (isHindi) "कोटिक्रम (Category):" else "Category:",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Row(
                                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    categories.forEach { cat ->
                                        FilterChip(
                                            selected = selectedCategory == cat,
                                            onClick = { selectedCategory = cat },
                                            label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = aadhaarLast4,
                                onValueChange = { if (it.length <= 4) aadhaarLast4 = it },
                                label = { Text(if (isHindi) "आधार के अंतिम 4 अंक" else "Aadhaar Last 4") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(0.9f)
                            )
                        }

                        if (formError != null) {
                            Surface(
                                color = CrimsonUrgentBg,
                                contentColor = CrimsonUrgent,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = formError ?: "",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (studentName.isBlank() || fatherName.isBlank() || mobileNumber.length < 10) {
                                    formError = if (isHindi) {
                                        "कृपया विद्यार्थी का नाम, पिता का नाम एवं 10 अंकों का वैध मोबाइल नंबर दर्ज करें।"
                                    } else {
                                        "Please enter Student Name, Father's Name, and a valid 10-digit Mobile Number."
                                    }
                                } else {
                                    onSubmitAdmission(
                                        studentName,
                                        fatherName,
                                        motherName,
                                        selectedClass,
                                        selectedStream,
                                        prevPercent.ifBlank { "82.0%" },
                                        mobileNumber,
                                        selectedVillage,
                                        selectedCategory,
                                        aadhaarLast4
                                    )
                                    studentName = ""
                                    fatherName = ""
                                    motherName = ""
                                    prevPercent = ""
                                    mobileNumber = ""
                                    aadhaarLast4 = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RoyalNavyPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("submit_admission_button")
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "आवेदन सबमिट करें एवं नामांकन टोकन पाएं" else "Submit Application & Generate Admission Token",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2. REGISTERED ADMISSION TOKENS
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SectionHeader(
                    badgeText = if (isHindi) "पंजीकृत नामांकन टोकन" else "REGISTERED ADMISSION TOKENS",
                    title = if (isHindi) "आपके सबमिट किए गए आवेदन (${savedAdmissions.size})" else "Your Submitted Applications (${savedAdmissions.size})"
                )

                savedAdmissions.forEach { app ->
                    AdmissionTokenCard(app = app, isHindi = isHindi)
                }
            }
        }

        // 3. INTERACTIVE BIHAR SCHOLARSHIP ELIGIBILITY CALCULATOR
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(
                    badgeText = if (isHindi) "बिहार सरकार छात्रवृत्ति पात्रता कैलकुलेटर" else "BIHAR GOVT SCHOLARSHIP CALCULATOR",
                    title = if (isHindi) "जानें आपको कौन-कौन सी सरकारी योजनाएं मिलेंगी" else "Check Your DBT & Merit Grant Eligibility",
                    subtitle = if (isHindi) {
                        "नीचे अपनी कक्षा, लिंग और अंक चुनें — पात्र योजनाएं तुरंत देखें"
                    } else {
                        "Select your Class, Gender, and Marks % below to see eligible Bihar schemes"
                    }
                )

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isHindi) "कक्षा चुनें (Class):" else "Select Class:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("9", "10", "11", "12").forEach { cls ->
                                FilterChip(
                                    selected = scholarshipFilterClass == cls,
                                    onClick = { onUpdateScholarshipFilter(cls, null, null) },
                                    label = { Text(if (isHindi) "कक्षा $cls वीं" else "Class $cls") }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = scholarshipFilterGender == "FEMALE",
                                    onClick = { onUpdateScholarshipFilter(null, "FEMALE", null) },
                                    label = { Text(if (isHindi) "छात्रा (Girl)" else "Girl Student") }
                                )
                                FilterChip(
                                    selected = scholarshipFilterGender == "ALL",
                                    onClick = { onUpdateScholarshipFilter(null, "ALL", null) },
                                    label = { Text(if (isHindi) "छात्र (Boy)" else "Boy Student") }
                                )
                            }

                            Text(
                                text = "${if (isHindi) "प्राप्तांक:" else "Marks:"} $scholarshipFilterPercent%",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Slider(
                            value = scholarshipFilterPercent.toFloat(),
                            onValueChange = { onUpdateScholarshipFilter(null, null, it.toInt()) },
                            valueRange = 35f..98f
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        Text(
                            text = if (isHindi) {
                                "पात्र सरकारी योजनाएं (${eligibleScholarships.size} उपलब्ध):"
                            } else {
                                "Eligible Government Schemes (${eligibleScholarships.size} Matched):"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            color = EmeraldSuccess,
                            fontWeight = FontWeight.Bold
                        )

                        eligibleScholarships.forEach { scheme ->
                            ScholarshipSchemeCard(scheme = scheme, isHindi = isHindi)
                        }
                    }
                }
            }
        }

        // 3B. OFFICIAL SCHOOL BANK ACCOUNT & PAYMENT QR CODE + CLOUD ADMISSION STATUS
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(
                    badgeText = if (isHindi) "नामांकन शुल्क एवं विद्यालय विकास कोष • पेमेंट QR" else "OFFICIAL FEE PAYMENT QR & BANK ACCOUNT",
                    title = if (isHindi) "आधिकारिक विद्यालय बैंक खाता एवं UPI QR कोड" else "Official School Bank Account & Payment QR",
                    subtitle = if (isHindi) {
                        "एडमिन पैनल द्वारा सत्यापित विद्यालय विकास एवं नामांकन शुल्क भुगतान विवरण"
                    } else {
                        "Live school bank account and dynamic UPI QR configured by the Principal/Admin Panel"
                    },
                    actionLabel = if (isHindi) "एडमिन अपडेट" else "Edit in Admin",
                    onActionClick = { onNavigateTab(PortalTab.ADMIN_PANEL) }
                )

                SchoolPaymentQrPreviewCard(
                    isHindi = isHindi,
                    holderName = schoolAccountConfig.accountHolderName,
                    bankName = schoolAccountConfig.bankName,
                    accountNumber = schoolAccountConfig.accountNumber,
                    ifscCode = schoolAccountConfig.ifscCode,
                    upiId = schoolAccountConfig.upiId,
                    qrTitle = schoolAccountConfig.qrCodeTitle,
                    qrCustomImageUri = schoolAccountConfig.qrCustomImageUri
                )

                if (cloudAdmissions.isNotEmpty()) {
                    SectionHeader(
                        badgeText = if (isHindi) "क्लाउड नामांकन सत्यापन स्थिति" else "LIVE ADMISSION APPROVAL STATUS",
                        title = if (isHindi) "ऑनलाइन नामांकन स्वीकृति (Accepted / Pending / Cancelled)" else "Live Admission Applications Status"
                    )

                    cloudAdmissions.forEach { adm ->
                        val statusColor = when (adm.status) {
                            "ACCEPTED" -> EmeraldSuccess
                            "CANCELLED" -> CrimsonUrgent
                            else -> SaffronGoldSecondary
                        }
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, statusColor.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${adm.studentName} (${adm.id})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${adm.classApplied} • ${adm.streamApplied} • ${adm.village}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    color = statusColor.copy(alpha = 0.14f),
                                    contentColor = statusColor,
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = adm.status,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. INTERACTIVE DOCUMENT CHECKLIST
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val progress = if (docChecklist.isNotEmpty()) {
                    checkedDocIds.size.toFloat() / docChecklist.size.toFloat()
                } else 0f

                SectionHeader(
                    badgeText = if (isHindi) "नामांकन दस्तावेज़ चेकलिस्ट" else "ADMISSION DOCUMENT CHECKLIST",
                    title = if (isHindi) {
                        "विद्यालय आने से पूर्व आवश्यक कागजात (${checkedDocIds.size}/${docChecklist.size} तैयार)"
                    } else {
                        "Required Documents Checklist (${checkedDocIds.size}/${docChecklist.size} Ready)"
                    }
                )

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { progress },
                            color = EmeraldSuccess,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                        )

                        docChecklist.forEach { (docId, label) ->
                            val checked = checkedDocIds.contains(docId)
                            Surface(
                                onClick = { onToggleDocCheck(docId) },
                                color = if (checked) EmeraldSuccessBg.copy(alpha = 0.45f) else Color.Transparent,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Checkbox(
                                        checked = checked,
                                        onCheckedChange = { onToggleDocCheck(docId) }
                                    )
                                    Text(
                                        text = label.get(isHindi),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            SchoolWebFooter(isHindi = isHindi, onNavigateTab = onNavigateTab)
        }
    }

    // Modal for newly generated Provisional Admission Receipt
    if (latestGeneratedAdmission != null) {
        ProvisionalAdmissionReceiptDialog(
            app = latestGeneratedAdmission,
            isHindi = isHindi,
            onDismiss = onDismissReceiptModal
        )
    }
}

@Composable
private fun AdmissionTokenCard(
    app: AdmissionApplication,
    isHindi: Boolean
) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, SaffronGoldSecondary.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = RoyalNavyPrimary,
                    contentColor = SaffronGoldBright,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "TOKEN: ${app.applicationId}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Text(
                    text = app.submittedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = app.studentName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${app.classApplied} • ${app.streamApplied} • ${app.prevPercentage}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "${if (isHindi) "पिता:" else "Father:"} ${app.fatherName} | ${app.villagePanchayat}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = EmeraldSuccessBg,
                    contentColor = EmeraldSuccess,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = app.status,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                TextButton(
                    onClick = {
                        shareTextContent(
                            context = context,
                            title = "Admission Token ${app.applicationId} - UMV Gangauli",
                            body = "Student: ${app.studentName}\nFather: ${app.fatherName}\nClass/Stream: ${app.classApplied} (${app.streamApplied})\nVillage: ${app.villagePanchayat}\nToken ID: ${app.applicationId}"
                        )
                    }
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isHindi) "पावती शेयर करें" else "Share Receipt")
                }
            }
        }
    }
}

@Composable
private fun ScholarshipSchemeCard(
    scheme: ScholarshipScheme,
    isHindi: Boolean
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
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
                    color = SaffronGoldBright,
                    contentColor = Color(0xFF1F1200),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = scheme.amountBadge.get(isHindi),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = scheme.portalName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = scheme.schemeName.get(isHindi),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = scheme.description.get(isHindi),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProvisionalAdmissionReceiptDialog(
    app: AdmissionApplication,
    isHindi: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.92f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = EmeraldSuccessBg,
                    contentColor = EmeraldSuccess,
                    shape = RoundedCornerShape(50)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(
                            text = if (isHindi) "प्रोविजनल नामांकन पावती" else "PROVISIONAL ADMISSION TOKEN",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = if (isHindi) "उच्च माध्यमिक विद्यालय गंगौली, सिमरी, बक्सर" else "U.M. Vidyalaya Gangauli, Simri, Buxar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = RoyalNavyPrimary,
                    contentColor = SaffronGoldBright,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isHindi) "आवेदन टोकन संख्या (Application ID)" else "Official Application Token ID",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = app.applicationId,
                            style = MaterialTheme.typography.headlineMedium,
                            color = SaffronGoldBright,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ReceiptDetailRow(if (isHindi) "विद्यार्थी का नाम:" else "Student Name:", app.studentName)
                    ReceiptDetailRow(if (isHindi) "पिता का नाम:" else "Father's Name:", app.fatherName)
                    ReceiptDetailRow(if (isHindi) "आवेदित कक्षा:" else "Class Applied:", app.classApplied)
                    ReceiptDetailRow(if (isHindi) "संकाय (Stream):" else "Stream:", app.streamApplied)
                    ReceiptDetailRow(if (isHindi) "पिछले प्राप्तांक:" else "Previous Marks:", app.prevPercentage)
                    ReceiptDetailRow(if (isHindi) "ग्राम / पंचायत:" else "Village:", app.villagePanchayat)
                    ReceiptDetailRow(if (isHindi) "मोबाइल नंबर:" else "Mobile No.:", app.mobileNumber)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            shareTextContent(
                                context = context,
                                title = "UMV Gangauli Admission Token: ${app.applicationId}",
                                body = "Student: ${app.studentName}\nClass: ${app.classApplied} (${app.streamApplied})\nToken: ${app.applicationId}"
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isHindi) "शेयर करें" else "Share")
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isHindi) "ठीक है (Done)" else "Done")
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}
