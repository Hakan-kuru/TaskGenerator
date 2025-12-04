package com.example.taskgenerator

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taskgenerator.presentation.ui.screens.MainScreen
import com.example.taskgenerator.presentation.view_model.Main_vm
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.taskgenerator.presentation.ui.screens.Create_main_task_screen
import com.example.taskgenerator.presentation.view_model.Create_main_task_vm
import com.example.taskgenerator.presentation.ui.screens.Add_sub_task_screen

// Bu ViewModel isimleri ve state/event alanları senin projende farklıysa
// bu dosyayı kendi ViewModel imzalarına göre uyarlaman yeterli olacak.

@Composable
fun Task_nav_rot(
    modifier: Modifier = Modifier
) {
    // Navigation graph için navController oluşturuyoruz.
    // (Bu, composable'lar arası geçişleri yönetiyor.)
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.MAIN, // Uygulama ilk açıldığında gösterilecek ekran
        modifier = modifier
    ) {
        // 🔹 ANA EKRAN
        composable(route = Routes.MAIN) {
            val viewModel: Main_vm = hiltViewModel()
            val uiState = viewModel.state.collectAsState()

            MainScreen(
                state = uiState.value,
                onRefresh = { viewModel.refresh() },
                onMainTaskClick = { mainTaskId ->
                    // İleride detay ekranı eklersen buradan yönlendirebilirsin
                },
                onToggleMainTaskDone = { taskId ->
                    viewModel.toggleMainTaskDone(taskId)
                },
                onAddSubTaskClick = { mainTaskId ->
                    // MainTask başlığını da göndermek istersen ViewModel'den çekip buraya ekleyebilirsin
                    navController.navigate(
                        Routes.addSubTaskRoute(
                            parentTaskId = mainTaskId,
                            parentTaskTitle = null // İster doldur ister null bırak
                        )
                    )
                },
                onAddMainTaskClick = {
                    navController.navigate(Routes.CREATE_MAIN_TASK)
                }
            )
        }
        composable(route = Routes.CREATE_MAIN_TASK) {
            var viewModel: Create_main_task_vm =hiltViewModel()      
            val uiState by viewModel.state.collectAsState()
            // Burada Hilt ViewModel'i Add_main_task_form_screen içinde alıyorsun
            // (senin ekranın: vm ve UI’yi zaten orada bağladık)
            Create_main_task_screen(
                state = uiState,
                onBackClick = { navController.popBackStack() },
                onTitleChange = { viewModel.onTitleChange(it) },
                onDescriptionChange = { viewModel.onDescriptionChange(it) },
                onTaskTypeChange = { viewModel.onTaskTypeChange(it) },
                onTargetCountChange = { viewModel.onTargetCountChange(it) },
                onDeadlineSelected = { millis -> viewModel.onDeadlineSelected(millis) },
                onSaveClick = { viewModel.onSaveClicked() },
                onSaved = {
                    // isSaved = true olduğunda Create_main_task_screen içinden çağrılıyor
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.ADD_SUB_TASK_ROUTE,
            arguments = listOf(
                navArgument("parentTaskId") { type = NavType.LongType },
                navArgument("parentTaskTitle") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val parentTaskId =
                backStackEntry.arguments?.getLong("parentTaskId") ?: 0L
            val parentTaskTitle =
                backStackEntry.arguments?.getString("parentTaskTitle")

            val viewModel: Add_sub_task_vm = hiltViewModel() // Alt görev ekranının ViewModel'i (BU NESNEYİ İLK KEZ KULLANIYORUZ)
            val uiState by viewModel.state.collectAsState()

            // parentTaskId ve parentTaskTitle'ı ViewModel'e ilk girişte aktarıyoruz
            LaunchedEffect(parentTaskId, parentTaskTitle) {
                viewModel.setParentTask(parentTaskId, parentTaskTitle)
            }

            Add_sub_task_screen(
                state = uiState,
                onBackClick = { navController.popBackStack() },
                onTitleChange = { viewModel.onTitleChange(it) },
                onDescriptionChange = { viewModel.onDescriptionChange(it) },
                onTaskTypeChange = { viewModel.onTaskTypeChange(it) },
                onTargetCountChange = { viewModel.onTargetCountChange(it) },
                onTargetMinutesChange = { viewModel.onTargetMinutesChange(it) },
                onSaveClick = { viewModel.onSaveClicked() },
                onSaved = {
                    // isSaved = true olduğunda Add_Sub_task_screen içinden tetiklenecek
                    navController.popBackStack()
                }
            )
        }

    }
}
