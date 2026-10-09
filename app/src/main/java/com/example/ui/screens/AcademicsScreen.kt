package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.TeacherRecordItem
import com.example.model.*
import com.example.ui.components.SchoolWebFooter
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun AcademicsScreen(
    isHindi: Boolean,
    streams: List<AcademicStream>,
    selectedClassId: String,
    selectedDayId: String,
    timetableSlots: List<TimetableSlot>,
    studyMaterials: List<StudyMaterial>,
    selectedStudyFilter: String,
    facultyMembers: List<FacultyMember>,
    cloudTeachers: List<TeacherRecordItem> = emptyList(),
    facultySearchQuery: String,
    selectedDepartment: String,
    onSelectTimetableClass: (String) -> Unit,
    onSelectTimetableDay: (String) -> Unit,
    onSelectStudyFilter: (String) -> Unit,
    onOpenStudyMaterial: (StudyMaterial) -> Unit,
    onFacultySearchChange: (String) -> Unit,
    onSelectDepartment: (String) -> Unit,
    onNavigateTab: (PortalTab) -> Unit
) {
    var expandedStreamId by remember { mutableStateOf("stream_isc") }

    val filteredFaculty = remember(facultyMembers, facultySearchQuery, selectedDepartment) {
        facultyMembers.filter { member ->
            val matchesDept = selectedDepartment == "ALL" || member.department.equals(selectedDepartment, ignoreCase = true)
            val q = facultySearchQuery.trim()
            val matchesQuery = q.isEmpty() ||
                member.name.hi.contains(q, ignoreCase = true) ||
                member.name.en.contains(q, ignoreCase = true) ||
                member.subject.hi.contains(q, ignoreCase = true) ||
                member.subject.en.contains(q, ignoreCase = true)
            matchesDept && matchesQuery
        }
    }

    val filteredMaterials = remember(studyMaterials, selectedStudyFilter) {
        if (selectedStudyFilter == "ALL") studyMaterials
        else studyMaterials.filter { it.classLevel.contains(selectedStudyFilter, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("academics_screen_list"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. ACADEMIC STREAMS & CURRICULUM
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(
                    badgeText = if (isHindi) "शैक्षणिक संकाय • BSEB पटना पाठ्यक्रम" else "ACADEMIC FACULTIES & STREAMS",
                    title = if (isHindi) "कक्षा 9वीं से 12वीं (+2) संकाय एवं सीट विवरण" else "Classes IX to XII (+2) Streams & Syllabus",
                    subtitle = if (isHindi) {
                        "किसी भी संकाय पर टैप करके मुख्य विषय, ऐच्छिक विषय एवं कैरियर अवसर देखें"
                    } else {
                        "Tap any stream card to view compulsory subjects, lab electives, and career pathways"
                    }
                )

                streams.forEach { stream ->
                    AcademicStreamAccordionCard(
                        stream = stream,
                        isHindi = isHindi,
                        isExpanded = expandedStreamId == stream.id,
                        onToggle = {
                            expandedStreamId = if (expandedStreamId == stream.id) "" else stream.id
                        },
                        onApplyClick = { onNavigateTab(PortalTab.ADMISSIONS) }
                    )
                }
            }
        }

        // 2. INTERACTIVE DAILY TIMETABLE / CLASS ROUTINE
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(
                    badgeText = if (isHindi) "दैनिक कक्षा एवं स्मार्ट लैब रूटीन" else "INTERACTIVE CLASS TIMETABLE",
                    title = if (isHindi) "पीरियड-वार कक्षा समय-सारणी" else "Daily Period & Smart Lab Schedule",
                    subtitle = if (isHindi) {
                        "अपनी कक्षा और दिन चुनें (प्रातः 09:30 से अपराह्न 03:30 बजे तक)"
                    } else {
                        "Filter by Class and Day of Week (09:30 AM – 03:30 PM)"
                    }
                )

                // Class Selector Chips
                val classOptions = listOf(
                    "CLASS_9" to BilingualText("कक्षा 9वीं", "Class 9th"),
                    "CLASS_10" to BilingualText("कक्षा 10वीं (मैट्रिक)", "Class 10th (Matric)"),
                    "CLASS_12_SCI" to BilingualText("11वीं-12वीं विज्ञान (I.Sc.)", "+2 Science (I.Sc.)"),
                    "CLASS_12_ARTS" to BilingualText("11वीं-12वीं कला (I.A.)", "+2 Arts (I.A.)")
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    classOptions.forEach { (id, label) ->
                        FilterChip(
                            selected = selectedClassId == id,
                            onClick = { onSelectTimetableClass(id) },
                            label = { Text(label.get(isHindi), fontWeight = FontWeight.SemiBold) },
                            leadingIcon = if (selectedClassId == id) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            modifier = Modifier.testTag("timetable_class_chip_$id")
                        )
                    }
                }

                // Day Selector Chips
                val dayOptions = listOf(
                    "MON" to BilingualText("सोमवार", "Mon"),
                    "TUE" to BilingualText("मंगलवार", "Tue"),
                    "WED" to BilingualText("बुधवार", "Wed"),
                    "THU" to BilingualText("गुरुवार", "Thu"),
                    "FRI" to BilingualText("शुक्रवार", "Fri"),
                    "SAT" to BilingualText("सुरक्षित शनिवार", "Sat (Special)")
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    dayOptions.forEach { (id, label) ->
                        AssistChip(
                            onClick = { onSelectTimetableDay(id) },
                            label = {
                                Text(
                                    text = label.get(isHindi),
                                    fontWeight = if (selectedDayId == id) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (selectedDayId == id) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                                labelColor = if (selectedDayId == id) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.testTag("timetable_day_chip_$id")
                        )
                    }
                }

                // Timetable Card List
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        timetableSlots.forEachIndexed { index, slot ->
                            TimetableSlotRow(slot = slot, isHindi = isHindi)
                            if (index < timetableSlots.lastIndex) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                            }
                        }
                    }
                }
            }
        }

        // 3. DIGITAL E-LIBRARY & STUDY NOTES
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(
                    badgeText = if (isHindi) "डॉ. राजेंद्र प्रसाद डिजिटल ई-लाइब्रेरी" else "DIGITAL E-LIBRARY & FORMULA NOTES",
                    title = if (isHindi) "बोर्ड परीक्षा नोट्स, सूत्र एवं प्रश्न बैंक" else "Board Exam Revision Notes & Guides",
                    subtitle = if (isHindi) {
                        "हमारे विशेषज्ञ शिक्षकों द्वारा तैयार अध्ययन सामग्री पढ़ने के लिए कार्ड पर टैप करें"
                    } else {
                        "Tap any study guide prepared by our faculty to open the formula & revision sheet"
                    }
                )

                val studyFilters = listOf(
                    "ALL" to BilingualText("सभी कक्षाएं", "All Classes"),
                    "9-10" to BilingualText("कक्षा 9-10 (मैट्रिक)", "Class 9–10"),
                    "Science" to BilingualText("11-12 विज्ञान", "11–12 Science"),
                    "Arts" to BilingualText("11-12 कला", "11–12 Arts")
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    studyFilters.forEach { (key, label) ->
                        FilterChip(
                            selected = selectedStudyFilter == key,
                            onClick = { onSelectStudyFilter(key) },
                            label = { Text(label.get(isHindi)) }
                        )
                    }
                }

                filteredMaterials.forEach { mat ->
                    StudyMaterialCard(
                        material = mat,
                        isHindi = isHindi,
                        onClick = { onOpenStudyMaterial(mat) }
                    )
                }
            }
        }

        // 4. FACULTY DIRECTORY
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(
                    badgeText = if (isHindi) "हमारे गौरवशाली शिक्षक वृंद • BPSC चयनित" else "DISTINGUISHED FACULTY DIRECTORY",
                    title = if (isHindi) "विद्वान एवं समर्पित शिक्षक परिवार" else "Meet Our Academic Mentors",
                    subtitle = if (isHindi) {
                        "विषय या शिक्षक के नाम से खोजें"
                    } else {
                        "Search by faculty name or subject specialization"
                    }
                )

                OutlinedTextField(
                    value = facultySearchQuery,
                    onValueChange = onFacultySearchChange,
                    placeholder = {
                        Text(
                            if (isHindi) "शिक्षक का नाम या विषय खोजें (जैसे: Physics, गणित, History)..."
                            else "Search teacher name or subject (e.g. Physics, Math, History)..."
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (facultySearchQuery.isNotEmpty()) {
                            IconButton(onClick = { onFacultySearchChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("faculty_search_input")
                )

                val depts = listOf(
                    "ALL" to BilingualText("सभी विभाग", "All Depts"),
                    "Science" to BilingualText("विज्ञान संकाय", "Science"),
                    "Arts" to BilingualText("कला संकाय", "Arts"),
                    "Secondary" to BilingualText("माध्यमिक", "Secondary"),
                    "Languages" to BilingualText("भाषा एवं साहित्य", "Languages"),
                    "Sports" to BilingualText("खेल एवं एनसीसी", "Sports & NCC")
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    depts.forEach { (deptKey, label) ->
                        FilterChip(
                            selected = selectedDepartment == deptKey,
                            onClick = { onSelectDepartment(deptKey) },
                            label = { Text(label.get(isHindi)) }
                        )
                    }
                }
            }
        }

        items(filteredFaculty, key = { it.id }) { member ->
            FacultyMemberCard(
                member = member,
                isHindi = isHindi,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (cloudTeachers.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SectionHeader(
                        badgeText = if (isHindi) "एडमिन पोर्टल द्वारा पंजीकृत शिक्षक" else "CLOUD REGISTERED FACULTY",
                        title = if (isHindi) "नव-पंजीकृत शिक्षक वृंद (${cloudTeachers.size})" else "Newly Registered Faculty (${cloudTeachers.size})",
                        actionLabel = if (isHindi) "शिक्षक जोड़ें" else "Register Teacher",
                        onActionClick = { onNavigateTab(PortalTab.ADMIN_PANEL) }
                    )

                    cloudTeachers.forEach { tchr ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${tchr.department} • ${tchr.designation}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = tchr.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${tchr.subject} • ${tchr.qualification} • ${tchr.experience}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
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
}

@Composable
private fun AcademicStreamAccordionCard(
    stream: AcademicStream,
    isHindi: Boolean,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onApplyClick: () -> Unit
) {
    Card(
        onClick = onToggle,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            width = if (isExpanded) 1.5.dp else 1.dp,
            color = if (isExpanded) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("stream_card_${stream.id}")
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stream.classRange.get(isHindi),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stream.streamTitle.get(isHindi),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${stream.seats} ${if (isHindi) "सीटें" else "Seats"}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    Text(
                        text = if (isHindi) "मुख्य विषय (Core Subjects):" else "Core Compulsory Subjects:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    stream.coreSubjects.forEach { subj ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(text = subj.get(isHindi), style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Text(
                        text = if (isHindi) "ऐच्छिक एवं कौशल विषय (Electives):" else "Electives & Skill Modules:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )

                    stream.electiveSubjects.forEach { elec ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = SaffronGoldSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(text = elec.get(isHindi), style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "${if (isHindi) "कैरियर अवसर:" else "Career Pathways:"} ${stream.careerPathways.get(isHindi)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stream.hodName.get(isHindi),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = onApplyClick,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.HowToReg, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isHindi) "इस संकाय में ऑनलाइन नामांकन करें" else "Apply for Admission in This Stream")
                    }
                }
            }
        }
    }
}

@Composable
private fun TimetableSlotRow(
    slot: TimetableSlot,
    isHindi: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            color = if (slot.isPracticalOrSmartClass) SaffronGoldBright else MaterialTheme.colorScheme.primaryContainer,
            contentColor = if (slot.isPracticalOrSmartClass) Color(0xFF1F1200) else MaterialTheme.colorScheme.onPrimaryContainer,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.width(64.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "P-${slot.period}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = slot.timeRange.substringBefore(" -"),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = slot.subject.get(isHindi),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "${slot.teacher.get(isHindi)} • ${slot.timeRange}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = slot.room.get(isHindi),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                if (slot.isPracticalOrSmartClass) {
                    Surface(
                        color = EmeraldSuccessBg,
                        contentColor = EmeraldSuccess,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (isHindi) "स्मार्ट लैब / प्रायोगिक" else "Smart Lab / Practical",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudyMaterialCard(
    material: StudyMaterial,
    isHindi: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("study_material_card_${material.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(24.dp))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${material.classLevel} • ${material.materialType.get(isHindi)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = material.title.get(isHindi),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = material.chaptersOrPages.get(isHindi),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            FilledTonalIconButton(onClick = onClick) {
                Icon(Icons.Default.Visibility, contentDescription = if (isHindi) "पढ़ें" else "Read")
            }
        }
    }
}

@Composable
private fun FacultyMemberCard(
    member: FacultyMember,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = RoyalNavyPrimary,
                    contentColor = SaffronGoldBright,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(26.dp))
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = member.name.get(isHindi),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = member.designation.get(isHindi),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = member.department,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHindi) "विषय विशेषज्ञता:" else "Specialization:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = member.subject.get(isHindi),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHindi) "शैक्षणिक योग्यता:" else "Qualification:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = member.qualification.get(isHindi),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text(
                text = "\"${member.message.get(isHindi)}\"",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
