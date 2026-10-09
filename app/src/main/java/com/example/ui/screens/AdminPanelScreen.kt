package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.*
import com.example.model.PortalTab
import com.example.ui.components.SchoolWebFooter
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import kotlin.math.abs

enum class AdminSubSection(val hiTitle: String, val enTitle: String) {
    GALLERY("📸 फोटो गैलरी", "📸 Photo Gallery"),
    ADMISSIONS("✅ नामांकन (Accept/Cancel)", "✅ Admissions"),
    ACCOUNT_QR("🏦 बैंक खाता व QR", "🏦 Account & QR"),
    LIVE_RESULTS("📊 क्लास-वाइज रिजल्ट", "📊 Live Results"),
    TEACHERS("👨‍🏫 शिक्षक पंजीकरण", "👨‍🏫 Teachers")
}

@Composable
fun AdminPanelScreen(
    isHindi: Boolean,
    adminEmail: String,
    galleryPhotos: List<GalleryPhotoItem>,
    admissions: List<AdmissionRecordItem>,
    schoolAccountConfig: SchoolAccountConfigItem,
    liveResults: List<ClassResultRecordItem>,
    registeredTeachers: List<TeacherRecordItem>,
    onAddGalleryPhoto: (occasionTheme: String, title: String, imageSource: String, dateLabel: String) -> Unit,
    onDeleteGalleryPhoto: (photoId: String) -> Unit,
    onCreateAdmission: (studentName: String, fatherName: String, classApplied: String, streamApplied: String, prevPercent: String, mobile: String, village: String) -> Unit,
    onUpdateAdmissionStatus: (admissionId: String, status: String) -> Unit,
    onDeleteAdmission: (admissionId: String) -> Unit,
    onSaveAccountConfig: (holder: String, bank: String, accNo: String, ifsc: String, upiId: String, qrTitle: String, qrUri: String) -> Unit,
    onPublishClassResult: (rollNo: String, studentName: String, fatherName: String, className: String, totalMarks: Int, maxMarks: Int, division: String, subjectBreakdown: String, isLive: Boolean) -> Unit,
    onToggleResultLive: (resultId: String, isLive: Boolean) -> Unit,
    onSetClassAllLive: (className: String, isLive: Boolean) -> Unit,
    onDeleteClassResult: (resultId: String) -> Unit,
    onRegisterTeacher: (name: String, designation: String, department: String, subject: String, qualification: String, experience: String, phone: String) -> Unit,
    onDeleteTeacher: (teacherId: String) -> Unit,
    onSignOut: () -> Unit,
    onNavigateTab: (PortalTab) -> Unit
) {
    var activeSection by remember { mutableStateOf(AdminSubSection.GALLERY) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_panel_screen"),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Admin Dashboard Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(RoyalNavyDark, RoyalNavyPrimary, Color(0xFF1B3B5C))
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = SaffronGoldBright,
                            contentColor = Color(0xFF1F1200),
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isHindi) "प्राचार्य एवं एडमिन कंट्रोल पैनल (LIVE)" else "PRINCIPAL & ADMIN CONTROL PANEL (LIVE)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onSignOut,
                            border = BorderStroke(1.dp, SaffronGoldBright),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SaffronGoldBright),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("admin_sign_out_btn")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "लॉगआउट" else "Sign Out",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = if (isHindi) {
                            "उच्च माध्यमिक विद्यालय गंगौली • एडमिन डैशबोर्ड"
                        } else {
                            "UMV Gangauli • Cloud Admin Dashboard"
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (isHindi) {
                            "लॉगिन आईडी: $adminEmail • क्लाउड डेटाबेस (Firestore) से रियल-टाइम सिंक सक्रिय"
                        } else {
                            "Signed in as: $adminEmail • Real-time Cloud Firestore Sync Active"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1)
                    )

                    // Quick KPI Summary Pills
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AdminKpiChip(
                            label = if (isHindi) "गैलरी फोटो" else "Gallery Photos",
                            count = "${galleryPhotos.size}"
                        )
                        AdminKpiChip(
                            label = if (isHindi) "नामांकन आवेदन" else "Admissions",
                            count = "${admissions.size}"
                        )
                        AdminKpiChip(
                            label = if (isHindi) "लाइव रिजल्ट" else "Live Results",
                            count = "${liveResults.count { it.isLive }}/${liveResults.size}"
                        )
                        AdminKpiChip(
                            label = if (isHindi) "पंजीकृत शिक्षक" else "Teachers",
                            count = "${registeredTeachers.size}"
                        )
                    }
                }
            }
        }

        // 2. Module Selector Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminSubSection.entries.forEach { section ->
                    val selected = activeSection == section
                    FilterChip(
                        selected = selected,
                        onClick = { activeSection = section },
                        label = {
                            Text(
                                text = if (isHindi) section.hiTitle else section.enTitle,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("admin_subtab_${section.name.lowercase()}")
                    )
                }
            }
        }

        // 3. Active Module Content
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (activeSection) {
                    AdminSubSection.GALLERY -> AdminGallerySection(
                        isHindi = isHindi,
                        photos = galleryPhotos,
                        onAddPhoto = onAddGalleryPhoto,
                        onDeletePhoto = onDeleteGalleryPhoto
                    )

                    AdminSubSection.ADMISSIONS -> AdminAdmissionsSection(
                        isHindi = isHindi,
                        admissions = admissions,
                        onCreateAdmission = onCreateAdmission,
                        onUpdateStatus = onUpdateAdmissionStatus,
                        onDeleteAdmission = onDeleteAdmission
                    )

                    AdminSubSection.ACCOUNT_QR -> AdminSchoolAccountQrSection(
                        isHindi = isHindi,
                        config = schoolAccountConfig,
                        onSaveConfig = onSaveAccountConfig
                    )

                    AdminSubSection.LIVE_RESULTS -> AdminLiveResultsSection(
                        isHindi = isHindi,
                        results = liveResults,
                        onPublishResult = onPublishClassResult,
                        onToggleResultLive = onToggleResultLive,
                        onSetClassAllLive = onSetClassAllLive,
                        onDeleteResult = onDeleteClassResult
                    )

                    AdminSubSection.TEACHERS -> AdminTeacherRegistrationSection(
                        isHindi = isHindi,
                        teachers = registeredTeachers,
                        onRegisterTeacher = onRegisterTeacher,
                        onDeleteTeacher = onDeleteTeacher
                    )
                }
            }
        }

        // 4. Footer
        item {
            SchoolWebFooter(
                isHindi = isHindi,
                onNavigateTab = onNavigateTab
            )
        }
    }
}

@Composable
private fun AdminKpiChip(label: String, count: String) {
    Surface(
        color = Color.White.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, SaffronGoldBright.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.titleMedium,
                color = SaffronGoldBright,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
        }
    }
}

// ============================================================================
// 1. PHOTO GALLERY MANAGEMENT (ADD WITH OCCASION/THEME & DELETE)
// ============================================================================
@Composable
private fun AdminGallerySection(
    isHindi: Boolean,
    photos: List<GalleryPhotoItem>,
    onAddPhoto: (occasionTheme: String, title: String, imageSource: String, dateLabel: String) -> Unit,
    onDeletePhoto: (String) -> Unit
) {
    var occasionTheme by remember { mutableStateOf("") }
    var photoTitle by remember { mutableStateOf("") }
    var dateLabel by remember { mutableStateOf("08 Oct 2026") }
    var selectedImageSource by remember { mutableStateOf("PRESET_CAMPUS") }
    var filterOccasion by remember { mutableStateOf("ALL") }

    // Zero-permission Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageSource = uri.toString()
        }
    }

    val presetOptions = listOf(
        "PRESET_CAMPUS" to (if (isHindi) "मुख्य विद्यालय भवन" else "Main Campus Building"),
        "PRESET_SMART_CLASS" to (if (isHindi) "स्मार्ट डिजिटल क्लास" else "Smart Classroom"),
        "PRESET_SCIENCE_LAB" to (if (isHindi) "विज्ञान प्रयोगशाला" else "Science Laboratory"),
        "PRESET_SPORTS" to (if (isHindi) "खेल मैदान एवं एथलेटिक्स" else "Sports Complex")
    )

    val quickOccasions = listOf(
        if (isHindi) "गणतंत्र दिवस समारोह (Republic Day)" else "Republic Day Celebration",
        if (isHindi) "सरस्वती पूजा एवं बसंत उत्सव" else "Saraswati Puja Festival",
        if (isHindi) "उन्नयन स्मार्ट क्लास एवं विज्ञान मेला" else "Smart Class & Science Fair",
        if (isHindi) "वार्षिक खेलकूद 'तरंग' प्रतियोगिता" else "Annual Sports Meet Tarang",
        if (isHindi) "बिहार दिवस एवं सांस्कृतिक कार्यक्रम" else "Bihar Diwas Cultural Event"
    )

    SectionHeader(
        badgeText = if (isHindi) "फोटो गैलरी प्रबंधन" else "PHOTO GALLERY MANAGER",
        title = if (isHindi) "अवसर / थीम नाम के साथ फोटो जोड़ें एवं हटाएं" else "Add & Delete Photos with Occasion / Theme",
        subtitle = if (isHindi) {
            "विद्यालय के कार्यक्रमों की तस्वीरें अवसर (Occasion) या थीम (Theme) के नाम के साथ गैलरी में जोड़ें या पुरानी तस्वीरें हटाएं।"
        } else {
            "Upload school event photos tagged with an Occasion or Theme name, or delete existing gallery items."
        }
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isHindi) "➕ नई फोटो गैलरी में जोड़ें (Add New Photo)" else "➕ Add New Photo to School Gallery",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = if (isHindi) "त्वरित अवसर/थीम चुनें (Quick Occasion Presets):" else "Quick Occasion / Theme Presets:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickOccasions.forEach { occ ->
                    SuggestionChip(
                        onClick = { occasionTheme = occ },
                        label = { Text(occ, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            OutlinedTextField(
                value = occasionTheme,
                onValueChange = { occasionTheme = it },
                label = {
                    Text(if (isHindi) "अवसर या थीम का नाम (Occasion / Theme Name) *" else "Occasion or Theme Name *")
                },
                placeholder = {
                    Text(if (isHindi) "उदा: वार्षिक विज्ञान प्रदर्शनी 2026" else "e.g. Annual Science Exhibition 2026")
                },
                leadingIcon = { Icon(Icons.Default.Celebration, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_gallery_occasion_input")
            )

            OutlinedTextField(
                value = photoTitle,
                onValueChange = { photoTitle = it },
                label = {
                    Text(if (isHindi) "फोटो शीर्षक / विवरण (Photo Caption / Title) *" else "Photo Title / Description *")
                },
                placeholder = {
                    Text(if (isHindi) "उदा: प्राचार्य द्वारा विजेता छात्रों को पुरस्कार वितरण" else "e.g. Principal awarding medals to winning students")
                },
                leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_gallery_title_input")
            )

            OutlinedTextField(
                value = dateLabel,
                onValueChange = { dateLabel = it },
                label = { Text(if (isHindi) "कार्यक्रम की तिथि (Event Date)" else "Event Date") },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = if (isHindi) "फोटो स्रोत चुनें (कैंपस फोटो या फोन गैलरी):" else "Choose Photo Source (Campus Preset or Device Photo Picker):",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presetOptions.forEach { (key, label) ->
                    FilterChip(
                        selected = selectedImageSource == key,
                        onClick = { selectedImageSource = key },
                        label = { Text(label) },
                        leadingIcon = if (selectedImageSource == key) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            OutlinedButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_pick_device_photo_btn")
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedImageSource.startsWith("content://") || selectedImageSource.startsWith("file://")) {
                        if (isHindi) "✔ डिवाइस से फोटो चुनी गई (बदलने के लिए टैप करें)" else "✔ Device Photo Selected (Tap to Change)"
                    } else {
                        if (isHindi) "फोन गैलरी से फोटो चुनें (Android Photo Picker)" else "Pick Photo from Phone Gallery"
                    }
                )
            }

            Button(
                onClick = {
                    if (occasionTheme.isNotBlank() && photoTitle.isNotBlank()) {
                        onAddPhoto(occasionTheme, photoTitle, selectedImageSource, dateLabel)
                        occasionTheme = ""
                        photoTitle = ""
                    }
                },
                enabled = occasionTheme.isNotBlank() && photoTitle.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("admin_add_photo_btn")
            ) {
                Icon(Icons.Default.AddAPhoto, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHindi) "फोटो अवसर/थीम के साथ गैलरी में जोड़ें" else "Add Photo with Occasion/Theme to Gallery",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Filter by Occasion/Theme
    val distinctOccasions = remember(photos) {
        listOf("ALL") + photos.map { it.occasionTheme }.distinct()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        distinctOccasions.forEach { occ ->
            FilterChip(
                selected = filterOccasion == occ,
                onClick = { filterOccasion = occ },
                label = {
                    Text(
                        if (occ == "ALL") {
                            if (isHindi) "सभी अवसर (${photos.size})" else "All Occasions (${photos.size})"
                        } else occ
                    )
                }
            )
        }
    }

    val filteredPhotos = remember(photos, filterOccasion) {
        if (filterOccasion == "ALL") photos else photos.filter { it.occasionTheme == filterOccasion }
    }

    filteredPhotos.forEach { photo ->
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(185.dp)
                ) {
                    Image(
                        painter = painterResource(id = resolveGalleryDrawable(photo.imageSource)),
                        contentDescription = photo.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f))
                                )
                            )
                    )
                    Surface(
                        color = SaffronGoldBright,
                        contentColor = Color(0xFF1F1200),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                            Text(
                                text = photo.occasionTheme,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Text(
                        text = photo.dateLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = photo.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (photo.imageSource.startsWith("content://")) {
                            Text(
                                text = if (isHindi) "डिवाइस फोटो URI संलग्न" else "Custom Device Photo Attached",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldSuccess
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    FilledTonalButton(
                        onClick = { onDeletePhoto(photo.id) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = CrimsonUrgentBg,
                            contentColor = CrimsonUrgent
                        ),
                        modifier = Modifier.testTag("admin_delete_photo_${photo.id}")
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isHindi) "हटाएं (Delete)" else "Delete",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun resolveGalleryDrawable(imageSource: String): Int {
    return when (imageSource) {
        "PRESET_SMART_CLASS" -> R.drawable.img_smart_classroom
        "PRESET_SCIENCE_LAB" -> R.drawable.img_science_lab
        "PRESET_SPORTS" -> R.drawable.img_sports_ground
        else -> R.drawable.img_campus_hero
    }
}

// ============================================================================
// 2. ADMISSION ACCEPT & CANCEL MANAGEMENT
// ============================================================================
@Composable
private fun AdminAdmissionsSection(
    isHindi: Boolean,
    admissions: List<AdmissionRecordItem>,
    onCreateAdmission: (studentName: String, fatherName: String, classApplied: String, streamApplied: String, prevPercent: String, mobile: String, village: String) -> Unit,
    onUpdateStatus: (admissionId: String, status: String) -> Unit,
    onDeleteAdmission: (admissionId: String) -> Unit
) {
    var statusFilter by remember { mutableStateOf("ALL") }
    var showWalkInForm by remember { mutableStateOf(false) }

    var studentName by remember { mutableStateOf("") }
    var fatherName by remember { mutableStateOf("") }
    var classApplied by remember { mutableStateOf("Class 11 (+2 Intermediate)") }
    var streamApplied by remember { mutableStateOf("Science (I.Sc. - PCM/PCB)") }
    var prevPercent by remember { mutableStateOf("85%") }
    var mobileNumber by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("Gangauli (गंगौली)") }

    SectionHeader(
        badgeText = if (isHindi) "ऑनलाइन नामांकन नियंत्रण" else "ADMISSION APPROVAL DESK",
        title = if (isHindi) "नामांकन आवेदन स्वीकार (Accept) या रद्द (Cancel) करें" else "Accept or Cancel Student Admission Applications",
        subtitle = if (isHindi) {
            "छात्रों द्वारा भरे गए ऑनलाइन नामांकन फॉर्म की जांच करें और एक क्लिक में नामांकन Accept (स्वीकृत) या Cancel (रद्द) करें।"
        } else {
            "Review student online admission applications and instantly Accept or Cancel applications."
        }
    )

    // Summary Counters Row
    val acceptedCount = admissions.count { it.status == "ACCEPTED" }
    val pendingCount = admissions.count { it.status == "PENDING" }
    val cancelledCount = admissions.count { it.status == "CANCELLED" }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AdmissionStatBox(
            title = if (isHindi) "लंबित (Pending)" else "Pending",
            count = pendingCount,
            color = SaffronGoldSecondary,
            modifier = Modifier.weight(1f)
        )
        AdmissionStatBox(
            title = if (isHindi) "स्वीकृत (Accepted)" else "Accepted",
            count = acceptedCount,
            color = EmeraldSuccess,
            modifier = Modifier.weight(1f)
        )
        AdmissionStatBox(
            title = if (isHindi) "रद्द (Cancelled)" else "Cancelled",
            count = cancelledCount,
            color = CrimsonUrgent,
            modifier = Modifier.weight(1f)
        )
    }

    // Filter + Quick Walk-In Entry Button
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(
            "ALL" to (if (isHindi) "सभी (${admissions.size})" else "All (${admissions.size})"),
            "PENDING" to (if (isHindi) "लंबित ($pendingCount)" else "Pending ($pendingCount)"),
            "ACCEPTED" to (if (isHindi) "स्वीकृत ($acceptedCount)" else "Accepted ($acceptedCount)"),
            "CANCELLED" to (if (isHindi) "रद्द ($cancelledCount)" else "Cancelled ($cancelledCount)")
        ).forEach { (key, label) ->
            FilterChip(
                selected = statusFilter == key,
                onClick = { statusFilter = key },
                label = { Text(label) }
            )
        }

        AssistChip(
            onClick = { showWalkInForm = !showWalkInForm },
            leadingIcon = { Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp)) },
            label = { Text(if (isHindi) "+ नया आवेदन जोड़ें" else "+ Add Walk-in Admission") }
        )
    }

    if (showWalkInForm) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isHindi) "नया छात्र नामांकन फॉर्म (काउंटर प्रविष्टि)" else "New Walk-in Student Admission Entry",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = studentName,
                    onValueChange = { studentName = it },
                    label = { Text(if (isHindi) "छात्र/छात्रा का नाम *" else "Student Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fatherName,
                    onValueChange = { fatherName = it },
                    label = { Text(if (isHindi) "पिता का नाम *" else "Father's Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = classApplied,
                        onValueChange = { classApplied = it },
                        label = { Text(if (isHindi) "कक्षा" else "Class") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = prevPercent,
                        onValueChange = { prevPercent = it },
                        label = { Text(if (isHindi) "प्राप्तांक %" else "Prev %") },
                        singleLine = true,
                        modifier = Modifier.weight(0.7f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it },
                        label = { Text(if (isHindi) "मोबाइल नंबर *" else "Mobile *") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = village,
                        onValueChange = { village = it },
                        label = { Text(if (isHindi) "ग्राम/पंचायत" else "Village") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Button(
                    onClick = {
                        if (studentName.isNotBlank() && fatherName.isNotBlank()) {
                            onCreateAdmission(
                                studentName,
                                fatherName,
                                classApplied,
                                streamApplied,
                                prevPercent,
                                mobileNumber.ifBlank { "9431000000" },
                                village
                            )
                            studentName = ""
                            fatherName = ""
                            mobileNumber = ""
                            showWalkInForm = false
                        }
                    },
                    enabled = studentName.isNotBlank() && fatherName.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isHindi) "नामांकन सूची में जोड़ें" else "Submit Admission Record")
                }
            }
        }
    }

    val filteredAdmissions = remember(admissions, statusFilter) {
        if (statusFilter == "ALL") admissions else admissions.filter { it.status == statusFilter }
    }

    filteredAdmissions.forEach { item ->
        val statusColor = when (item.status) {
            "ACCEPTED" -> EmeraldSuccess
            "CANCELLED" -> CrimsonUrgent
            else -> SaffronGoldSecondary
        }
        val statusBg = when (item.status) {
            "ACCEPTED" -> EmeraldSuccessBg
            "CANCELLED" -> CrimsonUrgentBg
            else -> SaffronGoldContainer
        }
        val statusLabel = when (item.status) {
            "ACCEPTED" -> if (isHindi) "✔ नामांकन स्वीकृत (ACCEPTED)" else "✔ ACCEPTED"
            "CANCELLED" -> if (isHindi) "✖ नामांकन रद्द (CANCELLED)" else "✖ CANCELLED"
            else -> if (isHindi) "⏳ सत्यापन लंबित (PENDING)" else "⏳ PENDING REVIEW"
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, statusColor.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = statusBg,
                        contentColor = statusColor,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.id,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { onDeleteAdmission(item.id) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete admission",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Text(
                    text = item.studentName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (isHindi) {
                        "पिता: ${item.fatherName} • ग्राम: ${item.village}\nकक्षा: ${item.classApplied} (${item.streamApplied}) • पूर्व प्राप्तांक: ${item.prevPercentage} • मो.: ${item.mobileNumber}"
                    } else {
                        "Father: ${item.fatherName} • Village: ${item.village}\nClass: ${item.classApplied} (${item.streamApplied}) • Prev Marks: ${item.prevPercentage} • Mob: ${item.mobileNumber}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onUpdateStatus(item.id, "ACCEPTED") },
                        enabled = item.status != "ACCEPTED",
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldSuccess,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_accept_admission_${item.id}")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "Accept (स्वीकार करें)" else "Accept Admission",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = { onUpdateStatus(item.id, "CANCELLED") },
                        enabled = item.status != "CANCELLED",
                        border = BorderStroke(1.dp, CrimsonUrgent),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonUrgent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_cancel_admission_${item.id}")
                    ) {
                        Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "Cancel (रद्द करें)" else "Cancel Admission",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdmissionStatBox(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.headlineSmall,
                color = color,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ============================================================================
// 3. SCHOOL ACCOUNT UPDATE & PAYMENT QR CODE CHANGE
// ============================================================================
@Composable
private fun AdminSchoolAccountQrSection(
    isHindi: Boolean,
    config: SchoolAccountConfigItem,
    onSaveConfig: (holder: String, bank: String, accNo: String, ifsc: String, upiId: String, qrTitle: String, qrUri: String) -> Unit
) {
    var holderName by remember(config) { mutableStateOf(config.accountHolderName) }
    var bankName by remember(config) { mutableStateOf(config.bankName) }
    var accountNumber by remember(config) { mutableStateOf(config.accountNumber) }
    var ifscCode by remember(config) { mutableStateOf(config.ifscCode) }
    var upiId by remember(config) { mutableStateOf(config.upiId) }
    var qrTitle by remember(config) { mutableStateOf(config.qrCodeTitle) }
    var qrImageUri by remember(config) { mutableStateOf(config.qrCustomImageUri) }

    val qrPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            qrImageUri = uri.toString()
        }
    }

    SectionHeader(
        badgeText = if (isHindi) "विद्यालय कोष एवं पेमेंट QR" else "SCHOOL ACCOUNT & PAYMENT QR",
        title = if (isHindi) "विद्यालय बैंक खाता एवं पेमेंट QR कोड अपडेट करें" else "Update School Bank Account & Change Payment QR Code",
        subtitle = if (isHindi) {
            "यहाँ से विद्यालय विकास कोष का बैंक खाता, IFSC कोड, UPI ID और आधिकारिक पेमेंट QR कोड कभी भी बदलें।"
        } else {
            "Update the official school bank account details, IFSC code, UPI ID, and Payment QR Code displayed to students and parents."
        }
    )

    // Live Preview of School Bank Account + Dynamic QR Card
    SchoolPaymentQrPreviewCard(
        isHindi = isHindi,
        holderName = holderName,
        bankName = bankName,
        accountNumber = accountNumber,
        ifscCode = ifscCode,
        upiId = upiId,
        qrTitle = qrTitle,
        qrCustomImageUri = qrImageUri
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isHindi) "🏦 बैंक खाता विवरण एवं QR कोड संपादित करें" else "🏦 Edit School Account & Payment QR Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = holderName,
                onValueChange = { holderName = it },
                label = { Text(if (isHindi) "खाताधारक का नाम (Account Holder Name)" else "Account Holder Name") },
                leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_bank_holder_input")
            )

            OutlinedTextField(
                value = bankName,
                onValueChange = { bankName = it },
                label = { Text(if (isHindi) "बैंक एवं शाखा का नाम (Bank & Branch Name)" else "Bank & Branch Name") },
                leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_bank_name_input")
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    label = { Text(if (isHindi) "खाता संख्या (Account No.)" else "Account Number") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("admin_account_no_input")
                )
                OutlinedTextField(
                    value = ifscCode,
                    onValueChange = { ifscCode = it.uppercase() },
                    label = { Text(if (isHindi) "IFSC कोड" else "IFSC Code") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(0.85f)
                        .testTag("admin_ifsc_input")
                )
            }

            OutlinedTextField(
                value = upiId,
                onValueChange = { upiId = it },
                label = { Text(if (isHindi) "आधिकारिक UPI ID (QR कोड इससे स्वतः बनेगा)" else "Official UPI ID (Auto-generates QR Matrix)") },
                leadingIcon = { Icon(Icons.Default.QrCode2, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_upi_id_input")
            )

            OutlinedTextField(
                value = qrTitle,
                onValueChange = { qrTitle = it },
                label = { Text(if (isHindi) "पेमेंट QR कोड शीर्षक / निर्देश" else "Payment QR Code Header Title") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_qr_title_input")
            )

            Text(
                text = if (isHindi) "पेमेंट QR कोड थीम / कस्टम इमेज बदलें:" else "Change Payment QR Code Style or Upload Custom QR Image:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "DEFAULT_QR" to (if (isHindi) "SBI आधिकारिक QR थीम" else "SBI Official QR Theme"),
                    "GOLD_HERITAGE_QR" to (if (isHindi) "गोल्डन विद्यालय QR थीम" else "Royal Gold QR Theme"),
                    "EMERALD_UPI_QR" to (if (isHindi) "एमराल्ड BHIM UPI थीम" else "Emerald BHIM UPI Theme")
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = qrImageUri == key,
                        onClick = { qrImageUri = key },
                        label = { Text(label) }
                    )
                }
            }

            OutlinedButton(
                onClick = {
                    qrPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_change_qr_btn")
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (qrImageUri.startsWith("content://")) {
                        if (isHindi) "✔ कस्टम QR इमेज चुनी गई (बदलने के लिए टैप करें)" else "✔ Custom QR Image Selected (Tap to Change)"
                    } else {
                        if (isHindi) "फोन गैलरी से नया Payment QR Code अपलोड करें" else "Upload New Payment QR Code Image from Gallery"
                    }
                )
            }

            Button(
                onClick = {
                    onSaveConfig(holderName, bankName, accountNumber, ifscCode, upiId, qrTitle, qrImageUri)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("admin_save_account_btn")
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHindi) "खाता विवरण एवं पेमेंट QR अपडेट करें" else "Save & Update School Account & Payment QR",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SchoolPaymentQrPreviewCard(
    isHindi: Boolean,
    holderName: String,
    bankName: String,
    accountNumber: String,
    ifscCode: String,
    upiId: String,
    qrTitle: String,
    qrCustomImageUri: String
) {
    val qrAccentColor = when (qrCustomImageUri) {
        "GOLD_HERITAGE_QR" -> Color(0xFF9A5B00)
        "EMERALD_UPI_QR" -> EmeraldSuccess
        else -> RoyalNavyPrimary
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(2.dp, SaffronGoldBright),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("school_payment_qr_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = RoyalNavyPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SaffronGoldBright)
                    Column {
                        Text(
                            text = qrTitle,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isHindi) "उच्च माध्यमिक विद्यालय गंगौली, सिमरी, बक्सर" else "Uchch Madhyamik Vidyalaya Gangauli, Simri, Buxar",
                            style = MaterialTheme.typography.labelSmall,
                            color = SaffronGoldBright
                        )
                    }
                }
            }

            // Dynamic Deterministic UPI QR Matrix Visual
            Box(
                modifier = Modifier
                    .size(196.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(3.dp, qrAccentColor, RoundedCornerShape(16.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                val seedPayload = "$upiId|$accountNumber|$ifscCode|$qrCustomImageUri"
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val gridCount = 21
                    val cellSize = size.width / gridCount
                    val hash = abs(seedPayload.hashCode())

                    for (r in 0 until gridCount) {
                        for (c in 0 until gridCount) {
                            val isTopLeftFinder = r in 0..6 && c in 0..6
                            val isTopRightFinder = r in 0..6 && c in (gridCount - 7) until gridCount
                            val isBottomLeftFinder = r in (gridCount - 7) until gridCount && c in 0..6
                            val isCenterCrest = r in 8..12 && c in 8..12

                            val drawModule = when {
                                isCenterCrest -> false
                                isTopLeftFinder -> isFinderModule(r, c)
                                isTopRightFinder -> isFinderModule(r, c - (gridCount - 7))
                                isBottomLeftFinder -> isFinderModule(r - (gridCount - 7), c)
                                else -> ((r * 31 + c * 17 + hash) % 3 == 0) || ((r xor c) + hash) % 5 == 0
                            }

                            if (drawModule) {
                                drawRect(
                                    color = qrAccentColor,
                                    topLeft = Offset(c * cellSize, r * cellSize),
                                    size = Size(cellSize * 0.92f, cellSize * 0.92f)
                                )
                            }
                        }
                    }
                }

                // Center School Emblem Badge inside QR
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(RoyalNavyDark)
                        .border(1.5.dp, SaffronGoldBright, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_school_crest_fg),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Surface(
                color = EmeraldSuccessBg,
                contentColor = EmeraldSuccess,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "BHIM / GPay / PhonePe / Paytm UPI ID: $upiId",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            // Official Bank Account Details Table
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AccountDetailRow(
                        label = if (isHindi) "खाताधारक (Account Name):" else "Account Holder:",
                        value = holderName
                    )
                    AccountDetailRow(
                        label = if (isHindi) "बैंक एवं शाखा (Bank):" else "Bank & Branch:",
                        value = bankName
                    )
                    AccountDetailRow(
                        label = if (isHindi) "खाता संख्या (A/C No.):" else "Account No.:",
                        value = accountNumber
                    )
                    AccountDetailRow(
                        label = if (isHindi) "IFSC कोड:" else "IFSC Code:",
                        value = ifscCode
                    )
                }
            }
        }
    }
}

private fun isFinderModule(r: Int, c: Int): Boolean {
    if (r == 0 || r == 6 || c == 0 || c == 6) return true
    if (r in 2..4 && c in 2..4) return true
    return false
}

@Composable
private fun AccountDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ============================================================================
// 4. CLASS-WISE LIVE RESULTS MANAGEMENT
// ============================================================================
@Composable
private fun AdminLiveResultsSection(
    isHindi: Boolean,
    results: List<ClassResultRecordItem>,
    onPublishResult: (rollNo: String, studentName: String, fatherName: String, className: String, totalMarks: Int, maxMarks: Int, division: String, subjectBreakdown: String, isLive: Boolean) -> Unit,
    onToggleResultLive: (resultId: String, isLive: Boolean) -> Unit,
    onSetClassAllLive: (className: String, isLive: Boolean) -> Unit,
    onDeleteResult: (resultId: String) -> Unit
) {
    val classesList = listOf(
        "Class 9",
        "Class 10",
        "Class 11 Science",
        "Class 12 Science",
        "Class 12 Arts"
    )

    var selectedClassFilter by remember { mutableStateOf("ALL") }

    // Form states
    var rollNo by remember { mutableStateOf("") }
    var studentName by remember { mutableStateOf("") }
    var fatherName by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("Class 12 Science") }
    var totalMarksStr by remember { mutableStateOf("450") }
    var division by remember { mutableStateOf("1st Division (Distinction)") }
    var subjectBreakdown by remember { mutableStateOf("Physics: 92 | Chemistry: 90 | Math: 95 | English: 86 | Hindi: 87") }
    var publishImmediatelyLive by remember { mutableStateOf(true) }

    SectionHeader(
        badgeText = if (isHindi) "कक्षा-वार परीक्षा परिणाम नियंत्रण" else "CLASS-WISE LIVE RESULT PORTAL",
        title = if (isHindi) "रिजल्ट क्लास-वाइज (Class-wise) Live या Hide करें" else "Publish & Toggle Class-wise Live Exam Results",
        subtitle = if (isHindi) {
            "किसी भी कक्षा (Class 9, 10, 11, 12) का परीक्षा परिणाम एक क्लिक में छात्र पोर्टल पर Live करें या नया छात्र अंकपत्र जोड़ें।"
        } else {
            "Make exam results Live class-wise for students with one click, or publish new student marksheets."
        }
    )

    // Class-wise Bulk Live Control Matrix
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (isHindi) "📡 कक्षा-वार (Class-wise) एक-क्लिक Live स्टेटस नियंत्रण" else "📡 Class-wise One-Click Live Status Control",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            classesList.forEach { cls ->
                val classRecords = results.filter { it.className.equals(cls, ignoreCase = true) }
                val liveCount = classRecords.count { it.isLive }
                val allLive = classRecords.isNotEmpty() && liveCount == classRecords.size

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cls,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isHindi) {
                                    "कुल छात्र: ${classRecords.size} • Live: $liveCount"
                                } else {
                                    "Total Records: ${classRecords.size} • Live: $liveCount"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = if (allLive) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onSetClassAllLive(cls, true) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldSuccess,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("admin_class_live_${cls.replace(" ", "_")}")
                            ) {
                                Text(
                                    text = if (isHindi) "Live करें" else "Go Live",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = { onSetClassAllLive(cls, false) },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isHindi) "रोकें (Hide)" else "Hide",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add New Result Form
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (isHindi) "➕ नया छात्र रिजल्ट जोड़ें एवं Live करें" else "➕ Publish New Student Marksheet Result",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                classesList.forEach { cls ->
                    FilterChip(
                        selected = className == cls,
                        onClick = { className = cls },
                        label = { Text(cls) }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = rollNo,
                    onValueChange = { rollNo = it },
                    label = { Text(if (isHindi) "रोल नंबर *" else "Roll No *") },
                    placeholder = { Text("260110") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(0.8f)
                        .testTag("admin_result_roll_input")
                )
                OutlinedTextField(
                    value = totalMarksStr,
                    onValueChange = { totalMarksStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text(if (isHindi) "कुल प्राप्तांक (/500) *" else "Marks (/500) *") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("admin_result_marks_input")
                )
            }

            OutlinedTextField(
                value = studentName,
                onValueChange = { studentName = it },
                label = { Text(if (isHindi) "छात्र/छात्रा का नाम *" else "Student Full Name *") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_result_name_input")
            )

            OutlinedTextField(
                value = fatherName,
                onValueChange = { fatherName = it },
                label = { Text(if (isHindi) "पिता का नाम *" else "Father's Name *") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_result_father_input")
            )

            OutlinedTextField(
                value = division,
                onValueChange = { division = it },
                label = { Text(if (isHindi) "श्रेणी (Division)" else "Division / Grade") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = subjectBreakdown,
                onValueChange = { subjectBreakdown = it },
                label = { Text(if (isHindi) "विषयवार अंक (Subject Marks)" else "Subject-wise Marks Breakdown") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_result_subjects_input")
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isHindi) "प्रकाशन के साथ ही पोर्टल पर LIVE करें" else "Make Result LIVE Immediately on Student Hub",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = publishImmediatelyLive,
                    onCheckedChange = { publishImmediatelyLive = it }
                )
            }

            Button(
                onClick = {
                    val marks = totalMarksStr.toIntOrNull() ?: 400
                    if (rollNo.isNotBlank() && studentName.isNotBlank()) {
                        onPublishResult(
                            rollNo,
                            studentName,
                            fatherName.ifBlank { "अभिभावक / Guardian" },
                            className,
                            marks,
                            500,
                            division,
                            subjectBreakdown,
                            publishImmediatelyLive
                        )
                        rollNo = ""
                        studentName = ""
                        fatherName = ""
                    }
                },
                enabled = rollNo.isNotBlank() && studentName.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("admin_publish_result_btn")
            ) {
                Icon(Icons.Default.Publish, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHindi) "कक्षा परिणाम प्रकाशित एवं Live करें" else "Publish Class Result",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Class Filter Chips for Results List
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        (listOf("ALL") + classesList).forEach { cls ->
            FilterChip(
                selected = selectedClassFilter == cls,
                onClick = { selectedClassFilter = cls },
                label = {
                    Text(if (cls == "ALL") (if (isHindi) "सभी कक्षाएं (${results.size})" else "All Classes (${results.size})") else cls)
                }
            )
        }
    }

    val filteredResults = remember(results, selectedClassFilter) {
        if (selectedClassFilter == "ALL") results else results.filter { it.className.equals(selectedClassFilter, ignoreCase = true) }
    }

    filteredResults.forEach { res ->
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(
                1.dp,
                if (res.isLive) EmeraldSuccess.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = if (res.isLive) EmeraldSuccessBg else CrimsonUrgentBg,
                            contentColor = if (res.isLive) EmeraldSuccess else CrimsonUrgent,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = if (res.isLive) {
                                    if (isHindi) "● LIVE (पोर्टल पर दृश्य)" else "● LIVE ON PORTAL"
                                } else {
                                    if (isHindi) "○ HIDDEN (गोपनीय)" else "○ HIDDEN"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${res.className} • Roll: ${res.rollNo}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = res.isLive,
                            onCheckedChange = { onToggleResultLive(res.id, it) }
                        )
                        IconButton(onClick = { onDeleteResult(res.id) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete result")
                        }
                    }
                }

                Text(
                    text = "${res.studentName} (पिता: ${res.fatherName})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${res.totalMarks}/${res.maxMarks} (${String.format("%.1f", res.percentage)}%) • ${res.division}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = res.subjectBreakdown,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ============================================================================
// 5. TEACHER REGISTRATION & DIRECTORY MANAGEMENT
// ============================================================================
@Composable
private fun AdminTeacherRegistrationSection(
    isHindi: Boolean,
    teachers: List<TeacherRecordItem>,
    onRegisterTeacher: (name: String, designation: String, department: String, subject: String, qualification: String, experience: String, phone: String) -> Unit,
    onDeleteTeacher: (teacherId: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("वरिष्ठ प्रवक्ता (PGT Lecturer)") }
    var department by remember { mutableStateOf("Science") }
    var subject by remember { mutableStateOf("") }
    var qualification by remember { mutableStateOf("M.Sc., B.Ed., BPSC TRE Qualified") }
    var experience by remember { mutableStateOf("8 Years") }
    var phone by remember { mutableStateOf("") }

    val departments = listOf("Science", "Arts", "Secondary", "Languages", "Sports", "Administration")

    SectionHeader(
        badgeText = if (isHindi) "शिक्षक पंजीकरण पोर्टल" else "TEACHER REGISTRATION PORTAL",
        title = if (isHindi) "नए शिक्षक का पंजीकरण (Teacher Registration) करें" else "Register New Faculty & Manage Teachers",
        subtitle = if (isHindi) {
            "विद्यालय में पदस्थापित BPSC एवं वरिष्ठ शिक्षकों का विषय, योग्यता एवं अनुभव के साथ आधिकारिक पंजीकरण करें।"
        } else {
            "Register new BPSC and senior secondary faculty members with their subject, qualification, and department."
        }
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isHindi) "👨‍🏫 नया शिक्षक पंजीकरण फॉर्म (Teacher Registration)" else "👨‍🏫 New Teacher Registration Form",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(if (isHindi) "शिक्षक का पूरा नाम (Full Name) *" else "Teacher Full Name *") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_teacher_name_input")
            )

            Text(
                text = if (isHindi) "विभाग चुनें (Select Department):" else "Select Department:",
                style = MaterialTheme.typography.labelMedium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                departments.forEach { dept ->
                    FilterChip(
                        selected = department == dept,
                        onClick = { department = dept },
                        label = { Text(dept) }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = designation,
                    onValueChange = { designation = it },
                    label = { Text(if (isHindi) "पदनाम (Designation) *" else "Designation *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text(if (isHindi) "मुख्य विषय (Subject) *" else "Subject *") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("admin_teacher_subject_input")
                )
            }

            OutlinedTextField(
                value = qualification,
                onValueChange = { qualification = it },
                label = { Text(if (isHindi) "शैक्षणिक योग्यता (Qualification)" else "Qualification (e.g. M.Sc., Ph.D., B.Ed.)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_teacher_qual_input")
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = experience,
                    onValueChange = { experience = it },
                    label = { Text(if (isHindi) "अनुभव (Experience)" else "Experience") },
                    singleLine = true,
                    modifier = Modifier.weight(0.8f)
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (isHindi) "मोबाइल / संपर्क नंबर" else "Contact Phone") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("admin_teacher_phone_input")
                )
            }

            Button(
                onClick = {
                    if (name.isNotBlank() && subject.isNotBlank()) {
                        onRegisterTeacher(
                            name,
                            designation,
                            department,
                            subject,
                            qualification,
                            experience,
                            phone.ifBlank { "06183-245101" }
                        )
                        name = ""
                        subject = ""
                        phone = ""
                    }
                },
                enabled = name.isNotBlank() && subject.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("admin_register_teacher_btn")
            ) {
                Icon(Icons.Default.Badge, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHindi) "शिक्षक पंजीकृत करें (Register Teacher)" else "Register Teacher in School Directory",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Registered Teachers List
    teachers.forEach { tchr ->
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${tchr.department} • ${tchr.designation}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Text(
                        text = tchr.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isHindi) {
                            "विषय: ${tchr.subject} • योग्यता: ${tchr.qualification}"
                        } else {
                            "Subject: ${tchr.subject} • Qualification: ${tchr.qualification}"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isHindi) {
                            "अनुभव: ${tchr.experience} • संपर्क: ${tchr.phone}"
                        } else {
                            "Experience: ${tchr.experience} • Contact: ${tchr.phone}"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(
                    onClick = { onDeleteTeacher(tchr.id) },
                    modifier = Modifier.testTag("admin_delete_teacher_${tchr.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Remove teacher",
                        tint = CrimsonUrgent
                    )
                }
            }
        }
    }
}
