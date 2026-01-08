package com.example.memories.feature.feature_media_edit.presentatiion.media_edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.memories.R
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

//data class MediaEditState(
//    val activeTool: EditTool = EditTool.FILTER,
//)

enum class EditTool(val label : String,val icon : Int){
    CROP("Crop",R.drawable.ic_crop),
    ADJUST("Adjust",R.drawable.ic_adjust),
    FILTER("Filters",R.drawable.ic_lux),
    ROTATE("Rotate",R.drawable.ic_rotate),
    MORE("More",R.drawable.ic_more_horizontal)
}

class EditorState internal constructor(
    initialBrightness: Float = 0f,
    initialBlur: Float = 0f,
    initialContrast: Float = 0f,
    initialStructure: Float = 0f,
    initialWarmth: Float = 0f,
    initialSaturation: Float = 0f,
    initialColor: Float = 0f,
    initialFade: Float = 0f,
    initialHighlights: Float = 0f,
    initialShadows: Float = 0f,
    initialVignette: Float = 0f,
    initialSharpen: Float = 0f,
    initialActiveTool: EditTool = EditTool.ADJUST,
    initialPreviousTool: EditTool = EditTool.ADJUST
) {
    var brightness by  mutableFloatStateOf(initialBrightness)
    var blur by mutableFloatStateOf(initialBlur)
    var contrast by mutableFloatStateOf(initialContrast)
    var structure by mutableFloatStateOf(initialStructure)
    var warmth by mutableFloatStateOf(initialWarmth)
    var saturation by mutableFloatStateOf(initialSaturation)
    var color by mutableFloatStateOf(initialColor)
    var fade by mutableFloatStateOf(initialFade)
    var highlights by mutableFloatStateOf(initialHighlights)
    var shadows by mutableFloatStateOf(initialShadows)
    var vignette by mutableFloatStateOf(initialVignette)
    var sharpen by mutableFloatStateOf(initialSharpen)
    private var _previousTool by mutableStateOf(initialPreviousTool)
    private var _activeTool by mutableStateOf(initialActiveTool)
    var activeTool: EditTool
        get() = _activeTool
        set(value) {
            if (_activeTool != value) {
                _previousTool = _activeTool
                _activeTool = value
            }
        }
    val previousTool: EditTool
        get() = _previousTool

    fun revertToPreviousTool() {
        activeTool = _previousTool
    }

    fun resetAdjustments() {
        brightness = 0f
        blur = 0f
        contrast = 0f
        structure = 0f
        warmth = 0f
        saturation = 0f
        color = 0f
        fade = 0f
        highlights = 0f
        shadows = 0f
        vignette = 0f
        sharpen = 0f
    }

    companion object {
        val Saver: Saver<EditorState, *> = Saver(
            save = { state ->
                listOf(
                    state.brightness,
                    state.blur,
                    state.contrast,
                    state.structure,
                    state.warmth,
                    state.saturation,
                    state.color,
                    state.fade,
                    state.highlights,
                    state.shadows,
                    state.vignette,
                    state.sharpen,
                    state.activeTool.ordinal,
                    state.previousTool.ordinal
                )
            },
            restore = { list ->
                EditorState(
                    initialBrightness = list[0] as Float,
                    initialBlur = list[1] as Float,
                    initialContrast = list[2] as Float,
                    initialStructure = list[3] as Float,
                    initialWarmth = list[4] as Float,
                    initialSaturation = list[5] as Float,
                    initialColor = list[6] as Float,
                    initialFade = list[7] as Float,
                    initialHighlights = list[8] as Float,
                    initialShadows = list[9] as Float,
                    initialVignette = list[10] as Float,
                    initialSharpen = list[11] as Float,
                    initialActiveTool = EditTool.entries[list[12] as Int],
                    initialPreviousTool = EditTool.entries[list[13] as Int]
                )
            }
        )
    }
}

@Composable
fun rememberEditorState(
    key: String? = null,
    initialBrightness: Float = 0f,
    initialBlur: Float = 0f,
    initialContrast: Float = 0f,
    initialStructure: Float = 0f,
    initialWarmth: Float = 0f,
    initialSaturation: Float = 0f,
    initialColor: Float = 0f,
    initialFade: Float = 0f,
    initialHighlights: Float = 0f,
    initialShadows: Float = 0f,
    initialVignette: Float = 0f,
    initialSharpen: Float = 0f,
    initialActiveTool: EditTool = EditTool.ADJUST
): EditorState {
    return rememberSaveable(
        saver = EditorState.Saver
    ) {
        EditorState(
            initialBrightness = initialBrightness,
            initialBlur = initialBlur,
            initialContrast = initialContrast,
            initialStructure = initialStructure,
            initialWarmth = initialWarmth,
            initialSaturation = initialSaturation,
            initialColor = initialColor,
            initialFade = initialFade,
            initialHighlights = initialHighlights,
            initialShadows = initialShadows,
            initialVignette = initialVignette,
            initialSharpen = initialSharpen,
            initialActiveTool = initialActiveTool
        )
    }
}