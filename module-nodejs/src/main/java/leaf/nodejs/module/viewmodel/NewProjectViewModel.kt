package leaf.nodejs.module.viewmodel

import androidx.lifecycle.viewModelScope
import io.github.caimucheng.leaf.common.mvi.MVIViewModel
import io.github.caimucheng.leaf.common.mvi.UiIntent
import io.github.caimucheng.leaf.common.mvi.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import leaf.nodejs.module.NodeJSModuleAPP
import org.json.JSONObject
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

enum class NewProjectCreateState {
    Default, Loading, Success, Failed
}

data class NewProjectState(
    val createState: NewProjectCreateState = NewProjectCreateState.Default,
    val exception: Exception? = null
) : UiState()

sealed class NewProjectIntent : UiIntent() {
    data class Create(val projectName: String, val description: String) : NewProjectIntent()
}

class NewProjectViewModel : MVIViewModel<NewProjectState, NewProjectIntent>() {
    override fun initialValue(): NewProjectState {
        return NewProjectState()
    }

    override fun handleIntent(intent: NewProjectIntent, currentState: NewProjectState) {
        when (intent) {
            is NewProjectIntent.Create -> create(intent.projectName, intent.description)
        }
    }

    private fun create(projectName: String, description: String) {
        viewModelScope.launch {
            try {
                setState(state.value.copy(createState = NewProjectCreateState.Loading))
                createSuspend(projectName, description)
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

    private suspend fun createSuspend(projectName: String, description: String) {
        return withContext(Dispatchers.IO) {
            val projectRootPath = File(NodeJSModuleAPP.currentPaths.leafIDEProjectPath, projectName)
            projectRootPath.mkdirs()

            val configurationDir = File(projectRootPath, ".LeafIDE")
            configurationDir.mkdirs()

            val workspaceFile = File(configurationDir, "workspace.json")
            workspaceFile.createNewFile()

            workspaceFile.writer().use {
                val jsonObject = JSONObject()
                jsonObject.put("name", projectName)
                jsonObject.put("description", description)
                jsonObject.put("moduleSupport", NodeJSModuleAPP.MODULE_SUPPORT)
                it.write(jsonObject.toString(4))
                it.flush()
            }
        }
    }
}