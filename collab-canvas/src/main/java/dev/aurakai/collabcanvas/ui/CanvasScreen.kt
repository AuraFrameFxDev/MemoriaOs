package dev.aurakai.collabcanvas.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import dev.aurakai.collabcanvas.model.CanvasElement
import dev.aurakai.collabcanvas.model.ElementType
import dev.aurakai.collabcanvas.ui.animation.*
import kotlinx.coroutines.launch

/**
 * Displays a collaborative drawing canvas with multi-tool support, gesture handling, and animated path rendering.
 */
/**
 * An interactive composable screen that provides a collaborative drawing canvas with multi-tool support.
 *
 * Supports freehand path drawing, basic rectangle and oval elements, pinch-to-zoom, panning, and progressive
 * animated rendering of previously drawn paths. The UI includes a top app bar (clear and save actions),
 * floating tool buttons to select Path/Rectangle/Oval, and a toolbar for color, stroke width selection, and clearing.
 *
 * State managed by this composable:
 * - A list of completed drawable paths (with animated copies for progressive rendering).
 * - A list of canvas elements (path/rectangle/oval).
 * - Current in-progress Path, current color, stroke width, selected tool/element, and an isDrawing flag.
 * - Animatables for zoom (scale) and pan (offset), and a transformable gesture state for handling pinch/drag gestures.
 *
 * Side effects and notable behavior:
 * - Mutates internal state lists (paths, elements, animatedPaths) as the user draws or clears the canvas.
 * - Pinch-to-zoom and pan update the internal scale and offset animatables.
 * - The top-bar "Clear Canvas" and toolbar "Clear" actions remove stored paths and animated paths.
 * - The "Save" action is a placeholder and does not persist the canvas in this implementation.
 */

/**
 * Renders the collaborative drawing canvas screen with tools, gestures, and animated rendering.
 *
 * Provides a full-screen drawing surface that supports:
 * - Freehand drawing, rectangle and oval tools selectable via floating action buttons.
 * - Pinch-to-zoom and pan (transformable) interactions.
 * - Tap and drag gesture handling for creating and committing drawable paths.
 * - Animated progressive rendering of previously committed paths.
 * - A top app bar with actions to clear the canvas and save (placeholder).
 * - An on-screen toolbar for selecting color, stroke width, and clearing animated paths.
 *
 * The composable owns and remembers all canvas state (paths, elements, current in-progress path,
 * selected tool/color/stroke, animation state, scale and offset). Committing a completed path
 * adds a PluckablePath to the internal lists and creates an animated copy for playback.
 */
/**
 * Full-screen composable providing an interactive collaborative drawing canvas.
 *
 * Supports freehand drawing (PATH) plus rectangle and oval tools, pinch-to-zoom and pan, tap/drag gestures
 * to create strokes, and animated playback of committed paths. The UI includes a top app bar with clear
 * and save actions (save is a placeholder), floating tool buttons to switch between PATH/RECTANGLE/OVAL,
 * and an on-screen toolbar for selecting color, stroke width, and clearing animated paths.
 *
 * Internal state owned by this composable includes the list of committed PluckablePath objects (`paths`),
 * a list of drawable CanvasElement objects (`elements`), the in-progress `currentPath`, current color and
 * stroke width, the selected tool and element, animation state for progressive rendering (`animatedPaths`),
 * and transform state for zoom (`scale`) and pan (`offset`). Committed paths are added to `paths` when a
 * drawing gesture ends and the current path has non-empty bounds. Clearing actions remove paths, elements,
 * and their animated copies.
 */
/**
 * Renders the interactive collaborative drawing surface for creating and animating simple canvas elements.
 *
 * This full-screen composable provides:
 * - Multiple drawing tools: freehand PATH, RECTANGLE, and OVAL.
 * - Pinch-to-zoom and pan support via a transformable state.
 * - Tap and drag input for creating in-progress strokes (freehand PATH); completed strokes are committed when the gesture ends and the path has non-empty bounds.
 * - A top app bar with Clear and Save actions and floating tool buttons to switch tools.
 * - A toolbar for selecting color and stroke width and clearing committed/animated paths.
 * - Animated playback support: committed paths are stored in `paths` and mirrored into `animatedPaths` for progressive rendering.
 *
 * Visual content (grid, elements, current in-progress path, and animated paths) is drawn under the current scale and offset transforms so zoom and pan affect all canvas content consistently.

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
/**
 * Full-screen composable providing an interactive collaborative drawing canvas.
 *
 * Renders a pan- and zoom-able canvas with a background grid, selectable drawing tools
 * (freehand PATH, RECTANGLE, OVAL), a live in-progress stroke, and playback-ready committed paths.
 * Users can draw freehand with drag/tap gestures (PATH), pan/zoom via transform gestures, select
 * tool, color, and stroke width via the toolbar, and clear or save the canvas from the top app bar.
 *
 * Visual content (grid, elements, current stroke, and animated/committed paths) is uniformly
 * transformed by the current scale and offset so zooming and panning affect all layers together.
 * Committed paths are stored in `paths` and mirrored into `animatedPaths` for animated playback.
 *
 * Side effects:
 * - Maintains and mutates internal remembered state: committed `paths`, `elements`, `animatedPaths`,
 *   current in-progress `currentPath`, `currentColor`, `strokeWidth`, selected tool, and transform
 *   state (`scale`, `offset`).
 * - Clearing actions remove entries from `paths`, `elements`, and `animatedPaths`.
 */
@Composable
fun CanvasScreen() {
    // Canvas state
    val paths = remember { mutableStateListOf<PluckablePath>() }
    val elements = remember { mutableStateListOf<CanvasElement>() }
    var currentPath by remember { mutableStateOf(Path()) }
    var currentColor by remember { mutableStateOf(Color.Black) }
    var strokeWidth by remember { mutableStateOf(5f) }
    var selectedTool by remember { mutableStateOf<ElementType>(ElementType.PATH) }
    var selectedElement by remember { mutableStateOf<CanvasElement?>(null) }
    var isDrawing by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    rememberScrollState()

    // Animation states
    val animatedPaths = remember { mutableStateMapOf<Int, PluckablePath>() }
    val scale = remember { Animatable(1f) }
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    // Update animated paths when paths change
    LaunchedEffect(paths) {
        paths.forEachIndexed { index, path ->
            if (!animatedPaths.containsKey(index)) {
                animatedPaths[index] = path.copy()
            }
        }
    }

    // Canvas gesture handlers
    val panZoomState = rememberTransformableState { zoomChange, panChange, rotationChange ->
        coroutineScope.launch {
            scale.snapTo(scale.value * zoomChange)
            val newOffset = offset.value + panChange * (1f / scale.value)
            offset.snapTo(newOffset)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Co-lab Canvas") },
                actions = {
                    IconButton(onClick = { 
                        paths.clear()
                        elements.clear()
                        animatedPaths.clear()
                    }) {
                        Icon(Icons.Default.Delete, "Clear Canvas")
                    }
                    IconButton(onClick = { /* Save canvas */ }) {
                        Icon(Icons.Default.Check, "Save")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tool selection buttons
                FloatingActionButton(
                    onClick = { selectedTool = ElementType.PATH },
                    modifier = Modifier.size(48.dp),
                    containerColor = if (selectedTool == ElementType.PATH) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Icon(Icons.Default.Edit, "Draw")
                }
                FloatingActionButton(
                    onClick = { selectedTool = ElementType.RECTANGLE },
                    modifier = Modifier.size(48.dp),
                    containerColor = if (selectedTool == ElementType.RECTANGLE) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Icon(Icons.Default.CropSquare, "Rectangle")
                }
                FloatingActionButton(
                    onClick = { selectedTool = ElementType.OVAL },
                    modifier = Modifier.size(48.dp),
                    containerColor = if (selectedTool == ElementType.OVAL) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Icon(Icons.Default.Circle, "Circle")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { tapOffset ->
                            isDrawing = true
                            currentPath.moveTo(tapOffset.x, tapOffset.y)
                            tryAwaitRelease()
                            isDrawing = false
                        }
                    )
                }
        ) {
            // Main drawing canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .transformable(panZoomState)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { dragOffset ->
                                if (selectedTool == ElementType.PATH) {
                                    isDrawing = true
                                    currentPath.moveTo(dragOffset.x, dragOffset.y)
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                when (selectedTool) {
                                    ElementType.PATH -> {
                                        val x = change.position.x
                                        val y = change.position.y
                                        currentPath.lineTo(x, y)
                                    }
                                    else -> {
                                        // Handle other element types
                                    }
                                }
                            },
                            onDragEnd = {
                                isDrawing = false
                                // Add the completed path to the list
                                if (currentPath.getBounds().isEmpty.not()) {
                                    paths.add(PluckablePath(currentPath, currentColor, strokeWidth))
                                    currentPath = Path()
                                }
                            }
                        )
                    }
            ) {
                // Apply transformations
                scale(scale.value, scale.value) {
                    translate(offset.value.x, offset.value.y) {
                        drawGrid()
                    }
                }

                // Draw all elements
                scale(scale.value, scale.value) {
                    translate(offset.value.x, offset.value.y) {
                        elements.forEach { element ->
                            when (element.type) {
                                ElementType.PATH -> {
                                    drawPath(
                                        path = element.path.toPath(),
                                        color = element.color,
                                        style = Stroke(
                                            width = element.strokeWidth,
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round
                                        )
                                    )
                                }
                                ElementType.RECTANGLE -> {
                                    val bounds = element.path.toPath().getBounds()
                                    drawRect(
                                        color = element.color,
                                        topLeft = Offset(bounds.left, bounds.top),
                                        size = androidx.compose.ui.geometry.Size(bounds.width, bounds.height),
                                        style = Stroke(width = element.strokeWidth)
                                    )
                                }
                                ElementType.OVAL -> {
                                    val bounds = element.path.toPath().getBounds()
                                    drawOval(
                                        color = element.color,
                                        topLeft = Offset(bounds.left, bounds.top),
                                        size = androidx.compose.ui.geometry.Size(bounds.width, bounds.height),
                                        style = Stroke(width = element.strokeWidth)
                                    )
                                }
                                else -> {}
                            }
                        }
                    }
                }

                // Draw current path being drawn
                if (isDrawing) {
                    scale(scale.value, scale.value) {
                        translate(offset.value.x, offset.value.y) {
                            drawPath(
                                path = currentPath,
                                color = currentColor,
                                style = Stroke(
                                    width = strokeWidth / scale.value,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }
                }

                // Draw all animated paths
                paths.forEachIndexed { index, path ->
                    val animatedPath = animatedPaths[index] ?: return@forEachIndexed
                    
                    scale(animatedPath.scale, animatedPath.scale) {
                        translate(animatedPath.offset.x, animatedPath.offset.y) {
                            drawPath(
                                path = path.path,
                                color = path.color.copy(alpha = path.alpha),
                                style = Stroke(width = path.strokeWidth)
                            )
                        }
                    }
                }
            }

            // Toolbar
            CanvasToolbar(
                onColorSelected = { color ->
                    currentColor = color
                },
                onStrokeWidthSelected = { width ->
                    strokeWidth = width
                },
                onClear = {
                    paths.clear()
                    animatedPaths.clear()
                }
            )
        }
    }
}

// Extension to draw grid
private fun DrawScope.drawGrid() {
    val gridSpacing = 50f
    val strokeWidth = 1f
    val color = Color.Gray.copy(alpha = 0.3f)
    
    // Draw vertical lines
    for (x in 0..size.width.toInt() step gridSpacing.toInt()) {
        drawLine(
            color = color,
            start = Offset(x.toFloat(), 0f),
            end = Offset(x.toFloat(), size.height),
            strokeWidth = strokeWidth
        )
    }
    
    // Draw horizontal lines
    for (y in 0..size.height.toInt() step gridSpacing.toInt()) {
        drawLine(
            color = color,
            start = Offset(0f, y.toFloat()),
            end = Offset(size.width, y.toFloat()),
            strokeWidth = strokeWidth
        )
    }
}
