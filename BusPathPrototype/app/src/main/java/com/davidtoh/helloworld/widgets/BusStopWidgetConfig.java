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
            mAppWidgetId = extras.getInt("WIDGET_ID", 1);
            if(intent.hasExtra("startStopName")) {
                EditText stopName = (EditText) findViewById(R.id.stopWidgetEditText);
                stopName.setText(intent.getStringExtra("startStopName"));
            }
        }
        Log.d("MYTAG config onCreate", Integer.toString(mAppWidgetId));

        Button createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getBaseContext());

                Intent intent = new Intent(getApplicationContext(), BusStopStatisticsActivity.class);
                EditText stopName = (EditText) findViewById(R.id.stopWidgetEditText);
                intent.putExtra("busStopName", "" + stopName.getText());
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.shortcut_widget);
                views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);

                Log.d("MYTAG update wid", Integer.toString(mAppWidgetId)+stopName.getText());
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
        intent.putExtra("WIDGET_ID", mAppWidgetId);
        intent.putExtra("stop", 1);
        intent.putExtra("stopWidget", 1);
        startActivity(intent);
        finish();
    }
}
