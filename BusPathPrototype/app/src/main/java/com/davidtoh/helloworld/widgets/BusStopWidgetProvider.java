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

                Intent intent = new Intent(context, BusStopWidgetConfig.class);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                // Get the layout for the App Widget and attach an on-click listener
                // to the button
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.shortcut_widget);
                views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);

                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("onReceive", Boolean.toString(intent.hasExtra("busStopName")));
        super.onReceive(context, intent);
        if (intent.getAction().equals("WIDGET_CONFIGURED")) {

            int widgetID = Integer.parseInt(intent.getStringExtra(AppWidgetManager.EXTRA_APPWIDGET_ID));
            String stopName = intent.getStringExtra("busStopName");

            Intent stopIntent = new Intent(context, BusStopStatisticsActivity.class);
            stopIntent.putExtra("busStopName", stopName);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, stopIntent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.shortcut_widget);
            views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
        }
    }
}
