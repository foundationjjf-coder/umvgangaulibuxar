package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.PortalTab
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.SchoolViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val schoolViewModel: SchoolViewModel = viewModel()
            val uiState by schoolViewModel.uiState.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = uiState.isDarkTheme) {
                GangauliAuthRoot(viewModel = schoolViewModel)
            }
        }
    }
}

@Composable
fun GangauliAuthRoot(
    viewModel: SchoolViewModel
) {
    val auth = remember { FirebaseAuth.getInstance() }
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val user = currentUser
    if (user == null) {
        LaunchedEffect(Unit) {
            viewModel.onUserSignedOut()
        }
        SchoolAuthGateScreen(
            isHindi = uiState.isHindi,
            onToggleLanguage = { viewModel.toggleLanguage() },
            onAuthSuccess = {
                auth.currentUser?.uid?.let { uid ->
                    viewModel.onUserAuthenticated(uid)
                }
            }
        )
    } else {
        LaunchedEffect(user.uid) {
            viewModel.onUserAuthenticated(user.uid)
        }
        GangauliSchoolPortalApp(
            viewModel = viewModel,
            adminEmail = user.email ?: "admin@umvgangauli.in",
            onSignOut = {
                signOutUser(
                    context = context,
                    credentialManager = credentialManager,
                    onSignOutComplete = { viewModel.onUserSignedOut() },
                    scope = scope
                )
            }
        )
    }
}

@Composable
fun GangauliSchoolPortalApp(
    viewModel: SchoolViewModel,
    adminEmail: String = "admin@umvgangauli.in",
    onSignOut: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val repo = viewModel.repository

    // Handle system back button when on secondary tabs
    BackHandler(enabled = uiState.currentTab != PortalTab.HOME) {
        viewModel.selectTab(PortalTab.HOME)
    }

    // Auto-dismiss toast banner after 4 seconds
    LaunchedEffect(uiState.bannerToastMessage) {
        if (uiState.bannerToastMessage != null) {
            delay(4000L)
            viewModel.clearBannerToast()
        }
    }

    val currentTickerNotice = repo.liveTickerNotices.getOrElse(uiState.activeTickerIndex) {
        repo.liveTickerNotices.first()
    }.get(uiState.isHindi)

    val currentTimetableSlots = remember(uiState.selectedTimetableClass, uiState.selectedTimetableDay) {
        repo.getTimetableFor(uiState.selectedTimetableClass, uiState.selectedTimetableDay)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 680.dp

        Scaffold(
            topBar = {
                Column {
                    SchoolPortalTopBar(
                        isHindi = uiState.isHindi,
                        isDarkTheme = uiState.isDarkTheme,
                        onToggleLanguage = { viewModel.toggleLanguage() },
                        onToggleTheme = { viewModel.toggleDarkTheme() },
                        onOpenVirtualTour = { viewModel.setShowCampusTourModal(true) }
                    )
                    LiveNoticeTickerBar(
                        noticeText = currentTickerNotice,
                        isHindi = uiState.isHindi,
                        onClickTicker = { viewModel.selectTab(PortalTab.NOTICES_CONTACT) }
                    )
                }
            },
            bottomBar = {
                if (!isWideScreen) {
                    SchoolBottomNavigationBar(
                        currentTab = uiState.currentTab,
                        isHindi = uiState.isHindi,
                        onSelectTab = { viewModel.selectTab(it) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    if (isWideScreen) {
                        SchoolNavigationRail(
                            currentTab = uiState.currentTab,
                            isHindi = uiState.isHindi,
                            onSelectTab = { viewModel.selectTab(it) }
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        when (uiState.currentTab) {
                            PortalTab.HOME -> HomeScreen(
                                isHindi = uiState.isHindi,
                                facilities = repo.campusFacilities,
                                toppers = repo.topperStudents,
                                notices = repo.noticesList,
                                galleryPhotos = uiState.cloudGalleryPhotos,
                                onNavigateTab = { viewModel.selectTab(it) },
                                onOpenFacility = { viewModel.openFacilityModal(it) },
                                onOpenNotice = { viewModel.openNoticeModal(it) },
                                onOpenVirtualTour = { viewModel.setShowCampusTourModal(true) },
                                onViewTopperMarksheet = { rollNo ->
                                    viewModel.searchResultByRoll(rollNo)
                                    viewModel.selectTab(PortalTab.STUDENT_HUB)
                                }
                            )

                            PortalTab.ACADEMICS -> AcademicsScreen(
                                isHindi = uiState.isHindi,
                                streams = repo.academicStreams,
                                selectedClassId = uiState.selectedTimetableClass,
                                selectedDayId = uiState.selectedTimetableDay,
                                timetableSlots = currentTimetableSlots,
                                studyMaterials = repo.studyMaterials,
                                selectedStudyFilter = uiState.selectedStudyClassFilter,
                                facultyMembers = repo.facultyMembers,
                                cloudTeachers = uiState.cloudTeachers,
                                facultySearchQuery = uiState.facultySearchQuery,
                                selectedDepartment = uiState.selectedFacultyDepartment,
                                onSelectTimetableClass = { viewModel.setTimetableClass(it) },
                                onSelectTimetableDay = { viewModel.setTimetableDay(it) },
                                onSelectStudyFilter = { viewModel.setStudyClassFilter(it) },
                                onOpenStudyMaterial = { viewModel.openStudyMaterialModal(it) },
                                onFacultySearchChange = { viewModel.setFacultySearchQuery(it) },
                                onSelectDepartment = { viewModel.setFacultyDepartment(it) },
                                onNavigateTab = { viewModel.selectTab(it) }
                            )

                            PortalTab.ADMISSIONS -> AdmissionsScreen(
                                isHindi = uiState.isHindi,
                                savedAdmissions = uiState.savedAdmissions,
                                cloudAdmissions = uiState.cloudAdmissions,
                                schoolAccountConfig = uiState.schoolAccountConfig,
                                latestGeneratedAdmission = uiState.latestGeneratedAdmission,
                                docChecklist = repo.admissionDocChecklist,
                                checkedDocIds = uiState.checkedDocIds,
                                villages = repo.simriBlockVillages,
                                scholarshipSchemes = repo.scholarshipSchemes,
                                scholarshipFilterClass = uiState.scholarshipFilterClass,
                                scholarshipFilterGender = uiState.scholarshipFilterGender,
                                scholarshipFilterPercent = uiState.scholarshipFilterPercent,
                                onSubmitAdmission = { sName, fName, mName, cls, strm, pct, mob, vill, cat, aadh ->
                                    viewModel.submitAdmissionApplication(
                                        studentName = sName,
                                        fatherName = fName,
                                        motherName = mName,
                                        classApplied = cls,
                                        streamApplied = strm,
                                        prevPercentage = pct,
                                        mobileNumber = mob,
                                        villagePanchayat = vill,
                                        category = cat,
                                        aadhaarLast4 = aadh
                                    )
                                },
                                onDismissReceiptModal = { viewModel.dismissAdmissionReceiptModal() },
                                onToggleDocCheck = { viewModel.toggleDocumentCheck(it) },
                                onUpdateScholarshipFilter = { cls, gen, pct ->
                                    viewModel.updateScholarshipFilter(cls, gen, pct)
                                },
                                onNavigateTab = { viewModel.selectTab(it) }
                            )

                            PortalTab.STUDENT_HUB -> StudentHubScreen(
                                isHindi = uiState.isHindi,
                                rollNumberQuery = uiState.rollNumberQuery,
                                searchedResult = uiState.searchedResult,
                                resultSearchError = uiState.resultSearchError,
                                allResults = repo.studentResults,
                                cloudLiveResults = uiState.cloudLiveResults,
                                homeworkList = repo.homeworkList,
                                completedHomeworkIds = uiState.completedHomeworkIds,
                                savedRequests = uiState.savedRequests,
                                onRollQueryChange = { viewModel.updateRollNumberQuery(it) },
                                onSearchResult = { viewModel.searchResultByRoll(it) },
                                onToggleHomework = { viewModel.toggleHomeworkCompleted(it) },
                                onSubmitRequest = { name, clsRoll, type, msg ->
                                    viewModel.submitStudentRequest(name, clsRoll, type, msg)
                                },
                                onNavigateTab = { viewModel.selectTab(it) }
                            )

                            PortalTab.NOTICES_CONTACT -> NoticesContactScreen(
                                isHindi = uiState.isHindi,
                                notices = repo.noticesList,
                                selectedCategory = uiState.selectedNoticeCategory,
                                searchQuery = uiState.noticeSearchQuery,
                                bookmarkedIds = uiState.bookmarkedNoticeIds,
                                showBookmarkedOnly = uiState.showBookmarkedOnly,
                                calendarEvents = repo.academicCalendarEvents,
                                onSelectCategory = { viewModel.setNoticeCategory(it) },
                                onSearchQueryChange = { viewModel.setNoticeSearchQuery(it) },
                                onToggleBookmark = { viewModel.toggleBookmarkNotice(it) },
                                onToggleShowBookmarkedOnly = { viewModel.toggleShowBookmarkedOnly() },
                                onOpenNotice = { viewModel.openNoticeModal(it) },
                                onBookCampusVisit = { name, phone, purpose ->
                                    viewModel.submitStudentRequest(
                                        studentName = name,
                                        classAndRoll = "Parent Visit ($phone)",
                                        requestType = "VISIT",
                                        message = purpose
                                    )
                                },
                                onNavigateTab = { viewModel.selectTab(it) }
                            )

                            PortalTab.ADMIN_PANEL -> AdminPanelScreen(
                                isHindi = uiState.isHindi,
                                adminEmail = adminEmail,
                                galleryPhotos = uiState.cloudGalleryPhotos,
                                admissions = uiState.cloudAdmissions,
                                schoolAccountConfig = uiState.schoolAccountConfig,
                                liveResults = uiState.cloudLiveResults,
                                registeredTeachers = uiState.cloudTeachers,
                                onAddGalleryPhoto = { occ, title, src, date ->
                                    viewModel.adminAddGalleryPhoto(occ, title, src, date)
                                },
                                onDeleteGalleryPhoto = { id ->
                                    viewModel.adminDeleteGalleryPhoto(id)
                                },
                                onCreateAdmission = { sName, fName, cls, strm, pct, mob, vill ->
                                    viewModel.adminCreateAdmission(sName, fName, cls, strm, pct, mob, vill)
                                },
                                onUpdateAdmissionStatus = { id, status ->
                                    viewModel.adminUpdateAdmissionStatus(id, status)
                                },
                                onDeleteAdmission = { id ->
                                    viewModel.adminDeleteAdmission(id)
                                },
                                onSaveAccountConfig = { holder, bank, acc, ifsc, upi, qrTitle, qrUri ->
                                    viewModel.adminSaveSchoolAccountConfig(holder, bank, acc, ifsc, upi, qrTitle, qrUri)
                                },
                                onPublishClassResult = { roll, sName, fName, cls, marks, max, div, subjs, live ->
                                    viewModel.adminPublishClassResult(roll, sName, fName, cls, marks, max, div, subjs, live)
                                },
                                onToggleResultLive = { id, live ->
                                    viewModel.adminToggleResultLive(id, live)
                                },
                                onSetClassAllLive = { cls, live ->
                                    viewModel.adminSetClassAllLive(cls, live)
                                },
                                onDeleteClassResult = { id ->
                                    viewModel.adminDeleteClassResult(id)
                                },
                                onRegisterTeacher = { name, desig, dept, subj, qual, exp, phone ->
                                    viewModel.adminRegisterTeacher(name, desig, dept, subj, qual, exp, phone)
                                },
                                onDeleteTeacher = { id ->
                                    viewModel.adminDeleteTeacher(id)
                                },
                                onSignOut = onSignOut,
                                onNavigateTab = { viewModel.selectTab(it) }
                            )
                        }
                    }
                }

                // Floating Confirmation Toast Banner
                AnimatedVisibility(
                    visible = uiState.bannerToastMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Surface(
                        color = EmeraldSuccess,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(14.dp),
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Text(
                                text = uiState.bannerToastMessage ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                        }
                    }
                }
            }
        }
    }

    // Interactive Modals
    uiState.selectedFacilityModal?.let { fac ->
        FacilityDetailModal(
            facility = fac,
            isHindi = uiState.isHindi,
            onDismiss = { viewModel.openFacilityModal(null) }
        )
    }

    uiState.selectedNoticeModal?.let { notice ->
        NoticeCircularModal(
            notice = notice,
            isHindi = uiState.isHindi,
            isBookmarked = uiState.bookmarkedNoticeIds.contains(notice.id),
            onToggleBookmark = { viewModel.toggleBookmarkNotice(notice.id) },
            onDismiss = { viewModel.openNoticeModal(null) }
        )
    }

    uiState.selectedStudyMaterialModal?.let { mat ->
        StudyMaterialReaderModal(
            material = mat,
            isHindi = uiState.isHindi,
            onDismiss = { viewModel.openStudyMaterialModal(null) }
        )
    }

    if (uiState.showCampusTourModal) {
        VirtualCampusTourModal(
            facilities = repo.campusFacilities,
            isHindi = uiState.isHindi,
            onDismiss = { viewModel.setShowCampusTourModal(false) },
            onNavigateToAdmissions = { viewModel.selectTab(PortalTab.ADMISSIONS) }
        )
    }
}

private fun getTabIcons(tab: PortalTab): Pair<ImageVector, ImageVector> = when (tab) {
    PortalTab.HOME -> Icons.Filled.AccountBalance to Icons.Outlined.AccountBalance
    PortalTab.ACADEMICS -> Icons.Filled.School to Icons.Outlined.School
    PortalTab.ADMISSIONS -> Icons.Filled.HowToReg to Icons.Outlined.HowToReg
    PortalTab.STUDENT_HUB -> Icons.Filled.WorkspacePremium to Icons.Outlined.WorkspacePremium
    PortalTab.NOTICES_CONTACT -> Icons.Filled.Campaign to Icons.Outlined.Campaign
    PortalTab.ADMIN_PANEL -> Icons.Filled.AdminPanelSettings to Icons.Outlined.AdminPanelSettings
}

@Composable
private fun SchoolBottomNavigationBar(
    currentTab: PortalTab,
    isHindi: Boolean,
    onSelectTab: (PortalTab) -> Unit
) {
    NavigationBar(
        windowInsets = WindowInsets.navigationBars,
        tonalElevation = 6.dp,
        modifier = Modifier.testTag("school_bottom_navigation")
    ) {
        PortalTab.entries.forEach { tab ->
            val selected = currentTab == tab
            val (filledIcon, outlinedIcon) = getTabIcons(tab)
            NavigationBarItem(
                selected = selected,
                onClick = { onSelectTab(tab) },
                icon = {
                    Icon(
                        imageVector = if (selected) filledIcon else outlinedIcon,
                        contentDescription = tab.label.get(isHindi)
                    )
                },
                label = {
                    Text(
                        text = tab.label.get(isHindi),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                modifier = Modifier.testTag("nav_tab_${tab.route}")
            )
        }
    }
}

@Composable
private fun SchoolNavigationRail(
    currentTab: PortalTab,
    isHindi: Boolean,
    onSelectTab: (PortalTab) -> Unit
) {
    NavigationRail(
        modifier = Modifier
            .fillMaxHeight()
            .testTag("school_navigation_rail")
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        PortalTab.entries.forEach { tab ->
            val selected = currentTab == tab
            val (filledIcon, outlinedIcon) = getTabIcons(tab)
            NavigationRailItem(
                selected = selected,
                onClick = { onSelectTab(tab) },
                icon = {
                    Icon(
                        imageVector = if (selected) filledIcon else outlinedIcon,
                        contentDescription = tab.label.get(isHindi)
                    )
                },
                label = {
                    Text(
                        text = tab.label.get(isHindi),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                modifier = Modifier.testTag("rail_tab_${tab.route}")
            )
        }
    }
}
