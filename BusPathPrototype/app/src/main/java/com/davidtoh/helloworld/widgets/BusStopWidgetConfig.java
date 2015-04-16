package com.davidtoh.helloworld.widgets;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;

import com.davidtoh.helloworld.R;
import com.davidtoh.helloworld.core_activities.BusStopStatisticsActivity;
import com.davidtoh.helloworld.core_activities.SearchStopsTripPlannerActivity;

/**
 * Created by davidtoh on 4/9/15.
 */
public class BusStopWidgetConfig extends Activity {

    int mAppWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.shortcut_config);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getBaseContext());
        RemoteViews views = new RemoteViews(getBaseContext().getPackageName(), R.layout.shortcut_widget);
        appWidgetManager.updateAppWidget(mAppWidgetId, views);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            if(intent.hasExtra("startStopName")) {
                EditText stopName = (EditText) findViewById(R.id.stopWidgetEditText);
                stopName.setText(intent.getStringExtra("startStopName"));
            }
        }


        Button createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getBaseContext());

                Intent intent = new Intent(BusStopWidgetConfig.this, BusStopWidgetProvider.class);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                EditText stopName = (EditText) findViewById(R.id.stopWidgetEditText);
                intent.putExtra("busStopName", stopName.getText());
                intent.setAction("WIDGET_CONFIGURED");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(BusStopWidgetConfig.this, 0, intent, 0);

                // Get the layout for the App Widget and attach an on-click listener
                // to the button
                RemoteViews views = new RemoteViews(BusStopWidgetConfig.this.getPackageName(), R.layout.shortcut_widget);
                views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);

                appWidgetManager.updateAppWidget(mAppWidgetId, views);

                finish();
                Intent toHomeScreen = new Intent(Intent.ACTION_MAIN);
                toHomeScreen.addCategory(Intent.CATEGORY_HOME);
                toHomeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toHomeScreen);
            }
        });
    }

    public void startNameFiller(View view) {
        Intent intent = new Intent(BusStopWidgetConfig.this, SearchStopsTripPlannerActivity.class);
        intent.putExtra("stop", 1);
        intent.putExtra("stopWidget", 1);
        startActivity(intent);
        finish();
    }
}
