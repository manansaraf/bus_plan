package com.davidtoh.helloworld.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.davidtoh.helloworld.R;
import com.davidtoh.helloworld.core_activities.BusStopStatisticsActivity;

/**
 * Created by davidtoh on 4/7/15.
 */
public class BusStopWidgetProvider extends AppWidgetProvider {

        public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

            // Perform this loop procedure for each App Widget that belongs to this provider
			for (int appWidgetId : appWidgetIds) {
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
				if (!sharedPref.contains(Integer.toString(appWidgetId))) {
					Intent intent = new Intent(context, BusStopWidgetConfig.class);
					intent.putExtra("WIDGET_ID", appWidgetId);
					PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

					// Get the layout for the App Widget and attach an on-click listener
					// to the button
					RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.shortcut_widget);
					views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);

					// Tell the AppWidgetManager to perform an update on the current app widget
					appWidgetManager.updateAppWidget(appWidgetId, views);
				} else {
					Intent intent = new Intent(context, BusStopStatisticsActivity.class);
					String stopName = sharedPref.getString(Integer.toString(appWidgetId), "NOT_FOUND");
					String color = sharedPref.getString(Integer.toString(appWidgetId) + "color", "NOT_FOUND");
					intent.putExtra("busStopName", stopName);
					PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

					RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.shortcut_widget);
					views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);

					switch (color) {
						case "Blue":
							views.setInt(R.id.widgetButton, "setBackgroundColor", Color.BLUE);
							break;
						case "White":
							views.setInt(R.id.widgetButton, "setBackgroundColor", Color.WHITE);
							break;
						case "Yellow":
							views.setInt(R.id.widgetButton, "setBackgroundColor", Color.YELLOW);
							break;
						case "Red":
							views.setInt(R.id.widgetButton, "setBackgroundColor", Color.RED);
							break;
						case "Green":
							views.setInt(R.id.widgetButton, "setBackgroundColor", Color.GREEN);
							break;
						case "Magenta":
							views.setInt(R.id.widgetButton, "setBackgroundColor", Color.MAGENTA);
							break;
						case "Grey":
							views.setInt(R.id.widgetButton, "setBackgroundColor", Color.GRAY);
							break;
						case "Cyan":
							views.setInt(R.id.widgetButton, "setBackgroundColor", Color.CYAN);
							break;
						default:
							break;
					}
					appWidgetManager.updateAppWidget(appWidgetId, views);
				}
			}
        }
}
