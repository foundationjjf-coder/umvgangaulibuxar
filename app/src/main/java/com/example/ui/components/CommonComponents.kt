package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.*
import com.example.ui.theme.*

fun launchPhoneDialer(context: Context, phoneNumber: String = "+916183245101") {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        context.startActivity(intent)
    } catch (_: Exception) {}
}

fun launchEmailIntent(context: Context, subject: String = "Inquiry - UMV Gangauli, Simri, Buxar") {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:principal.umvgangauli.buxar@bihar.gov.in")
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        context.startActivity(intent)
    } catch (_: Exception) {}
}

fun launchMapsLocation(context: Context) {
    try {
        val uri = Uri.parse("geo:25.6458,84.1162?q=Uchch+Madhyamik+Vidyalaya+Gangauli+Simri+Buxar+Bihar")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (_: Exception) {}
}

fun shareTextContent(context: Context, title: String, body: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "$title\n\n$body\n\n— उच्च माध्यमिक विद्यालय गंगौली, सिमरी, बक्सर (बिहार) | UMV Gangauli Portal")
        }
        context.startActivity(Intent.createChooser(intent, title))
    } catch (_: Exception) {}
}

@Composable
fun SchoolPortalTopBar(
    isHindi: Boolean,
    isDarkTheme: Boolean,
    onToggleLanguage: () -> Unit,
    onToggleTheme: () -> Unit,
    onOpenVirtualTour: () -> Unit
) {
    val context = LocalContext.current
    Surface(
        color = RoyalNavyPrimary,
        contentColor = Color.White,
        shadowElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("school_portal_top_bar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            // Top micro institutional strip (like elite university/school websites)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RoyalNavyDark)
                    .padding(horizontal = 14.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = SaffronGoldBright,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isHindi) {
                            "बिहार शिक्षा विभाग • BSEB कोड: 73045 • UDISE+: 10300604502"
                        } else {
                            "Dept. of Education, Bihar • BSEB: 73045 • UDISE+: 10300604502"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFE2E8F0),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isHindi) "360° कैंपस" else "360° Tour",
                        style = MaterialTheme.typography.labelSmall,
                        color = SaffronGoldBright,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onOpenVirtualTour() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .testTag("top_bar_virtual_tour_btn")
                    )
                }
            }

            // Main Masthead Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // School Crest Emblem
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(2.dp, SaffronGoldBright, CircleShape)
                            .background(RoyalNavyDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_school_crest_fg),
                            contentDescription = stringResource(id = R.string.cd_school_crest),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) {
                                "उच्च माध्यमिक विद्यालय गंगौली"
                            } else {
                                "U.M. Vidyalaya Gangauli"
                            },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = PlayfairDisplayFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = SaffronGoldBright,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (isHindi) {
                                    "सिमरी, बक्सर (बिहार) - 802131 • +2 मॉडल स्कूल"
                                } else {
                                    "Simri, Buxar, Bihar - 802131 • +2 Model School"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFCBD5E1),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Action Controls: Bilingual Switcher + Theme + Call
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Language Toggle Pill (हिंदी / EN)
                    Surface(
                        onClick = onToggleLanguage,
                        shape = RoundedCornerShape(50),
                        color = SaffronGoldBright,
                        contentColor = Color(0xFF1E1300),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("language_toggle_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = stringResource(id = R.string.cd_language_switch),
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (isHindi) "EN" else "हिंदी",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Theme Toggle Button
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = stringResource(id = R.string.cd_theme_switch),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiveNoticeTickerBar(
    noticeText: String,
    isHindi: Boolean,
    onClickTicker: () -> Unit
) {
    Surface(
        onClick = onClickTicker,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("live_notice_ticker_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                color = CrimsonUrgent,
                contentColor = Color.White,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = if (isHindi) "सूचना LIVE" else "LIVE UPDATE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }

            AnimatedContent(
                targetState = noticeText,
                transitionSpec = {
                    (slideInVertically { height -> height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> -height } + fadeOut()
                    )
                },
                modifier = Modifier.weight(1f),
                label = "NoticeTickerAnimation"
            ) { targetText ->
                Text(
                    text = targetText,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(
    badgeText: String,
    title: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(SaffronGoldSecondary)
                )
                Text(
                    text = badgeText.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }

            if (actionLabel != null && onActionClick != null) {
                TextButton(
                    onClick = onActionClick,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SchoolWebFooter(
    isHindi: Boolean,
    onNavigateTab: (PortalTab) -> Unit
) {
    val context = LocalContext.current
    Surface(
        color = RoyalNavyPrimary,
        contentColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_school_crest_fg),
                    contentDescription = null,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, SaffronGoldBright, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column {
                    Text(
                        text = if (isHindi) "उच्च माध्यमिक विद्यालय गंगौली" else "Uchch Madhyamik Vidyalaya Gangauli",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Text(
                        text = if (isHindi) "प्रखंड - सिमरी, जिला - बक्सर, बिहार (802131)" else "Block - Simri, Dist - Buxar, Bihar (802131)",
                        style = MaterialTheme.typography.bodySmall,
                        color = SaffronGoldBright
                    )
                }
            }

            Text(
                text = if (isHindi) {
                    "\"सा विद्या या विमुक्तये\" — पवित्र मां गंगा के तटवर्ती सिमरी अंचल में स्थित यह राजकीय +2 उच्च माध्यमिक विद्यालय ग्रामीण प्रतिभाओं को आधुनिक विज्ञान, डिजिटल तकनीक, खेलकूद और मानवीय मूल्यों के साथ विश्वस्तरीय शिक्षा प्रदान करने के लिए समर्पित है।"
                } else {
                    "\"Sa Vidya Ya Vimuktaye\" — Located in the historic Ganga belt of Simri Block, Buxar, our +2 Senior Secondary Institution empowers students with Unnayan Smart Classrooms, STEM laboratories, and holistic excellence."
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFCBD5E1)
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

            // Quick Links Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isHindi) "महत्वपूर्ण अनुभाग" else "QUICK PORTAL LINKS",
                        style = MaterialTheme.typography.labelMedium,
                        color = SaffronGoldBright,
                        fontWeight = FontWeight.Bold
                    )
                    PortalTab.entries.forEach { tab ->
                        Text(
                            text = "• ${tab.label.get(isHindi)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            modifier = Modifier
                                .clickable { onNavigateTab(tab) }
                                .padding(vertical = 2.dp)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isHindi) "आधिकारिक विवरण" else "INSTITUTIONAL CODES",
                        style = MaterialTheme.typography.labelMedium,
                        color = SaffronGoldBright,
                        fontWeight = FontWeight.Bold
                    )
                    Text("• BSEB Code: 73045", style = MaterialTheme.typography.bodySmall, color = Color(0xFFE2E8F0))
                    Text("• UDISE+: 10300604502", style = MaterialTheme.typography.bodySmall, color = Color(0xFFE2E8F0))
                    Text("• Block: Simri, Buxar", style = MaterialTheme.typography.bodySmall, color = Color(0xFFE2E8F0))
                    Text("• Pin Code: 802131", style = MaterialTheme.typography.bodySmall, color = Color(0xFFE2E8F0))
                    Text(
                        text = if (isHindi) "• समय: 09:00 AM - 04:00 PM" else "• Hours: 09:00 AM - 04:00 PM",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { launchPhoneDialer(context) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, SaffronGoldBright)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isHindi) "हेल्पलाइन कॉल" else "Call Helpline", style = MaterialTheme.typography.labelMedium)
                }

                OutlinedButton(
                    onClick = {
                        shareTextContent(
                            context = context,
                            title = "उच्च माध्यमिक विद्यालय गंगौली, सिमरी, बक्सर (बिहार)",
                            body = "उच्च माध्यमिक विद्यालय गंगौली (BSEB Code: 73045) के आधिकारिक डिजिटल पोर्टल पर ऑनलाइन नामांकन, परीक्षा परिणाम, स्मार्ट क्लास और छात्रवृत्ति की जानकारी देखें!"
                        )
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isHindi) "पोर्टल शेयर करें" else "Share Portal", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun FacilityDetailModal(
    facility: FacilityItem,
    isHindi: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 680.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
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
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.78f))
                                )
                            )
                    )
                    Surface(
                        color = SaffronGoldBright,
                        contentColor = Color(0xFF1F1200),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = facility.badgeText.get(isHindi),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = if (isHindi) "बंद करें" else "Close",
                            tint = Color.White
                        )
                    }
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = facility.title.get(isHindi),
                            style = MaterialTheme.typography.headlineSmall,
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
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = facility.description.get(isHindi),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isHindi) "प्रमुख विशेषताएं एवं उपकरण:" else "Key Infrastructure & Equipment:",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            facility.highlights.forEach { point ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = EmeraldSuccess,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Text(
                                        text = point.get(isHindi),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isHindi) "समय-सारणी" else "Lab / Facility Hours",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = facility.timing.get(isHindi),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isHindi) "प्रभारी शिक्षक" else "Faculty In-Charge",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = facility.inCharge.get(isHindi),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isHindi) "बंद करें (Close)" else "Close Details")
                    }
                }
            }
        }
    }
}

@Composable
fun NoticeCircularModal(
    notice: NoticeItem,
    isHindi: Boolean,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 660.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Official Letterhead Banner
                Surface(
                    color = RoyalNavyPrimary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = notice.refNo,
                                style = MaterialTheme.typography.labelMedium,
                                color = SaffronGoldBright,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = notice.date.get(isHindi),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isHindi) {
                                "कार्यालय: प्राचार्य, उच्च माध्यमिक विद्यालय गंगौली, सिमरी, बक्सर"
                            } else {
                                "OFFICE OF THE PRINCIPAL, UMV GANGAULI, SIMRI, BUXAR"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Text(
                            text = notice.title.get(isHindi),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Official Circular Body
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = notice.fullCircular.get(isHindi),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isHindi) "आज्ञा से / जारीकर्ता:" else "Issued By:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = notice.issuedBy.get(isHindi),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedIconButton(onClick = onToggleBookmark) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = if (isHindi) "सहेजें" else "Bookmark",
                                tint = if (isBookmarked) SaffronGoldSecondary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        OutlinedIconButton(
                            onClick = {
                                shareTextContent(
                                    context = context,
                                    title = notice.title.get(isHindi),
                                    body = notice.fullCircular.get(isHindi)
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = if (isHindi) "शेयर करें" else "Share"
                            )
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isHindi) "बंद करें" else "Close Circular")
                }
            }
        }
    }
}

@Composable
fun StudyMaterialReaderModal(
    material: StudyMaterial,
    isHindi: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 660.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${material.classLevel} • ${material.subject.get(isHindi)}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = material.title.get(isHindi),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = material.chaptersOrPages.get(isHindi),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isHindi) "प्रमुख अध्ययन बिंदु (Key Syllabus Topics):" else "Key Syllabus Topics Covered:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        material.keyTopics.forEach { topic ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoStories,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(top = 2.dp)
                                )
                                Text(
                                    text = topic.get(isHindi),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isHindi) "डिजिटल रिवीजन नोट्स एवं सूत्र सारांश:" else "Digital Revision Notes & Formula Summary:",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = material.summaryNotes.get(isHindi),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            shareTextContent(
                                context = context,
                                title = material.title.get(isHindi),
                                body = material.summaryNotes.get(isHindi)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isHindi) "नोट्स शेयर करें" else "Share Notes")
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isHindi) "पढ़ लिया (Done)" else "Done Reading")
                    }
                }
            }
        }
    }
}

@Composable
fun VirtualCampusTourModal(
    facilities: List<FacilityItem>,
    isHindi: Boolean,
    onDismiss: () -> Unit,
    onNavigateToAdmissions: () -> Unit
) {
    var activeStop by remember { mutableIntStateOf(0) }
    val currentFacility = facilities.getOrElse(activeStop) { facilities.first() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 680.dp),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                ) {
                    Image(
                        painter = painterResource(id = currentFacility.imageRes ?: R.drawable.img_campus_hero),
                        contentDescription = currentFacility.title.get(isHindi),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Black.copy(alpha = 0.35f), Color.Black.copy(alpha = 0.82f))
                                )
                            )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = SaffronGoldBright,
                            contentColor = Color(0xFF1F1200),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = if (isHindi) {
                                    "360° वर्चुअल कैंपस भ्रमण • पड़ाव ${activeStop + 1}/${facilities.size}"
                                } else {
                                    "360° Virtual Campus Tour • Stop ${activeStop + 1}/${facilities.size}"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = currentFacility.title.get(isHindi),
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentFacility.subtitle.get(isHindi),
                            style = MaterialTheme.typography.bodySmall,
                            color = SaffronGoldBright
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Step indicator chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        facilities.forEachIndexed { idx, _ ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        if (idx == activeStop) SaffronGoldSecondary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { activeStop = idx }
                            )
                        }
                    }

                    Text(
                        text = currentFacility.description.get(isHindi),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    currentFacility.highlights.forEach { hl ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(17.dp)
                            )
                            Text(
                                text = hl.get(isHindi),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                activeStop = if (activeStop > 0) activeStop - 1 else facilities.lastIndex
                            }
                        ) {
                            Text(if (isHindi) "← पिछला पड़ाव" else "← Previous")
                        }

                        Button(
                            onClick = {
                                if (activeStop < facilities.lastIndex) {
                                    activeStop += 1
                                } else {
                                    onDismiss()
                                    onNavigateToAdmissions()
                                }
                            }
                        ) {
                            Text(
                                if (activeStop < facilities.lastIndex) {
                                    if (isHindi) "अगला पड़ाव →" else "Next Stop →"
                                } else {
                                    if (isHindi) "नामांकन हेतु आवेदन करें" else "Apply for Admission"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
