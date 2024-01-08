package io.github.caimucheng.leaf.ide.viewmodel

import io.github.caimucheng.leaf.common.mvi.MVIAppViewModel
import io.github.caimucheng.leaf.common.mvi.UiIntent
import io.github.caimucheng.leaf.common.mvi.UiState
import kotlinx.coroutines.launch

enum class EditorAction {
    SHOW, HIDE, UPDATE, UNDO, REDO
}

data class EditorState(
    val editorAction: EditorAction? = EditorAction.SHOW,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false
) : UiState()

sealed class EditorIntent : UiIntent() {
    data object ShowEditor : EditorIntent()

    data object HideEditor : EditorIntent()

    data object Undo : EditorIntent()

    data object Redo : EditorIntent()

    data class RefreshCanUndoAndRedo(
        val canUndo: Boolean,
        val canRedo: Boolean
    ) : EditorIntent()
}

object EditorViewModel : MVIAppViewModel<EditorState, EditorIntent>() {
    override fun initialValue(): EditorState {
        return EditorState()
    }

    override fun handleIntent(intent: EditorIntent, currentState: EditorState) {
        when (intent) {
            EditorIntent.ShowEditor -> {
                viewModelScope.launch {
                    setState(
                        state.value.copy(
                            editorAction = EditorAction.SHOW
                        )
                    )
                }
            }

            EditorIntent.HideEditor -> {
                viewModelScope.launch {
                    setState(
                        state.value.copy(
                            editorAction = EditorAction.HIDE
                        )
                    )
                }
            }

            EditorIntent.Undo -> {
                viewModelScope.launch {
                    setState(
                        state.value.copy(
                            editorAction = EditorAction.UNDO
                        )
                    )
                }
            }

            EditorIntent.Redo -> {
                viewModelScope.launch {
                    setState(
                        state.value.copy(
                            editorAction = EditorAction.REDO
                        )
                    )
                }
            }

            is EditorIntent.RefreshCanUndoAndRedo -> {
                viewModelScope.launch {
                    setState(
                        state.value.copy(
                            editorAction = EditorAction.UPDATE,
                            canUndo = intent.canUndo,
                            canRedo = intent.canRedo
                        )
                    )
                }
            }
        }
    }
}