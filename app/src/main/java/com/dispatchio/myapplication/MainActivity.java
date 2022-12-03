package com.dispatchio.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    String unitFinal;
    String locationFinal;
    String locationLast;
    boolean validatorX = true;

    private static final String CHANNEL_ID = "dispatchio";
    private static final String CHANNEL_NAME = "Dispatchio";
    private static final String CHANNEL_DESC = "TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Button but=(Button) findViewById(R.id.button4);

        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new doit().execute();
            }
        });
    }

    private void displayNotification(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_minor_crash_24)
                        .setContentTitle("NEW CALL")
                        .setContentText("YOU HAVE A CALL AT: " + locationFinal)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat mNotify = NotificationManagerCompat.from(this);

        mNotify.notify(1, mBuilder.build());
    }

    public class doit extends AsyncTask<Void, Void, Elements> {


        @SuppressLint("SetTextI18n")
        @Override
        protected Elements doInBackground (Void... params){

            int iti = 0;
            EditText t = findViewById(R.id.fever);
            String myUnit = t.getText().toString();
            org.jsoup.nodes.Document doc = null;

            try {
                while (validatorX == true){
                    iti = iti + 1;
                    Log.d("info", "again " + iti);
                    doc = Jsoup.connect("https://www.lcwc911.us/live-incident-list").get();
                    Elements area;
                    if (doc.toString().contains("Active Fire Incidents")
                            && doc.toString().contains("Active Traffic Incidents")) {
                        area = doc.getElementsByClass("live-incident-container ");
                    } else if (doc.toString().contains("Active Fire Incidents")) {
                        area = doc.getElementsByClass("live-incident-container last");
                    } else {
                        area = doc.getElementsByClass("live-incident-container first");
                    }
                    Elements call = area.first().getElementsByClass("odd first");;
                    Elements location = call.first().getElementsByClass("location-row");
                    Elements unit = call.first().getElementsByClass("units-row");

                    if (unit.toString().contains(myUnit) && !location.text().equals(locationLast)){
                        Log.d("info", "call");
                        locationLast = location.text();
                        unitFinal = unit.toString();
                        locationFinal = location.text();
                        displayNotification();
                    }
                    findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("info", "stopped");
                            validatorX = false;
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}