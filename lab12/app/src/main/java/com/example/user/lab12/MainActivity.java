package com.example.user.lab12;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver recviver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            data data1 = new Gson().fromJson(intent.getExtras().getString("json"), data.class);

            final String[] items = new  String[data1.result.results.length];

            for (int i = 0; i < items.length; i++)
                items[i] = "\n 列車即將進入 :" + data1.result.results[i].Station + "\n 列車行駛目的地 :" + data1.result.results[i].Destination;

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(MainActivity.this).setTitle("台北捷運列車到站站名").setItems(items, null).show();
                }
            });
        }
    };
  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         registerReceiver(recviver, new IntentFilter("MyMessage"));

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Request req = new Request.Builder().url("https://data.taipei/opendata/datalist/apiAccess?scope=resourceAquire&rid=55ec6d6e-dc5c-4268-a725-d04cc262172b").build();

                new OkHttpClient().newCall(req).enqueue(new Callback() {

                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.e("查詢失敗", e.toString());
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        sendBroadcast(new Intent("MyMessage").putExtra("json", response.body().string()));
                    }
                });

            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(recviver);
    }
}
