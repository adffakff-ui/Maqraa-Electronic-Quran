package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.*
import com.example.ui.attendance.AttendanceScreen
import com.example.ui.circles.CircleDetailScreen
import com.example.ui.dashboard.SupervisorDashboardScreen
import com.example.ui.login.LoginScreen
import com.example.ui.navigation.AppNavigationDrawerSheet
import com.example.ui.parent.ParentPortalScreen
import com.example.ui.recitation.AudioRecitationScreen
import com.example.ui.reports.ReportsAndCertificatesScreen
import com.example.ui.supervisor.SupervisorRegistrationScreen
import com.example.ui.teacherportal.TeacherPortalScreen
import com.example.ui.teachers.TeacherManagementScreen
import com.example.ui.theme.*
import kotlinx.coroutines.launch

object AppDestinations {
    const val LOGIN = "login"
    const val SUPERVISOR_REGISTRATION = "supervisor_registration"
    const val SUPERVISOR_DASHBOARD = "supervisor_dashboard"
    const val TEACHERS = "teachers"
    const val CIRCLE_DETAIL = "circle_detail"
    const val ATTENDANCE = "attendance"
    const val TEACHER_PORTAL = "teacher_portal"
    const val AUDIO_RECITATION = "audio_recitation"
    const val PARENT_PORTAL = "parent_portal"
    const val REPORTS = "reports"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SocietyApp()
            }
        }
    }
}

@Composable
fun SocietyApp() {
    val navController = rememberNavController()

    // Current Active Tenant & State
    var currentTenant by remember { mutableStateOf(SocietyRepository.tenants.first()) }
    var activeUserRole by remember { mutableStateOf(UserRole.SUPERVISOR) }
    var selectedRingForDetail by remember { mutableStateOf(SocietyRepository.getInitialRings().first()) }
    var selectedStudentForRecitation by remember { mutableStateOf(SocietyRepository.getInitialCircleStudents().first()) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide bottom bar on login and supervisor registration
    val showBottomBar = currentRoute != null &&
            currentRoute != AppDestinations.LOGIN &&
            currentRoute != AppDestinations.SUPERVISOR_REGISTRATION

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                if (showBottomBar) {
                    AppNavigationDrawerSheet(
                        currentRoute = currentRoute,
                        currentTenant = currentTenant,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(AppDestinations.SUPERVISOR_DASHBOARD) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onCloseDrawer = {
                            coroutineScope.launch { drawerState.close() }
                        },
                        onSwitchTenant = {
                            navController.navigate(AppDestinations.LOGIN) {
                                popUpTo(0)
                            }
                        },
                        onLogout = {
                            navController.navigate(AppDestinations.LOGIN) {
                                popUpTo(0)
                            }
                        }
                    )
                }
            }
        ) {
            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        NavigationBar(
                            containerColor = Color.White,
                            tonalElevation = 8.dp,
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .testTag("app_navigation_bar")
                        ) {
                            // لوحة المشرف
                            NavigationBarItem(
                                selected = currentRoute == AppDestinations.SUPERVISOR_DASHBOARD,
                                onClick = {
                                    navController.navigate(AppDestinations.SUPERVISOR_DASHBOARD) {
                                        popUpTo(AppDestinations.SUPERVISOR_DASHBOARD) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (currentRoute == AppDestinations.SUPERVISOR_DASHBOARD) Icons.Default.Dashboard else Icons.Outlined.Dashboard,
                                        contentDescription = "المشرف"
                                    )
                                },
                                label = { Text("المشرف", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = SocietyBluePrimary,
                                    selectedTextColor = SocietyBluePrimary,
                                    indicatorColor = SocietyBlueLight
                                )
                            )

                            // إدارة المعلمين
                            NavigationBarItem(
                                selected = currentRoute == AppDestinations.TEACHERS,
                                onClick = {
                                    navController.navigate(AppDestinations.TEACHERS) {
                                        popUpTo(AppDestinations.SUPERVISOR_DASHBOARD) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (currentRoute == AppDestinations.TEACHERS) Icons.Default.School else Icons.Outlined.School,
                                        contentDescription = "المعلمون"
                                    )
                                },
                                label = { Text("المعلمون", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = SocietyBluePrimary,
                                    selectedTextColor = SocietyBluePrimary,
                                    indicatorColor = SocietyBlueLight
                                )
                            )

                            // تفاصيل الحلقة
                            NavigationBarItem(
                                selected = currentRoute == AppDestinations.CIRCLE_DETAIL,
                                onClick = {
                                    navController.navigate(AppDestinations.CIRCLE_DETAIL) {
                                        popUpTo(AppDestinations.SUPERVISOR_DASHBOARD) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (currentRoute == AppDestinations.CIRCLE_DETAIL) Icons.Default.MenuBook else Icons.Outlined.MenuBook,
                                        contentDescription = "الحلقة"
                                    )
                                },
                                label = { Text("الحلقة", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = SocietyBluePrimary,
                                    selectedTextColor = SocietyBluePrimary,
                                    indicatorColor = SocietyBlueLight
                                )
                            )

                            // تسجيل الحضور
                            NavigationBarItem(
                                selected = currentRoute == AppDestinations.ATTENDANCE,
                                onClick = {
                                    navController.navigate(AppDestinations.ATTENDANCE) {
                                        popUpTo(AppDestinations.SUPERVISOR_DASHBOARD) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (currentRoute == AppDestinations.ATTENDANCE) Icons.Default.Checklist else Icons.Outlined.Checklist,
                                        contentDescription = "الحضور"
                                    )
                                },
                                label = { Text("الحضور", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = SocietyBluePrimary,
                                    selectedTextColor = SocietyBluePrimary,
                                    indicatorColor = SocietyBlueLight
                                )
                            )

                            // قائمة Hamburger (المزيد) - تفتح القائمة الجانبية الكاملة لجميع الروابط
                            NavigationBarItem(
                                selected = drawerState.isOpen || (currentRoute != null && currentRoute !in listOf(
                                    AppDestinations.SUPERVISOR_DASHBOARD,
                                    AppDestinations.TEACHERS,
                                    AppDestinations.CIRCLE_DETAIL,
                                    AppDestinations.ATTENDANCE
                                )),
                                onClick = {
                                    coroutineScope.launch {
                                        if (drawerState.isOpen) drawerState.close() else drawerState.open()
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "قائمة الروابط والتنقل"
                                    )
                                },
                                label = { Text("المزيد ☰", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = SocietyBluePrimary,
                                    selectedTextColor = SocietyBluePrimary,
                                    indicatorColor = SocietyBlueLight
                                ),
                                modifier = Modifier.testTag("nav_bottom_hamburger_menu_btn")
                            )
                        }
                    }
                },
                containerColor = SlateBackground
            ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppDestinations.LOGIN,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // شاشة تسجيل الدخول
                composable(AppDestinations.LOGIN) {
                    LoginScreen(
                        currentTenant = currentTenant,
                        onTenantSelected = { currentTenant = it },
                        onLoginSuccess = { tenant, role ->
                            currentTenant = tenant
                            activeUserRole = role
                            navController.navigate(AppDestinations.SUPERVISOR_DASHBOARD) {
                                popUpTo(AppDestinations.LOGIN) { inclusive = true }
                            }
                        },
                        onNavigateToSupervisorRegistration = {
                            navController.navigate(AppDestinations.SUPERVISOR_REGISTRATION)
                        }
                    )
                }

                // شاشة تسجيل المشرف الجديد (المكون المستقل)
                composable(AppDestinations.SUPERVISOR_REGISTRATION) {
                    SupervisorRegistrationScreen(
                        currentTenant = currentTenant,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onRegistrationSuccess = { tenant, profile ->
                            currentTenant = tenant
                            activeUserRole = UserRole.SUPERVISOR
                            navController.navigate(AppDestinations.SUPERVISOR_DASHBOARD) {
                                popUpTo(AppDestinations.LOGIN) { inclusive = true }
                            }
                        }
                    )
                }

                // لوحة المشرف العام
                composable(AppDestinations.SUPERVISOR_DASHBOARD) {
                    SupervisorDashboardScreen(
                        currentTenant = currentTenant,
                        onNavigateToTeachers = { navController.navigate(AppDestinations.TEACHERS) },
                        onSwitchTenant = {
                            navController.navigate(AppDestinations.LOGIN) {
                                popUpTo(0)
                            }
                        },
                        onLogout = {
                            navController.navigate(AppDestinations.LOGIN) {
                                popUpTo(0)
                            }
                        }
                    )
                }

                // إدارة المعلمين
                composable(AppDestinations.TEACHERS) {
                    TeacherManagementScreen(
                        currentTenant = currentTenant,
                        onBack = { navController.popBackStack() }
                    )
                }

                // تفاصيل الحلقة وإدارة السعة
                composable(AppDestinations.CIRCLE_DETAIL) {
                    CircleDetailScreen(
                        currentTenant = currentTenant,
                        ring = selectedRingForDetail,
                        userRole = activeUserRole,
                        onBack = { navController.popBackStack() },
                        onNavigateToAttendance = { navController.navigate(AppDestinations.ATTENDANCE) }
                    )
                }

                // تسجيل الحضور والـ QR
                composable(AppDestinations.ATTENDANCE) {
                    AttendanceScreen(
                        currentTenant = currentTenant,
                        onBack = { navController.popBackStack() }
                    )
                }

                // بوابة المعلم
                composable(AppDestinations.TEACHER_PORTAL) {
                    TeacherPortalScreen(
                        currentTenant = currentTenant,
                        onOpenAudioRecitation = { st ->
                            selectedStudentForRecitation = st
                            navController.navigate(AppDestinations.AUDIO_RECITATION)
                        },
                        onOpenAttendance = { navController.navigate(AppDestinations.ATTENDANCE) },
                        onBack = { navController.popBackStack() }
                    )
                }

                // تقييم التلاوة الذكي ودعم القراءات العشر
                composable(AppDestinations.AUDIO_RECITATION) {
                    AudioRecitationScreen(
                        currentTenant = currentTenant,
                        student = selectedStudentForRecitation,
                        onBack = { navController.popBackStack() }
                    )
                }

                // بوابة ولي الأمر
                composable(AppDestinations.PARENT_PORTAL) {
                    ParentPortalScreen(
                        currentTenant = currentTenant,
                        onBack = { navController.popBackStack() }
                    )
                }

                // التقارير وتوليد الشهادات
                composable(AppDestinations.REPORTS) {
                    ReportsAndCertificatesScreen(
                        currentTenant = currentTenant,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
}
