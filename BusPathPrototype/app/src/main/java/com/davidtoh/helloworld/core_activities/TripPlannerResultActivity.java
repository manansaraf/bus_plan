package com.davidtoh.helloworld.core_activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.davidtoh.helloworld.R;
import com.davidtoh.helloworld.database.BusStopsDAO;
import com.davidtoh.helloworld.utils.BusStopInfo;
import com.davidtoh.helloworld.utils.DateParser;
import com.davidtoh.helloworld.utils.ExpandableListAdapterTripPlanner;
import com.davidtoh.helloworld.utils.TripInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TripPlannerResultActivity extends Activity {
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_planner_result);
        Intent intent = getIntent();
        String end_name="";
        String start_name="";
        String timeAndDate="";
        if(intent.hasExtra("endStopName")&&intent.hasExtra("startStopName")) {
            end_name = intent.getStringExtra("endStopName");
            start_name = intent.getStringExtra("startStopName");
        }
        if(intent.hasExtra("date")){
            String date = intent.getStringExtra("date");
            timeAndDate+="&date="+date;
        }
        if(intent.hasExtra("time")){
            String time = intent.getStringExtra("time");
            timeAndDate+="&time="+time;
        }
        makeAPICalls(start_name,end_name,timeAndDate);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip_planner_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void makeAPICalls(String start_name,String end_name,String timeAndDate) {

        BusStopsDAO busStopsDAO = new BusStopsDAO(this);
        busStopsDAO.open();
        BusStopInfo start_busStopInfo = busStopsDAO.getStop(start_name);

        String start_stopID = start_busStopInfo.getStopID();
        BusStopInfo end_busStopInfo = busStopsDAO.getStop(end_name);

        String end_stopID = end_busStopInfo.getStopID();

        TextView textView = (TextView) findViewById(R.id.statisticsStatusView);
        textView.setVisibility(View.GONE);
        //check connection
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        String tripPlannerURL = "https://developer.cumtd.com/api/v2.2/JSON/GetPlannedTripsByStops?key="
                + getResources().getString(R.string.apiKey) + "&origin_stop_id=" + start_stopID+
                "&destination_stop_id=" + end_stopID+timeAndDate;
        Log.v("Android",tripPlannerURL);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        if (networkInfo != null && networkInfo.isConnected()) {
            ShowProgressBar();
            new getTrip().execute(tripPlannerURL);
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText("No network connection available.");
        }
    }
    public String makeConnection(String urlString) throws IOException {
        InputStream inputStream = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            inputStream = conn.getInputStream();
            return readIt(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
    public String readIt(InputStream stream) throws IOException {
        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

        StringBuilder JSONResult = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            JSONResult.append(line);
        }
        reader.close();
        return JSONResult.toString();
    }
    private class getTrip extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                createLists(urls[0]);
                return "";

            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            TextView textView = (TextView) findViewById(R.id.statisticsStatusView);
            if(!result.equals("")) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(result);
            }
            CloseProgressBar();
        }
    }
    private void createLists(String tripPlannerURL) throws IOException {
        String tripJSON = makeConnection(tripPlannerURL);
        List<List<TripInfo>> TripList = buildTripJSON(tripJSON);

        HashMap<String, List<String>> fullTripInfoList = new HashMap<>();
        List<String> listDataHeader = new ArrayList<>();
        int i=0;
        for (List<TripInfo> stop : TripList) {
            i++;
            List<String> busStopInfoList = new ArrayList<>();
            for(TripInfo trip : stop){
                if(trip.getBus_name()==null){
                    String inside = "Walk from "+trip.getStartBusStop()+" to " +trip.getEndBusStop() +
                            " for "+trip.getWalk_distance()+" miles";
                    String up = trip.getStartTime();
                    busStopInfoList.add(up + ":" + inside);
                }
                else{
                    String inside = "Board "+trip.getBus_name()+" from "+trip.getStartBusStop();
                    String up = trip.getStartTime();
                    busStopInfoList.add(up+":"+inside);
                    inside = "Get off at "+trip.getEndBusStop();
                    up = trip.getEndTime();
                    busStopInfoList.add(up+":"+inside);
                }
            }
            fullTripInfoList.put("Trip "+i, busStopInfoList);
            listDataHeader.add("Trip "+i);
        }

        final ExpandableListAdapterTripPlanner listAdapter = new ExpandableListAdapterTripPlanner(this, listDataHeader, fullTripInfoList);
        final ExpandableListView expListView = (ExpandableListView) findViewById(R.id.expandRouteView);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                expListView.setAdapter(listAdapter);
                int count = listAdapter.getGroupCount();
                for(int pos = 0; pos < count; pos++) {
                    expListView.expandGroup(pos);
                }
            }
        });
    }
    public void ShowProgressBar(){
        spinner.setVisibility(View.VISIBLE);
    }
    public void CloseProgressBar(){
        spinner.setVisibility(View.GONE);
    }
    public List<List<TripInfo>> buildTripJSON(String str) throws IOException{
        JSONObject JObject;
        List<List<TripInfo>> trips = null;
        try {
            JObject = new JSONObject(str);
            JSONArray JArray = JObject.getJSONArray("itineraries");
            trips  = new ArrayList<>();
            for (int i = 0; i < JArray.length(); i++) {
                JSONObject JObject_inside = JArray.getJSONObject(i);
                JSONArray JArray_inside = JObject_inside.getJSONArray("legs");
                List<TripInfo> list = new ArrayList<>();
                for (int j = 0; j < JArray_inside.length(); j++) {
                    String start_stop,end_stop,start_time,end_time;
                    JSONObject JObject_leg = JArray_inside.getJSONObject(j);
                    TripInfo trip;
                    if(JObject_leg.getString("type").equals("Walk")){
                        JSONObject walk = JObject_leg.getJSONObject("walk");
                        JSONObject beginStop = walk.getJSONObject("begin");
                        start_stop = beginStop.getString("name");
                        start_time = beginStop.getString("time");
                        Date date = DateParser.parse(start_time);
                        start_time=DateParser.toString(date);
                        JSONObject endStop = walk.getJSONObject("end");
                        end_stop = endStop.getString("name");
                        end_time = endStop.getString("time");
                        date = DateParser.parse(end_time);
                        end_time=DateParser.toString(date);
                        trip = new TripInfo(start_stop, end_stop, start_time, end_time, walk.getDouble("distance"), null);
                        list.add(trip);
                    }
                    else {
                        JSONArray service_array = JObject_leg.getJSONArray("services");
                        for(int k=0;k<service_array.length();k++){
                            JSONObject service=service_array.getJSONObject(k);
                            JSONObject route = service.getJSONObject("route");
                            JSONObject beginStop = service.getJSONObject("begin");
                            start_stop = beginStop.getString("name");
                            start_time = beginStop.getString("time");
                            Date date = DateParser.parse(start_time);
                            start_time=DateParser.toString(date);
                            JSONObject endStop = service.getJSONObject("end");
                            end_stop = endStop.getString("name");
                            end_time = endStop.getString("time");
                            date = DateParser.parse(end_time);
                            end_time=DateParser.toString(date);
                            JSONObject trip_json = service.getJSONObject("trip");
                            String direction = trip_json.getString("direction");
                            char direct= direction.charAt(0);
                            String bus_name = route.getString("route_short_name")+direct+" "+route.getString("route_id");
                            trip = new TripInfo(start_stop, end_stop, start_time, end_time, 0,bus_name );
                            list.add(trip);
                        }
                    }


                }
                trips.add(list);
            }
        }catch (Exception e) {
            Log.e("JSON ERROR: ", e.getMessage());
        }
        return trips;
    }
}