package com.lockedin.app.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.GlanceComposable
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.lockedin.app.MainActivity

/**
 * Medium 4x2 widget: search bar + a few recent sites + quick generate.
 *
 * For security, actual password values are never shown in the widget; tapping
 * entries opens the main app where authentication is enforced.
 */
class MediumWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { MediumWidgetContent() }
    }
}

@Composable
@GlanceComposable
private fun MediumWidgetContent() {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .background(ColorProvider(androidx.compose.ui.graphics.Color(0xFF0A0E1A)))
            .padding(12.dp),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(ColorProvider(androidx.compose.ui.graphics.Color(0x33FFFFFF)))
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .clickable(onClick = actionStartActivity(MainActivity::class.java))
        ) {
            Text(
                text = "Search LockedIn",
                style = TextStyle(color = ColorProvider(androidx.compose.ui.graphics.Color(0xB3F1F5F9)))
            )
        }

        androidx.glance.layout.Spacer(modifier = GlanceModifier.padding(6.dp))

        Column(
            verticalAlignment = Alignment.Top
        ) {
            repeat(3) { index ->
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .clickable(onClick = actionStartActivity(MainActivity::class.java)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent ${index + 1}",
                        style = TextStyle(color = ColorProvider(androidx.compose.ui.graphics.Color(0xFFF1F5F9)))
                    )
                }
            }
        }

        androidx.glance.layout.Spacer(modifier = GlanceModifier.padding(4.dp))

        Text(
            text = "Generate",
            style = TextStyle(color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF7C4DFF))),
            modifier = GlanceModifier
                .clickable(onClick = actionStartActivity(MainActivity::class.java))
        )
    }
}

