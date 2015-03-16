package com.davidtoh.helloworld.core_activities;

/**
 * Created by davidtoh on 2/21/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.davidtoh.helloworld.R;
import com.davidtoh.helloworld.utils.SchedulerAddReminder;

public class SchedulerActivity extends Activity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduler);

        ListView listview = (ListView) findViewById(R.id.listView2);
        listview.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scheduler, menu);
        return true;
    }

    public void toSchedulerAddReminder(View view) {
        Intent changeToScheduler = new Intent(view.getContext(), SchedulerAddReminder.class);
        startActivityForResult(changeToScheduler, 0);
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {

        Intent intent = new Intent();
        intent.setClass(this, BusStopStatisticsActivity.class);
        intent.putExtra("position", position);

        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                //openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
