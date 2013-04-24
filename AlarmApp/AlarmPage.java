/* Author:  Edric Orense
 * File:    AlarmPage.java
 * Purpose: Basic alarm clock app that plays a song after a certain amount of time
 *			This app is part of a larger app that has an alarm clock for Muni Times (Still being implemented)
 */

package com.example.alarmapp;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends Activity{

	TextView time;
	EditText alarmInt; 
	Calendar cal;
	Timer clockTimer;
	Timer alarmTimer;
	ClockUpdaterTask clockTask;
	PlaySoundTask alarmTask;
	MediaPlayer sound;
	Switch onOff;
	TimePicker alarmTime;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        alarmTime = (TimePicker)findViewById(R.id.timePicker1);
        alarmInt = (EditText)findViewById(R.id.editText1);
        time = (TextView)findViewById(R.id.textView1);
        sound = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
        clockTask = new ClockUpdaterTask();
        clockTimer = new Timer();
        alarmTimer = new Timer();
        alarmTask = new PlaySoundTask();
        final Switch onOff = (Switch)findViewById(R.id.switch1);
        
        cal = Calendar.getInstance();

        //Create listener for the on/off button's changes
        onOff.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
            	//If "on" is toggled
            	if(onOff.isChecked()){
        			stopAlarm();
        			
        			alarmTask = new PlaySoundTask();
        			alarmTimer = new Timer();
        			//If a value is set for the alarm (in minutes)
        			if(!alarmInt.getText().toString().equals("")){
        				int min = Integer.parseInt(alarmInt.getText().toString().trim());
        				min = min*60000;
        				alarmTimer.schedule(alarmTask, 1000, 1000);
        			}
        		//"off" is toggled
        		}else{
        			stopAlarm();
        			
        		}    	
            	
            }
        });
        
        clockTimer.schedule(clockTask, 1000, 1000);
                
    }
	
	//A method to properly format time
	public String setTime(Calendar){
		String calMin;
		
		//Adds a "0" if the time is less the 10 (Writing XX:0X instead of XX:X)
		if(cal.get(Calendar.MINUTE) < 10){
			calMin = "0" + cal.get(Calendar.MINUTE);
		}else{
			calMin = "" + cal.get(Calendar.MINUTE);
		}
		
		//Concats an "AM" or "PM" to the time texbox
		if(cal.get(Calendar.AM) == Calendar.AM){
			time.setText(cal.get(Calendar.HOUR) + ":" + calMin + " am");
		}else{
 			time.setText(cal.get(Calendar.HOUR) + ":" + calMin + " pm");
		}
		return calMin;
	}
     
    private class ClockUpdaterTask extends TimerTask{

		@SuppressLint({ "NewApi", "NewApi" })
		@Override
		public void run() {
			
			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					String calMin;
					cal = Calendar.getInstance();
					calMin = setTime(cal);
				        				}
			});
		}
	}

	//Task to play the sound
    private class PlaySoundTask extends TimerTask{

		@SuppressLint({ "NewApi", "NewApi" })
		@Override
		public void run() {
			//Create thread that can alter the UI
			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					cal = Calendar.getInstance();
					//See if current time matches set alarm time
					if((cal.get(Calendar.HOUR_OF_DAY) == alarmTime.getCurrentHour()) 
							&& (cal.get(Calendar.MINUTE) == alarmTime.getCurrentMinute())){
						//If the sound is playing, stop it and rewind
						if(sound.isPlaying()){
							
							alarmTimer.cancel();
							alarmTask.cancel();
							alarmTask = new PlaySoundTask();
		        			alarmTimer = new Timer();
	        				alarmTimer.schedule(alarmTask, sound.getDuration(), sound.getDuration());

						}
						sound.start();
					}
						
				}
			});
		}
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	//Stops the alarm
	public void stopAlarm(){
		//Turn off sound if it's playing, and reset the sound
		if(sound.isPlaying()){
			sound.stop();
			sound.reset();
			sound = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
		}
		//Cancel the alarms
		alarmTimer.cancel();
		alarmTask.cancel();
		alarmTimer.purge();
	}
	
}
