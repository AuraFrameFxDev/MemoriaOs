package dev.aurakai.auraframefx.datavein.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.datavein.model.DataVeinNode
import dev.aurakai.auraframefx.datavein.model.NodeCategory
import dev.aurakai.auraframefx.datavein.model.NodeType

/**
 * Enhanced Node Info Panel with FFX-style progression details
 */
/**
 * Displays a stylized card with detailed information for a DataVeinNode.
 *
 * Shows the node type header with a colored status indicator (Green = activated, Yellow = unlocked, Red = locked),
 * identification rows (Tag, ID, Ring, Level), the node description, and an optional Data row.
 * If the node is unlocked, displays XP as "xp/1000" and a horizontal progress bar filled proportionally to xp/1000.
 * A short status line is shown at the bottom with one of:
 *  - "🔒 Locked - Requires Path Progression" (not unlocked)
 *  - "💤 Dormant - Click to Activate" (unlocked but not activated)
 *  - "⚡ Active - Processing Data Flow" (activated)
 */
/**
 * Displays a stylized information card for a DataVeinNode.
 *
 * Shows the node type title with a colored status dot (Green = activated, Yellow = unlocked, Red = locked),
 * identification rows (Tag, ID, Ring, Level), optional XP and progress bar when unlocked, the node description,
 * optional data field, and a short status line describing lock/activation state.
 *
 * @param node The DataVeinNode to render (provides type, activation/unlock state, XP, description, data, etc.).
 * @param modifier Optional Modifier applied to the outer Card, allowing callers to adjust layout or styling.
 */
/**
 * Renders a compact information card for a DataVeinNode.
 *
 * Displays the node type header with a colored glow and a small status dot (green = activated,
 * yellow = unlocked, red = locked), identification rows (Tag, ID, Ring, Level), the node
 * description, optional Data row, and a bottom status line describing the node state.
 *
 * If the node is unlocked, an XP row and a horizontal progress bar are shown (filled proportionally
 * to `node.xp / 1000f` and labeled as `xp/1000`). The bottom status line shows one of:
 * - "🔒 Locked - Requires Path Progression" (not unlocked)
 * - "💤 Dormant - Click to Activate" (unlocked but not activated)
 * - "⚡ Active - Processing Data Flow" (activated)
 *
 * @param node The DataVeinNode to display.
 */
/**
 * Renders a stylized information panel for a DataVein node.
 *
 * Displays the node type header with a colorized status indicator, identifying rows
 * (Tag, ID, Ring, Level), optional XP and XP progress bar when unlocked, the node
 * description, optional data field, and a compact bottom status line reflecting
 * locked/dormant/active state.
 *
 * The card border and header text use the node type's glow color. XP is shown as
 * "xp/1000" and the progress bar is filled proportionally to `node.xp / 1000f`.
 *
 * @param node The DataVeinNode to display.
 * @param modifier Optional Compose modifier applied to the top-level Card.
 */
@Composable
fun NodeInfoPanel(
    node: DataVeinNode,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(250.dp)
            .border(
                width = 2.dp,
                color = node.type.glowColor,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header with node type and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = node.type.displayName,
                    color = node.type.glowColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                // Status indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            if (node.activated) Color.Green
                            else if (node.isUnlocked) Color.Yellow
                            else Color.Red
                        )
                )
            }

            HorizontalDivider(color = node.type.glowColor.copy(alpha = 0.3f))

            // Node identification
            InfoRow("Tag", node.tag, Color.Cyan)
            InfoRow("ID", node.id, Color.White)
            InfoRow("Ring", node.ring.toString(), Color.White)
            InfoRow("Level", node.level.toString(), Color.White)

            // FFX-style progression info
            if (node.isUnlocked) {
                InfoRow("XP", "${node.xp}/1000", Color.Cyan)

                // XP Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(
                            Color.Gray.copy(alpha = 0.3f),
                            RoundedCornerShape(3.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(node.xp / 1000f)
                            .background(
                                Color.Cyan,
                                RoundedCornerShape(3.dp)
                            )
                    )
                }
            }

            // Node description and data
            Text(
                text = node.type.description,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp,
                lineHeight = 14.sp
            )

            if (node.data.isNotEmpty()) {
                InfoRow("Data", node.data, Color.Gray)
            }

            // Status details
            val statusText = when {
                !node.isUnlocked -> "🔒 Locked - Requires Path Progression"
                !node.activated -> "💤 Dormant - Click to Activate"
                else -> "⚡ Active - Processing Data Flow"
            }

            Text(
                text = statusText,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp,
                lineHeight = 12.sp
            )
        }
    }
}

/**
 * Displays a compact, categorized legend of DataVein node types.
 *
 * Groups NodeType values by their NodeCategory and lists each type with a
 * small color dot (filled with the type color and bordered by its glow color)
 * alongside the type's display name. A short interaction note is shown at the
 * bottom.
 */
@Composable
fun NodeTypeLegend(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "🌐 DataVein Node Types",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

            // Group by category
            NodeCategory.values().forEach { category ->
                val nodesInCategory = NodeType.values().filter { it.category == category }
                if (nodesInCategory.isNotEmpty()) {
                    Text(
                        text = category.name,
                        color = Color.Gray,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )

                    nodesInCategory.forEach { type ->
                        Row(
                            modifier = Modifier.padding(start = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(type.color)
                                    .border(
                                        1.dp,
                                        type.glowColor.copy(alpha = 0.5f),
                                        CircleShape
                                    )
                            )
                            Text(
                                text = type.displayName,
                                color = Color.White,
                                fontSize = 8.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }

            // Legend explanation
            Text(
                text = "💡 Click nodes to explore paths\n🔒 Unlock nodes via progression",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 8.sp,
                lineHeight = 10.sp
            )
        }
    }
}

/**
 * Displays a compact status card showing real-time-like metrics and progress for the DataVein.
 *
 * Shows an animated glyph with a title, three metric rows (Active Flows, Active Nodes, Unlocked),
 * and two progress bars for "Activation" and "Progression" computed as ratios against totalNodes.
 *
 * @param activeFlows Current number of active data flows.
 * @param activeNodes Number of nodes currently active.
 * @param totalNodes Total number of nodes; used as the denominator for percentage calculations (safe-divide: 0 if zero).
 * @param unlockedNodes Number of nodes that have been unlocked.
 * @param modifier Modifier to apply to the root Card composable.
 */
@Composable
fun StatusPanel(
    activeFlows: Int,
    activeNodes: Int,
    totalNodes: Int,
    unlockedNodes: Int,
    modifier: Modifier = Modifier
) {
    val pulseAnimation by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.8f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "⚡",
                    fontSize = (16 * pulseAnimation).sp,
                    color = Color.Cyan
                )
                Text(
                    text = "Genesis DataVein",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            HorizontalDivider(color = Color.Cyan.copy(alpha = 0.3f))

            StatusRow("Active Flows", activeFlows.toString(), Color(0xFF00FF88))
            StatusRow("Active Nodes", "$activeNodes/$totalNodes", Color(0xFF4FC3F7))
            StatusRow("Unlocked", "$unlockedNodes/$totalNodes", Color.Cyan)

            // Progress indicators
            val activePercentage = if (totalNodes > 0) activeNodes.toFloat() / totalNodes else 0f
            val unlockedPercentage =
                if (totalNodes > 0) unlockedNodes.toFloat() / totalNodes else 0f

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ProgressIndicator("Activation", activePercentage, Color(0xFF4FC3F7))
                ProgressIndicator("Progression", unlockedPercentage, Color.Cyan)
            }
        }
    }
}

/**
 * FFX-style Progression Indicator
 */
@Composable
fun ProgressionIndicator(
    selectedNode: DataVeinNode?,
    modifier: Modifier = Modifier
) {
    selectedNode?.let { node ->
        if (node.isUnlocked) {
            Card(
                modifier = modifier,
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "🎯 Node Progression",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )

                    Text(
                        text = "Next: ${1000 - node.xp} XP",
                        color = Color.Cyan,
                        fontSize = 9.sp
                    )

                    // Available abilities/upgrades
                    Text(
                        text = "Available Paths: ${node.connectedPaths.size}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 8.sp
                    )
                }
            }
        }
    }
}

// Helper composables
@Composable
private fun InfoRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatusRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 10.sp
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProgressIndicator(label: String, progress: Float, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 8.sp
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                color = color,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    Color.Gray.copy(alpha = 0.3f),
                    RoundedCornerShape(2.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(
                        color,
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

/**
 * Animation utilities for enhanced visual effects
 */
@Composable
fun rememberPulseAnimation(): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    return infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    ).value
}

@Composable
fun rememberRotationAnimation(): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    return infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    ).value
}