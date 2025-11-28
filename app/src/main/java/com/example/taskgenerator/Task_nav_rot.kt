package com.example.taskgenerator

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.taskgenerator.presentation.ui.screens.Add_Sub_task_screen
import com.example.taskgenerator.presentation.ui.screens.Add_main_task_form_screen
import com.example.taskgenerator.presentation.ui.screens.MainScreen
import com.example.taskgenerator.presentation.view_model.Main_vm

// Bu ViewModel isimleri ve state/event alanları senin projende farklıysa
// bu dosyayı kendi ViewModel imzalarına göre uyarlaman yeterli olacak.

// ÖRNEK olabilecek basit ViewModel arayüzü varsayıyorum:
// class MainViewModel @Inject constructor(...) : ViewModel() {
//     val state: StateFlow<MainScreenState>
//     fun refresh()
//     fun toggleMainTaskDone(id: Long, done: Boolean)
// }
//
// class AddMainTaskViewModel : ViewModel() {
//     fun saveMainTask(form: AddMainTaskForm)
// }
//
// class AddSubTaskViewModel : ViewModel() {
//     fun saveSubTask(form: AddSubTaskForm)
// }

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
            val uiState = viewModel.state.value

            MainScreen(
                state = uiState,
                onRefresh = { viewModel.refresh() },
                onMainTaskClick = { mainTaskId ->
                    // İleride detay ekranı eklersen buradan yönlendirebilirsin
                },
                onToggleMainTaskDone = { taskId, newValue ->
                    viewModel.toggleMainTaskDone(taskId, newValue)
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
                    navController.navigate(Routes.ADD_MAIN_TASK)
                }
            )
        }

        // 🔹 MAIN TASK EKLEME EKRANI
        composable(route = Routes.ADD_MAIN_TASK) {
            val viewModel: com.example.taskgenerator.presentation.addtask.AddMainTaskViewModel = hiltViewModel()

            Add_main_task_form_screen(
                onBackClick = { navController.popBackStack() },
                onSaveClick = { form ->
                    viewModel.saveMainTask(form)
                    navController.popBackStack() // Kayıttan sonra geri dön
                }
            )
        }

        // 🔹 SUB TASK EKLEME EKRANI
        composable(
            route = Routes.ADD_SUB_TASK_ROUTE,
            arguments = listOf(
                navArgument(Routes.ARG_PARENT_TASK_ID) {
                    type = NavType.LongType
                },
                navArgument(Routes.ARG_PARENT_TASK_TITLE) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val parentId =
                backStackEntry.arguments?.getLong(Routes.ARG_PARENT_TASK_ID) ?: 0L
            val parentTitle =
                backStackEntry.arguments?.getString(Routes.ARG_PARENT_TASK_TITLE)

            val viewModel: com.example.taskgenerator.presentation.addtask.AddSubTaskViewModel = hiltViewModel()

            Add_Sub_task_screen(
                parentTaskId = parentId,
                parentTaskTitle = parentTitle,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { form ->
                    viewModel.saveSubTask(form)
                    navController.popBackStack()
                }
            )
        }
    }
}
