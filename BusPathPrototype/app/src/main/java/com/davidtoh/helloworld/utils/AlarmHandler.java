package com.davidtoh.helloworld.utils;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


import com.davidtoh.helloworld.database.AlarmDAO;


import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;

import java.util.List;
import java.util.Locale;

/**
 * Created by Elee on 2015-04-17.
 * Class that called from Alarm, and check database to push today's alarm.
 *
 */
public class AlarmHandler {
    private Context context;
    public AlarmHandler(Context context){
        this.context = context;
        try {
            putAlarms(getTodayAlarmList());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private List<AlarmInfo> getTodayAlarmList(){
        AlarmDAO alarmDAO = new AlarmDAO(context);

        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);

        Calendar calendar = Calendar.getInstance();
        weekDay = dayFormat.format(calendar.getTime()).toLowerCase();
        alarmDAO.open();
        return alarmDAO.getAlarmsByDay(weekDay);
    }
    private void putAlarms(List<AlarmInfo> alarms) throws ParseException {

        for(int i =0; i< alarms.size();i++) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            //intent.putExtra("position",alarms.get(i).getID());
            intent.putExtra("type",alarms.get(i).getID());
            String[] time = alarms.get(i).getTime().split(":");
            int hours = Integer.parseInt(time[0]);
            int minutes = Integer.parseInt(time[1]);
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY,hours);
            c.set(Calendar.MINUTE,minutes);
            c.set(Calendar.SECOND, 0);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarms.get(i).getID(), intent, 0);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        }
    }

}
