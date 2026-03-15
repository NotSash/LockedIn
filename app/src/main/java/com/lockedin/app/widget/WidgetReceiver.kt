package com.lockedin.app.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * GlanceAppWidgetReceiver for LockedIn widgets.
 *
 * Registers the small widget as the primary implementation; medium widget
 * can be registered via a separate receiver if needed.
 */
class SmallWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SmallWidget()
}

