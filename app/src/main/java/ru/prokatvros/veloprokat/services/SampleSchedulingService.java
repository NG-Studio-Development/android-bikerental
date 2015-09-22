package ru.prokatvros.veloprokat.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import ru.prokatvros.veloprokat.model.requests.LoadAllDataRequest;
import ru.prokatvros.veloprokat.model.requests.PostResponseListener;
import ru.prokatvros.veloprokat.services.receivers.SampleAlarmReceiver;
import ru.prokatvros.veloprokat.utils.DataParser;


public class SampleSchedulingService extends IntentService {
    private static final String TAG = "SCHEDUL_SERVICE";

    public SampleSchedulingService() { super(SERVICE_NAME); }
    
    private static final String SERVICE_NAME = "SchedulingService";

    private NotificationManager notificationManager;
    public static final int NOTIFICATION_ID = 1;

    @Override
    protected void onHandleIntent(Intent intent) {
        //sendNotification(intent);
        sendToServer();
        Log.d(TAG, "onHandleIntent()");
        SampleAlarmReceiver.completeWakefulIntent(intent);
    }

    private void sendToServer() {
        final SampleAlarmReceiver alarm = new SampleAlarmReceiver();
        String dataFromPool = DataParser.getInstance(this).loadDataFromPool();
        Log.d(TAG, "Data from pool: " + dataFromPool);

        LoadAllDataRequest request = LoadAllDataRequest.saveRequestAllData(dataFromPool, new PostResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Save request all data SUCCESS");
                alarm.cancelAlarm(SampleSchedulingService.this);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Save request all data ERROR");
            }
        });

        Volley.newRequestQueue(this).add(request);


        /*long idAdmin = BikerentalApplication.getInstance().getAdmin().getId();
        final SampleAlarmReceiver alarm = new SampleAlarmReceiver();
        final String strJsonRent = new DataParser(this).loadDataFromPool();

        final RentRequest rentRequest = RentRequest.requestPostRent(idAdmin, strJsonRent, new PostResponseListener() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(SampleSchedulingService.this, "Success", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Response: " + response);
                alarm.cancelAlarm(SampleSchedulingService.this);
                //getHostActivity().onBackPressed();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SampleSchedulingService.this, "Error: "+error.getMessage(), Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });


        Volley.newRequestQueue(this).add(rentRequest); */
    }



    private void sendNotification(Intent intent) {
        notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);



    }
}
