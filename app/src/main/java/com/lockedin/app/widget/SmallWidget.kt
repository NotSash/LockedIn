package com.lockedin.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.lockedin.app.MainActivity

/**
 * Small 2x2 widget: logo + "Generate" button which launches app.
 *
 * The actual password generation happens inside the app for security.
 */
class SmallWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            LockedInWidgetTheme.Content(darkTheme = true) {
                SmallWidgetContent()
            }
        }
    }
}

@Composable
private fun SmallWidgetContent() {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .background(GlanceTheme.colors.background)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🔒",
                style = TextStyle(color = ColorProvider(GlanceTheme.colors.onBackground))
            )
            androidx.glance.layout.Spacer(modifier = GlanceModifier.padding(4.dp))
            Text(
                text = "Generate",
                style = TextStyle(color = ColorProvider(GlanceTheme.colors.primary)),
                modifier = GlanceModifier.clickable(
                    onClick = actionStartActivity(MainActivity::class.java)
                )
            )
        }
    }
}

