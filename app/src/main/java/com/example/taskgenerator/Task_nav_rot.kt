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
import androidx.compose.runtime.collectAsState

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
                    navController.navigate(Routes.ADD_MAIN_TASK)
                }
            )
        }

    }
}
