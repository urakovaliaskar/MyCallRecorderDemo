package com.aliaskarurakov.android.mycallrecorderdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    RecordAdapter mAdapter;
    List<CallDetails> mCallDetails;

    private final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putInt("numOfCalls", 0).apply();

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        if (checkPermission()){
            Toast.makeText(getApplicationContext(), "Permission already granted", Toast.LENGTH_LONG).show();
        }

        mCallDetails = new ArrayList<>();

        getRecords();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRecords();
    }

    private boolean checkPermission(){
        int i = 0;
        String[] permission={
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        List<String> reqPerm = new ArrayList<>();

        for (String p:permission){
            int resultPhone = ContextCompat.checkSelfPermission(MainActivity.this, p);
            if (resultPhone == PackageManager.PERMISSION_GRANTED){
                i++;
            } else {
                reqPerm.add(p);
            }
        }

        if (i==5){
            return true;
        } else {
            return requestPermission(reqPerm);
        }

    }

    private boolean requestPermission(List<String> perm) {
        String[] listReq = new String[perm.size()];
        listReq = perm.toArray(listReq);
        for (String permissions:listReq){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissions)){
                Toast.makeText(getApplicationContext(), "Phone permission needed for " + permissions, Toast.LENGTH_SHORT).show();
            }
        }
        ActivityCompat.requestPermissions(MainActivity.this, listReq, 1);

        return false;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(getApplicationContext(),"Permission Granted to access Phone calls",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(),"You can't access Phone calls",Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void getRecords(){
        Call<List<CallDetails>> call = RetrofitClient
                .getmInstance()
                .getApi()
                .getRecords();

        call.enqueue(new Callback<List<CallDetails>>() {
            @Override
            public void onResponse(Call<List<CallDetails>> call, Response<List<CallDetails>> response) {

                mCallDetails = response.body();
                Log.d("onResponse: ", mCallDetails.toString());

                mAdapter = new RecordAdapter(getApplicationContext(), mCallDetails);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<List<CallDetails>> call, Throwable t) {
                Log.i(TAG, "onFailure: "+t.getMessage());
            }
        });
    }

}
