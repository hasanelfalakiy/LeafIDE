package leaf.plugin.nodejs.viewmodel

import androidx.lifecycle.viewModelScope
import io.github.caimucheng.leaf.common.mvi.MVIViewModel
import io.github.caimucheng.leaf.common.mvi.UiIntent
import io.github.caimucheng.leaf.common.mvi.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import leaf.plugin.nodejs.APP
import java.io.File

enum class NewProjectCreateState {
    Default, Loading, Success, Failed
}

data class NewProjectState(
    val createState: NewProjectCreateState = NewProjectCreateState.Default,
    val exception: Exception? = null
) : UiState()

sealed class NewProjectIntent : UiIntent() {
    data class Create(val projectName: String) : NewProjectIntent()
}

class NewProjectViewModel : MVIViewModel<NewProjectState, NewProjectIntent>() {
    override fun initialValue(): NewProjectState {
        return NewProjectState()
    }

    override fun handleIntent(intent: NewProjectIntent, currentState: NewProjectState) {
        when (intent) {
            is NewProjectIntent.Create -> create(intent.projectName)
        }
    }

    private fun create(projectName: String) {
        viewModelScope.launch {
            try {
                setState(state.value.copy(createState = NewProjectCreateState.Loading))
                createSuspend(projectName)
                setState(
                    state.value.copy(
                        createState = NewProjectCreateState.Success,
                        exception = null
                    )
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                setState(
                    state.value.copy(
                        createState = NewProjectCreateState.Failed,
                        exception = e
                    )
                )
            }
        }
    }

    private suspend fun createSuspend(projectName: String) {
        return withContext(Dispatchers.IO) {
            val projectRootPath = File(APP.currentPaths.leafIDEProjectPath, projectName)
            projectRootPath.mkdirs()
        }
    }
}