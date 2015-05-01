package com.busplan.widgets;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Spinner;

import com.busplan.R;
import com.busplan.core_activities.BusStopStatisticsActivity;
import com.busplan.core_activities.SearchStopsTripPlannerActivity;

/**
 * Created by davidtoh on 4/9/15. This class is responsible for setting up and configuring new widgets.
 */
public class BusStopWidgetConfig extends Activity {

	int mAppWidgetId;

    /**
     * Called when a widget is created in order to set up and configure the new widget.
     * @param savedInstanceState passed to the superclass constructor
     */
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.shortcut_config);
		final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getBaseContext());

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt("WIDGET_ID", 1);
			if (intent.hasExtra("startStopName")) {
				EditText stopName = (EditText) findViewById(R.id.stopWidgetEditText);
				stopName.setText(intent.getStringExtra("startStopName"));
			}
		}

		Spinner spinner = (Spinner) findViewById(R.id.colorSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.colors, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		Button createButton = (Button) findViewById(R.id.createButton);
		createButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(), BusStopStatisticsActivity.class);
				EditText stopName = (EditText) findViewById(R.id.stopWidgetEditText);
				intent.putExtra("busStopName", "" + stopName.getText());
				PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
						mAppWidgetId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

				RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.shortcut_widget);
				views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);

				Spinner colorSpinner = (Spinner) findViewById(R.id.colorSpinner);
				String color = colorSpinner.getSelectedItem().toString();
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

				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString(Integer.toString(mAppWidgetId), "" + stopName.getText());
				editor.putString(Integer.toString(mAppWidgetId) + "color", color);
				editor.commit();

				appWidgetManager.updateAppWidget(mAppWidgetId, views);

				finish();
				Intent toHomeScreen = new Intent(Intent.ACTION_MAIN);
				toHomeScreen.addCategory(Intent.CATEGORY_HOME);
				toHomeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(toHomeScreen);
			}
		});
	}

    /**
     * Called after the EditText field is clicked to create an Intent to launch the Search Stops page.
     * @param view parent View
     */
    public void startNameFiller(View view) {
		Intent intent = new Intent(BusStopWidgetConfig.this, SearchStopsTripPlannerActivity.class);
		intent.putExtra("WIDGET_ID", mAppWidgetId);
		intent.putExtra("stop", 1);
		intent.putExtra("stopWidget", 1);
		startActivity(intent);
		finish();
	}
}
