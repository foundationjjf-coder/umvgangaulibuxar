package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.GalleryPhotoItem
import com.example.model.*
import com.example.ui.components.SchoolWebFooter
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    isHindi: Boolean,
    facilities: List<FacilityItem>,
    toppers: List<TopperStudent>,
    notices: List<NoticeItem>,
    galleryPhotos: List<GalleryPhotoItem> = emptyList(),
    onNavigateTab: (PortalTab) -> Unit,
    onOpenFacility: (FacilityItem) -> Unit,
    onOpenNotice: (NoticeItem) -> Unit,
    onOpenVirtualTour: () -> Unit,
    onViewTopperMarksheet: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. GRAND CAMPUS HERO BANNER
        item {
            HeroCampusBanner(
                isHindi = isHindi,
                onApplyAdmission = { onNavigateTab(PortalTab.ADMISSIONS) },
                onCheckResults = { onNavigateTab(PortalTab.STUDENT_HUB) },
                onOpenVirtualTour = onOpenVirtualTour
            )
        }

        // 2. IMPACT METRICS BENTO GRID
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                ImpactStatsGrid(isHindi = isHindi)
            }
        }

        // 3. QUICK DIGITAL SERVICES DOCK
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(
                    badgeText = if (isHindi) "डिजिटल कैंपस सेवाएं" else "DIGITAL CAMPUS SERVICES",
                    title = if (isHindi) "एक क्लिक में सभी ऑनलाइन सुविधाएं" else "All School Services at One Tap",
                    subtitle = if (isHindi) {
                        "विद्यार्थियों एवं अभिभावकों के लिए घर बैठे नामांकन, परिणाम, समय-सारणी और छात्रवृत्ति पोर्टल"
                    } else {
                        "Seamless digital access to admissions, marksheets, class routines, and Bihar scholarship schemes"
                    }
                )
                QuickServicesGrid(
                    isHindi = isHindi,
                    onNavigateTab = onNavigateTab,
                    onOpenVirtualTour = onOpenVirtualTour
                )
            }
        }

        // 4. PRINCIPAL'S DESK & HERITAGE OF GANGAULI
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                PrincipalVisionCard(
                    isHindi = isHindi,
                    onMeetFaculty = { onNavigateTab(PortalTab.ACADEMICS) }
                )
            }
        }

        // 5. WORLD-CLASS CAMPUS FACILITIES SHOWCASE
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(
                    badgeText = if (isHindi) "विश्वस्तरीय इंफ्रास्ट्रक्चर" else "WORLD-CLASS INFRASTRUCTURE",
                    title = if (isHindi) "उन्नयन स्मार्ट क्लास एवं आधुनिक प्रयोगशालाएं" else "Smart Classrooms, STEM Labs & Sports Arena",
                    subtitle = if (isHindi) {
                        "किसी भी सुविधा कार्ड पर टैप करके फोटो, समय और उपकरणों का पूरा विवरण देखें"
                    } else {
                        "Tap any facility card to inspect equipment, timings, and faculty in-charge"
                    }
                )
            }
        }

        items(facilities, key = { it.id }) { facility ->
            FacilityShowcaseCard(
                facility = facility,
                isHindi = isHindi,
                onClick = { onOpenFacility(facility) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // 5B. OCCASION & THEME PHOTO GALLERY (LIVE CLOUD FIRESTORE)
        if (galleryPhotos.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        SectionHeader(
                            badgeText = if (isHindi) "विद्यालय फोटो गैलरी • अवसर एवं थीम" else "OCCASION & THEME PHOTO GALLERY",
                            title = if (isHindi) "समारोह, उत्सव एवं कैंपस की झलकियाँ" else "Campus Events & Occasion Highlights",
                            subtitle = if (isHindi) {
                                "एडमिन पैनल द्वारा अवसर (Occasion) एवं थीम नाम के साथ जोड़ी गई नवीनतम तस्वीरें"
                            } else {
                                "Live event gallery organized by Occasion & Theme from the Admin Panel"
                            },
                            actionLabel = if (isHindi) "फोटो जोड़ें (Admin)" else "Manage Gallery",
                            onActionClick = { onNavigateTab(PortalTab.ADMIN_PANEL) }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        galleryPhotos.forEach { photo ->
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.width(290.dp)
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(165.dp)
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
                                                .padding(10.dp)
                                        ) {
                                            Text(
                                                text = photo.occasionTheme,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.ExtraBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                        Text(
                                            text = photo.dateLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(10.dp)
                                        )
                                    }
                                    Text(
                                        text = photo.title,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. PRIDE OF GANGAULI: BOARD TOPPERS WALL
        item {
            Column(
                modifier = Modifier.padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SectionHeader(
                        badgeText = if (isHindi) "गंगौली के मेधावी सितारे • बोर्ड परिणाम 2026" else "PRIDE OF GANGAULI • BOARD TOPPERS 2026",
                        title = if (isHindi) "हमारे सर्वोच्च अंक प्राप्तकर्ता छात्र-छात्राएं" else "Merit Roll & District Rank Achievers",
                        subtitle = if (isHindi) {
                            "छात्र कार्ड पर टैप करके उनका पूर्ण डिजिटल अंक-पत्र (Marksheet) देखें"
                        } else {
                            "Tap any topper card to inspect their complete BSEB subject-wise marksheet"
                        },
                        actionLabel = if (isHindi) "सभी परिणाम" else "Result Portal",
                        onActionClick = { onNavigateTab(PortalTab.STUDENT_HUB) }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    toppers.forEach { topper ->
                        TopperSpotlightCard(
                            topper = topper,
                            isHindi = isHindi,
                            onClick = { onViewTopperMarksheet(topper.rollNo) }
                        )
                    }
                }
            }
        }

        // 7. LATEST OFFICIAL NOTICES PREVIEW
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(
                    badgeText = if (isHindi) "नवीनतम आधिकारिक सूचनाएं" else "OFFICIAL NOTICE BOARD",
                    title = if (isHindi) "सूचना पट्ट एवं परिपत्र" else "Latest Circulars & Updates",
                    actionLabel = if (isHindi) "सभी देखें" else "View All",
                    onActionClick = { onNavigateTab(PortalTab.NOTICES_CONTACT) }
                )

                notices.take(3).forEach { notice ->
                    HomeNoticePreviewRow(
                        notice = notice,
                        isHindi = isHindi,
                        onClick = { onOpenNotice(notice) }
                    )
                }
            }
        }

        // 8. INSTITUTIONAL WEB FOOTER
        item {
            SchoolWebFooter(
                isHindi = isHindi,
                onNavigateTab = onNavigateTab
            )
        }
    }
}

@Composable
private fun HeroCampusBanner(
    isHindi: Boolean,
    onApplyAdmission: () -> Unit,
    onCheckResults: () -> Unit,
    onOpenVirtualTour: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 390.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_campus_hero),
            contentDescription = stringResource(id = R.string.cd_campus_hero),
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )

        // Multi-stop editorial dark navy gradient overlay for crisp text readability
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            RoyalNavyDark.copy(alpha = 0.55f),
                            RoyalNavyDark.copy(alpha = 0.78f),
                            RoyalNavyPrimary.copy(alpha = 0.96f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Prestige Model School Badge
            Surface(
                color = SaffronGoldBright.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, SaffronGoldBright),
                shape = RoundedCornerShape(50)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = SaffronGoldBright,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isHindi) {
                            "बिहार सरकार का उत्कृष्ट +2 मॉडल विद्यालय • सिमरी, बक्सर"
                        } else {
                            "Premier +2 Model Senior Secondary School • Simri, Buxar"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = SaffronGoldBright,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Main Hero Title
            Text(
                text = if (isHindi) {
                    "उच्च माध्यमिक विद्यालय गंगौली\nसिमरी, बक्सर (बिहार)"
                } else {
                    "Uchch Madhyamik Vidyalaya\nGangauli, Simri, Buxar"
                },
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 28.sp,
                    lineHeight = 36.sp
                ),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            // Sanskrit Motto & Subtitle
            Text(
                text = if (isHindi) {
                    "\"सा विद्या या विमुक्तये\" — पवित्र गंगा तटवर्ती गंगौली की धरती पर कक्षा 9वीं से 12वीं (विज्ञान, कला एवं वाणिज्य) की विश्वस्तरीय स्मार्ट एवं संस्कारयुक्त शिक्षा।"
                } else {
                    "\"Sa Vidya Ya Vimuktaye\" — Empowering Class 9 to 12 students in Science, Arts & Commerce with Unnayan Smart Classes, Modern Science Labs, and Academic Excellence."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE2E8F0)
            )

            // Affiliation Badges Row
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HeroPillTag(icon = Icons.Default.Verified, text = "BSEB Code: 73045")
                HeroPillTag(icon = Icons.Default.AccountBalance, text = "UDISE+: 10300604502")
                HeroPillTag(icon = Icons.Default.School, text = if (isHindi) "कक्षा 9वीं – 12वीं (+2)" else "Classes IX – XII (+2)")
                HeroPillTag(icon = Icons.Default.LocationOn, text = "Pin: 802131")
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Primary CTA Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onApplyAdmission,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SaffronGoldBright,
                        contentColor = Color(0xFF1F1200)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("hero_apply_admission_btn")
                ) {
                    Icon(Icons.Default.HowToReg, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "ऑनलाइन नामांकन" else "Apply Admission",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                OutlinedButton(
                    onClick = onCheckResults,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("hero_check_results_btn")
                ) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "परीक्षा परिणाम" else "Exam Results",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            // Secondary Virtual Campus Tour Button
            Surface(
                onClick = onOpenVirtualTour,
                color = Color.White.copy(alpha = 0.14f),
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_virtual_tour_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = null,
                        tint = SaffronGoldBright,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) {
                            "360° इंटरएक्टिव कैंपस टूर देखें (स्मार्ट क्लास, विज्ञान लैब एवं खेल परिसर)"
                        } else {
                            "Launch 360° Interactive Campus Tour (Smart Class, Science Labs & Sports)"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroPillTag(icon: ImageVector, text: String) {
    Surface(
        color = Color.White.copy(alpha = 0.12f),
        contentColor = Color.White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = SaffronGoldBright, modifier = Modifier.size(13.dp))
            Text(text = text, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ImpactStatsGrid(isHindi: Boolean) {
    val stats = listOf(
        Triple("1,480+", if (isHindi) "नामांकित विद्यार्थी" else "Enrolled Students", Icons.Default.Groups),
        Triple("98.6%", if (isHindi) "बोर्ड उत्तीर्णता दर" else "BSEB Pass Rate", Icons.Default.EmojiEvents),
        Triple("42+", if (isHindi) "BPSC योग्य शिक्षक" else "Expert Faculty", Icons.Default.School),
        Triple("100%", if (isHindi) "स्मार्ट डिजिटल लैब" else "Smart Digital Labs", Icons.Default.Computer)
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            stats.take(2).forEach { (number, label, icon) ->
                StatMetricCard(
                    number = number,
                    label = label,
                    icon = icon,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            stats.drop(2).forEach { (number, label, icon) ->
                StatMetricCard(
                    number = number,
                    label = label,
                    icon = icon,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatMetricCard(
    number: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
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
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(22.dp))
                }
            }
            Column {
                Text(
                    text = number,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun QuickServicesGrid(
    isHindi: Boolean,
    onNavigateTab: (PortalTab) -> Unit,
    onOpenVirtualTour: () -> Unit
) {
    val services = listOf(
        Triple(
            if (isHindi) "ऑनलाइन नामांकन" else "Online Admission",
            if (isHindi) "सत्र 2026-27 टोकन" else "Apply Class 9–12",
            Icons.Default.HowToReg to PortalTab.ADMISSIONS
        ),
        Triple(
            if (isHindi) "डिजिटल अंक-पत्र" else "Exam Marksheet",
            if (isHindi) "रोल नंबर से परिणाम" else "Roll No. Lookup",
            Icons.Default.Assessment to PortalTab.STUDENT_HUB
        ),
        Triple(
            if (isHindi) "छात्रवृत्ति जांच" else "Scholarships",
            if (isHindi) "कन्या उत्थान व साइकिल" else "₹25,000 & DBT",
            Icons.Default.AccountBalanceWallet to PortalTab.ADMISSIONS
        ),
        Triple(
            if (isHindi) "कक्षा समय-सारणी" else "Class Routine",
            if (isHindi) "पीरियड एवं लैब समय" else "Daily Timetable",
            Icons.Default.Schedule to PortalTab.ACADEMICS
        ),
        Triple(
            if (isHindi) "ई-लाइब्रेरी नोट्स" else "E-Library Notes",
            if (isHindi) "बोर्ड सूत्र एवं मॉडल पेपर" else "Notes & Formulas",
            Icons.AutoMirrored.Filled.MenuBook to PortalTab.ACADEMICS
        ),
        Triple(
            if (isHindi) "एडमिन कंट्रोल पैनल" else "Admin Panel",
            if (isHindi) "फोटो, नामांकन, QR व रिजल्ट" else "Gallery, QR & Results",
            Icons.Default.AdminPanelSettings to PortalTab.ADMIN_PANEL
        )
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        services.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { (title, sub, iconAndTab) ->
                    val (icon, targetTab) = iconAndTab
                    Surface(
                        onClick = { onNavigateTab(targetTab) },
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        shadowElevation = 1.dp,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                shape = CircleShape,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
                                }
                            }
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = sub,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrincipalVisionCard(
    isHindi: Boolean,
    onMeetFaculty: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, SaffronGoldSecondary.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = RoyalNavyPrimary,
                    contentColor = SaffronGoldBright,
                    shape = CircleShape,
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHindi) "प्राचार्य की कलम से • विद्यालय संदेश" else "FROM THE PRINCIPAL'S DESK",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isHindi) "डॉ. राकेश कुमार पाण्डेय" else "Dr. Rakesh Kumar Pandey",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isHindi) {
                            "प्राचार्य, उच्च माध्यमिक विद्यालय गंगौली, सिमरी, बक्सर"
                        } else {
                            "Principal, UMV Gangauli, Simri, Buxar (M.Sc., Ph.D., B.Ed.)"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = if (isHindi) {
                    "\"बक्सर जिले के ऐतिहासिक सिमरी प्रखंड अंतर्गत ग्राम गंगौली का यह विद्यालय आज डिजिटल क्रांति और शैक्षणिक उत्कृष्टता का प्रतीक बन चुका है। उन्नयन बिहार स्मार्ट क्लासरूम, सुसज्जित विज्ञान प्रयोगशालाओं और BPSC द्वारा चयनित समर्पित शिक्षकों के मार्गदर्शन में हमारे विद्यार्थी मैट्रिक, इंटरमीडिएट, IIT-JEE, NEET और रक्षा सेवाओं में निरंतर परचम लहरा रहे हैं।\""
                } else {
                    "\"Situated in the culturally rich village of Gangauli in Simri Block, Buxar, our school blends Bihar's timeless scholarly heritage with 21st-century Unnayan Smart Classrooms, advanced STEM laboratories, and dedicated BPSC-selected faculty to nurture global leaders.\""
                },
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = EmeraldSuccessBg,
                    contentColor = EmeraldSuccess,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isHindi) "✓ 75% उपस्थिति एवं अनुशासन संकल्प" else "✓ 100% Academic Discipline",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                TextButton(onClick = onMeetFaculty) {
                    Text(if (isHindi) "शिक्षक निर्देशिका देखें" else "Meet All Faculty")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun FacilityShowcaseCard(
    facility: FacilityItem,
    isHindi: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag("facility_card_${facility.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
            ) {
                if (facility.imageRes != null) {
                    Image(
                        painter = painterResource(id = facility.imageRes),
                        contentDescription = facility.title.get(isHindi),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f))
                            )
                        )
                )

                Surface(
                    color = SaffronGoldBright,
                    contentColor = Color(0xFF1E1200),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = facility.badgeText.get(isHindi),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = facility.title.get(isHindi),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = facility.subtitle.get(isHindi),
                        style = MaterialTheme.typography.bodySmall,
                        color = SaffronGoldBright
                    )
                }
            }

            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = facility.description.get(isHindi),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = facility.timing.get(isHindi),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = if (isHindi) "विवरण देखें →" else "Explore Lab →",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TopperSpotlightCard(
    topper: TopperStudent,
    isHindi: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(275.dp)
            .testTag("topper_card_${topper.rollNo}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, SaffronGoldSecondary.copy(alpha = 0.4f))
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
                    color = RoyalNavyPrimary,
                    contentColor = SaffronGoldBright,
                    shape = CircleShape,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = topper.percentage,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Roll: ${topper.rollNo}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column {
                Text(
                    text = topper.name.get(isHindi),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${if (isHindi) "पिता:" else "S/D of:"} ${topper.fatherName.get(isHindi)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = topper.streamClass.get(isHindi),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Surface(
                color = EmeraldSuccessBg,
                contentColor = EmeraldSuccess,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = topper.achievementBadge.get(isHindi),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (isHindi) "प्राप्तांक:" else "Marks:"} ${topper.marksObtained}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isHindi) "अंक-पत्र देखें →" else "View Marksheet →",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HomeNoticePreviewRow(
    notice: NoticeItem,
    isHindi: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = if (notice.isUrgent) CrimsonUrgentBg else MaterialTheme.colorScheme.primaryContainer,
                contentColor = if (notice.isUrgent) CrimsonUrgent else MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (notice.isUrgent) Icons.Default.NotificationsActive else Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notice.categoryLabel.get(isHindi),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• ${notice.date.get(isHindi)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = notice.title.get(isHindi),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
