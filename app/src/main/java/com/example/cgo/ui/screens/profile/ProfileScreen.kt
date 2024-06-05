package com.example.cgo.ui.screens.profile

import android.graphics.Paint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.example.cgo.data.database.entities.Event
import com.example.cgo.data.database.entities.User
import com.example.cgo.ui.CGORoute
import com.example.cgo.ui.composables.ImageWithPlaceholder
import com.example.cgo.ui.composables.NoItemPlaceholder
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.example.cgo.data.database.entities.PrivacyType
import kotlin.math.cos
import kotlin.math.sin

data class PieChartData(
    val color: Color,
    val name: String,
    val value: Int
)

@Composable
fun ProfileScreen(
    user: User,
    events: List<Event>,
    navController: NavHostController
) {
    val pieChartData = listOf(
        PieChartData(
            color = Color.Green,
            name ="Wins",
            value = events.filter { it.winnerId == user.userId }.size
        ),
        PieChartData(
            color = Color.Red,
            name = "Losses",
            value = events.filter { it.winnerId != null && it.winnerId != user.userId }.size
        ),
        PieChartData(
            color = Color.Gray,
            name ="Draws",
            value = events.filter { it.winnerId == null }.size
        )
    )
    Column (
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.size(10.dp))
        ImageWithPlaceholder(uri = user.profilePicture?.toUri(), size = com.example.cgo.ui.composables.Size.Large)
        Text(text = user.username)
        Spacer(Modifier.size(10.dp))
        if (pieChartData.sumOf { it.value } > 0) {
            PieChart(data = pieChartData)
            Spacer(Modifier.size(15.dp))
        }
        HorizontalDivider()
        MatchHistory(events = events.take(20).filter { it.privacyType == PrivacyType.PUBLIC }, user = user, navController = navController)
    }
}

@Composable
fun MatchHistory(
    events: List<Event>,
    user: User,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(bottom = 10.dp)
    ) {
        Text(
            text = "Match History",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.size(10.dp))
        if (events.isNotEmpty()) {
            Column (
                modifier = Modifier.border(width = 2.dp, color = Color.Gray)
            ) {
                events.forEach { event ->
                    EventItem(
                        event = event,
                        user = user,
                        onClick = { navController.navigate(CGORoute.EventDetails.buildRoute(event.eventId)) }
                    )
                }
            }
        } else {
            NoItemPlaceholder("No events found")
        }
    }
}

@Composable
fun EventItem(event: Event, user: User, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(text = event.title) },
        supportingContent = {
            Text(text = event.date)
        },
        trailingContent = {
            if (event.winnerId == user.userId)
                Text(text = "Win")
            else if (event.winnerId != null)
                Text(text = "Loss")
        },
    )
    HorizontalDivider()
}

@Composable
fun PieChart(data: List<PieChartData>) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        val text = "Games Played: ${data.sumOf { it.value }}"
        val textMeasurer = rememberTextMeasurer()
        val style = TextStyle(
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary
        )
        var textLayoutResult = remember("") {
            textMeasurer.measure(text, style)
        }
        val textColor = MaterialTheme.colorScheme.primary.toArgb()
        Canvas(
            modifier = Modifier
                .fillMaxWidth(.4f)
                .aspectRatio(1f)
        ) {
            drawText(
                textMeasurer = textMeasurer,
                text = text,
                style = style,
                topLeft = Offset(
                    x = center.x - textLayoutResult.size.width / 2,
                    y = center.y - textLayoutResult.size.height / 2,
                )
            )
            val total = data.sumOf { it.value }.toFloat()
            var startAngle = -90f
            val width = size.width
            val radius = width / 2f
            val strokeWidth = 30.dp.toPx()
            data.filter { it.value > 0 }.forEach { value ->
                val sweepAngle = 360f * value.value.toFloat() / total
                drawArc(
                    color = value.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                )
                textLayoutResult = textMeasurer.measure(value.name + ": ${value.value}", style)
                var rotationAngle = startAngle + sweepAngle / 2f + 90f
                var offset = 40f
                val angleInRadians = (startAngle + sweepAngle / 2).degreeToAngle
                if (-textLayoutResult.size.height / 2 + center.y + (radius + strokeWidth) * sin(angleInRadians) > center.y) {
                    rotationAngle += 180
                    offset = -offset
                }
                val labelX = -textLayoutResult.size.width / 2 + center.x + (radius + strokeWidth - offset) * cos(angleInRadians)
                val labelY = -textLayoutResult.size.height / 2 + center.y + (radius + strokeWidth - offset) * sin(angleInRadians)
                rotate(
                    degrees = rotationAngle,
                    pivot = Offset(labelX + textLayoutResult.size.width / 2, labelY + textLayoutResult.size.height / 2),
                    block = {
                        drawIntoCanvas { canvas ->
                            canvas.nativeCanvas.drawText(
                                value.name + ": ${value.value}",
                                labelX,
                                labelY,
                                Paint().apply {
                                    color = textColor
                                    textSize = 16.sp.toPx()
                                }
                            )
                        }
                    }
                )
                startAngle += sweepAngle
            }
        }
    }
}

private val Float.degreeToAngle get() = (this * Math.PI / 180f).toFloat()