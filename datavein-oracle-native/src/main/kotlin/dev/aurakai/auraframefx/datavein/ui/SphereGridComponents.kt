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
 * Renders a stylized info card displaying a DataVein node's type, identification, progression, description, and current status.
 *
 * Shows the node type title with a colored status dot, tag/ID/ring/level rows, and — when unlocked — XP and a horizontal XP bar.
 * Also displays the node description, optional data row (when present), and a one-line status message reflecting locked, dormant, or active states.
 *
 * @param node The DataVeinNode whose details are rendered.
 * @param modifier Optional Compose modifier applied to the outer Card.

 */
/**
 * Renders a stylized information card for a DataVeinNode.
 *
 * The card (250.dp wide, rounded corners, 2.dp border tinted by the node type glow color)
 * shows the node type title and a small circular status indicator (green = activated, yellow = unlocked but dormant,
 * red = locked). Below the header it presents identification rows (Tag, ID, Ring, Level).
 *
 * If the node is unlocked an XP row (`XP: {xp}/1000`) and a horizontal progress track are shown; the progress fill
 * reflects `node.xp / 1000f`. The node type description is displayed beneath those rows. If `node.data` is non-empty
 * a "Data" row is shown.
 *
 * A single-line status message is shown at the bottom with one of:
 * - "🔒 Locked - Requires Path Progression" (locked)
 * - "💤 Dormant - Click to Activate" (unlocked but not activated)
 * - "⚡ Active - Processing Data Flow" (activated)
 *
 * @param node The DataVeinNode to display (used for type, identification, XP, description, data, unlocked/activated state).
 */
/**
 * Renders a stylized information card for a DataVein node.
 *
 * The card shows the node type title (colored by the node type's glow color) with a small circular
 * status indicator (green = activated, yellow = unlocked, red = locked). Always displays identification
 * rows for Tag, ID, Ring, and Level. If the node is unlocked, displays an XP row and a horizontal XP
 * progress bar whose fill is proportionate to `node.xp / 1000`. The node type description is always
 * shown; a "Data" row is rendered only when `node.data` is non-empty. A one-line status message at the
 * bottom summarizes node state as one of:
 *  - "🔒 Locked - Requires Path Progression" when not unlocked,
 *  - "💤 Dormant - Click to Activate" when unlocked but not activated,
 *  - "⚡ Active - Processing Data Flow" when activated.
 *
 * @param node The DataVeinNode whose information and progression are rendered.
 */
/**
 * Renders a stylized info card for a DataVeinNode showing its identity, progression and current state.
 *
 * The card is bordered and tinted using `node.type.glowColor` and contains:
 * - A header with the node type title and a small circular status indicator (green = activated, yellow = unlocked, red = locked).
 * - Identification rows (Tag, ID, Ring, Level).
 * - XP and an inline progress bar when the node is unlocked (XP shown as `xp/1000` and fill proportional to `node.xp / 1000f`).
 * - The node type description and an optional Data row when `node.data` is non-empty.
 * - A single-line status message summarizing lock/activation state:
 *   - locked -> "🔒 Locked - Requires Path Progression"
 *   - unlocked but not activated -> "💤 Dormant - Click to Activate"
 *   - activated -> "⚡ Active - Processing Data Flow"
 *
 * @param node The DataVeinNode to display.
 * @param modifier Optional Compose [Modifier] applied to the card container.
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
 * Renders a compact legend Card listing DataVein node types grouped by category.
 *
 * The card shows a header ("🌐 DataVein Node Types"), a horizontal divider, and for each
 * NodeCategory a category label followed by the NodeType entries in that category. Each
 * entry displays a small circular swatch (filled with `type.color` and bordered with a
 * semi-transparent `type.glowColor`) and the type's `displayName`. A short two-line
 * explanatory legend appears at the bottom with interaction hints.
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
 * Compact status card showing real-time DataVein metrics.
 *
 * Renders a pulsing header with a lightning icon, three status rows (Active Flows, Active Nodes, Unlocked),
 * and two horizontal progress indicators (Activation and Progression). Activation and progression percentages
 * are computed as activeNodes/totalNodes and unlockedNodes/totalNodes; when totalNodes is zero those percentages
 * are treated as 0 to avoid division by zero.
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