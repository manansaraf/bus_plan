package com.davidtoh.helloworld.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.davidtoh.helloworld.R;
import com.davidtoh.helloworld.core_activities.BusStopStatisticsActivity;

/**
 * Created by davidtoh on 4/7/15.
 */
public class BusStopWidgetProvider extends AppWidgetProvider {

        public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
            final int N = appWidgetIds.length;

            // Perform this loop procedure for each App Widget that belongs to this provider
            for (int i=0; i<N; i++) {
                int appWidgetId = appWidgetIds[i];
                Log.d("MYTAG in onUpdate", Integer.toString(appWidgetId));

                Intent intent = new Intent(context, BusStopWidgetConfig.class);
                intent.putExtra("WIDGET_ID", appWidgetId);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                // Get the layout for the App Widget and attach an on-click listener
                // to the button
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.shortcut_widget);
                views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);

                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
}
