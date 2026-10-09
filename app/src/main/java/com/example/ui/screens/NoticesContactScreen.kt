package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun NoticesContactScreen(
    isHindi: Boolean,
    notices: List<NoticeItem>,
    selectedCategory: String,
    searchQuery: String,
    bookmarkedIds: Set<String>,
    showBookmarkedOnly: Boolean,
    calendarEvents: List<AcademicEvent>,
    onSelectCategory: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onToggleShowBookmarkedOnly: () -> Unit,
    onOpenNotice: (NoticeItem) -> Unit,
    onBookCampusVisit: (String, String, String) -> Unit,
    onNavigateTab: (PortalTab) -> Unit
) {
    val context = LocalContext.current
    var visitorName by remember { mutableStateOf("") }
    var visitorPhone by remember { mutableStateOf("") }
    var visitPurpose by remember { mutableStateOf("") }

    val filteredNotices = remember(notices, selectedCategory, searchQuery, bookmarkedIds, showBookmarkedOnly) {
        notices.filter { item ->
            val catOk = selectedCategory == "ALL" || item.category == selectedCategory
            val bmOk = !showBookmarkedOnly || bookmarkedIds.contains(item.id)
            val q = searchQuery.trim()
            val queryOk = q.isEmpty() ||
                item.title.hi.contains(q, ignoreCase = true) ||
                item.title.en.contains(q, ignoreCase = true) ||
                item.summary.hi.contains(q, ignoreCase = true) ||
                item.summary.en.contains(q, ignoreCase = true) ||
                item.refNo.contains(q, ignoreCase = true)
            catOk && bmOk && queryOk
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("notices_contact_screen_list"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. DIGITAL NOTICE BOARD HEADER & FILTERS
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(
                    badgeText = if (isHindi) "डिजिटल सूचना पट्ट एवं परिपत्र" else "DIGITAL NOTICE BOARD & CIRCULARS",
                    title = if (isHindi) "आधिकारिक विद्यालय सूचनाएं" else "Official School Notifications",
                    subtitle = if (isHindi) {
                        "पूर्ण आधिकारिक पत्र (Circular) पढ़ने के लिए किसी भी सूचना कार्ड पर टैप करें"
                    } else {
                        "Tap any notice card to open and share the full official circular"
                    }
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            if (isHindi) "सूचना या पत्रांक खोजें (जैसे: नामांकन, मेधासॉफ्ट, परीक्षा)..."
                            else "Search notice title or ref no..."
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = onToggleShowBookmarkedOnly) {
                            Icon(
                                imageVector = if (showBookmarkedOnly) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Filter Bookmarked",
                                tint = if (showBookmarkedOnly) SaffronGoldSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notice_search_input")
                )

                val categories = listOf(
                    "ALL" to BilingualText("सभी सूचनाएं", "All Notices"),
                    "ADMISSION" to BilingualText("नामांकन", "Admissions"),
                    "SCHOLARSHIP" to BilingualText("छात्रवृत्ति / DBT", "Scholarships"),
                    "EXAM" to BilingualText("परीक्षा", "Exams"),
                    "CULTURAL" to BilingualText("खेल एवं संस्कृति", "Sports & Events")
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { (catKey, label) ->
                        FilterChip(
                            selected = selectedCategory == catKey,
                            onClick = { onSelectCategory(catKey) },
                            label = { Text(label.get(isHindi)) },
                            modifier = Modifier.testTag("notice_category_chip_$catKey")
                        )
                    }
                }
            }
        }

        items(filteredNotices, key = { it.id }) { notice ->
            val isBookmarked = bookmarkedIds.contains(notice.id)
            NoticeBoardCard(
                notice = notice,
                isHindi = isHindi,
                isBookmarked = isBookmarked,
                onToggleBookmark = { onToggleBookmark(notice.id) },
                onClick = { onOpenNotice(notice) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // 2. ACADEMIC & CULTURAL CALENDAR
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(
                    badgeText = if (isHindi) "वार्षिक शैक्षणिक एवं सांस्कृतिक कैलेंडर" else "ACADEMIC & CULTURAL CALENDAR",
                    title = if (isHindi) "आगामी विद्यालय कार्यक्रम (2026-27)" else "Upcoming Campus Events & Milestones"
                )

                calendarEvents.forEach { ev ->
                    CalendarEventRowCard(event = ev, isHindi = isHindi)
                }
            }
        }

        // 3. CAMPUS LOCATION, HELPLINE & PARENT APPOINTMENT
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(
                    badgeText = if (isHindi) "संपर्क एवं लोकेशन • गंगौली, सिमरी, बक्सर" else "CAMPUS ADDRESS & HELPLINE DESK",
                    title = if (isHindi) "विद्यालय पता एवं अभिभावक सहायता केंद्र" else "Connect with UMV Gangauli Campus"
                )

                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    border = BorderStroke(1.dp, SaffronGoldSecondary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                color = RoyalNavyPrimary,
                                contentColor = SaffronGoldBright,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.size(50.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(26.dp))
                                }
                            }
                            Column {
                                Text(
                                    text = if (isHindi) "उच्च माध्यमिक विद्यालय गंगौली" else "Uchch Madhyamik Vidyalaya Gangauli",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isHindi) {
                                        "ग्राम एवं पोस्ट - गंगौली, प्रखंड - सिमरी, जिला - बक्सर (बिहार) - 802131"
                                    } else {
                                        "Vill & P.O. Gangauli, Block - Simri, Dist. - Buxar, Bihar - 802131"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (isHindi) "• मार्गदर्शक चिह्न: सिमरी-गंगौली मुख्य मार्ग (डुमरांव / बक्सर रेलवे स्टेशन से सुगम संपर्क)" else "• Landmark: Simri-Gangauli Main Road (Easy access from Dumraon & Buxar Railway Station)",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = if (isHindi) "• कार्यालय समय: प्रातः 09:00 बजे से सायं 04:00 बजे तक (सोमवार - शनिवार)" else "• Office Hours: 09:00 AM to 04:00 PM (Monday – Saturday)",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "• Email: principal.umvgangauli.buxar@bihar.gov.in",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { launchPhoneDialer(context) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isHindi) "कॉल करें" else "Call Office")
                            }

                            OutlinedButton(
                                onClick = { launchMapsLocation(context) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isHindi) "गूगल मैप" else "Google Maps")
                            }
                        }

                        OutlinedButton(
                            onClick = { launchEmailIntent(context) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isHindi) "प्राचार्य कार्यालय को ईमेल भेजें" else "Send Official Email to Principal")
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // Book Parent-Principal Meeting Form
                        Text(
                            text = if (isHindi) "अभिभावक भ्रमण / प्राचार्य से भेंट समय बुक करें:" else "Schedule Parent-Principal Campus Visit:",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = visitorName,
                                onValueChange = { visitorName = it },
                                label = { Text(if (isHindi) "अभिभावक का नाम" else "Parent Name") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = visitorPhone,
                                onValueChange = { visitorPhone = it },
                                label = { Text(if (isHindi) "मोबाइल नंबर" else "Mobile No.") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = visitPurpose,
                            onValueChange = { visitPurpose = it },
                            label = {
                                Text(
                                    if (isHindi) "भ्रमण का उद्देश्य (जैसे: नामांकन परामर्श, टीसी, छात्रवृत्ति)"
                                    else "Purpose of Visit (e.g. Admission, TC, Scholarship)"
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (visitorName.isNotBlank()) {
                                    onBookCampusVisit(
                                        visitorName,
                                        visitorPhone.ifBlank { "9431XXXXXX" },
                                        visitPurpose.ifBlank { "Campus Visit & Admission Inquiry" }
                                    )
                                    visitorName = ""
                                    visitorPhone = ""
                                    visitPurpose = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SaffronGoldBright,
                                contentColor = Color(0xFF1F1200)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("book_visit_button")
                        ) {
                            Icon(Icons.Default.EventAvailable, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "भ्रमण अपॉइंटमेंट बुक करें" else "Confirm Campus Visit Appointment",
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
private fun NoticeBoardCard(
    notice: NoticeItem,
    isHindi: Boolean,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag("notice_card_${notice.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            1.dp,
            if (notice.isUrgent) CrimsonUrgent.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
        )
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (notice.isUrgent) {
                        Surface(
                            color = CrimsonUrgent,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (isHindi) "अति आवश्यक" else "URGENT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = notice.categoryLabel.get(isHindi),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) SaffronGoldSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = notice.title.get(isHindi),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = notice.summary.get(isHindi),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${notice.refNo} • ${notice.date.get(isHindi)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = if (isHindi) "पूरा परिपत्र पढ़ें →" else "Read Full Circular →",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CalendarEventRowCard(
    event: AcademicEvent,
    isHindi: Boolean
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                color = RoyalNavyPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(width = 56.dp, height = 60.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = event.monthBadge,
                        style = MaterialTheme.typography.labelSmall,
                        color = SaffronGoldBright,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = event.dayNum,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = event.categoryBadge.get(isHindi),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = event.title.get(isHindi),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = event.subtitle.get(isHindi),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
