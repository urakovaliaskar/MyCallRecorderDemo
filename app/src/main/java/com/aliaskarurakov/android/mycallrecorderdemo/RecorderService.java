package com.aliaskarurakov.android.mycallrecorderdemo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Network;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RecorderService extends Service {

    CallDetails mCallDetails;
    MediaRecorder mRecorder;
    Calendar calendar = Calendar.getInstance();
    static final String TAG=" Inside Service";
    boolean isRecording = false;

    String audioUrl = "http://kangal9k.atwebpages.com/callrecorder/audio/";

    String name = "";
    String phone = "";
    String path = "";
    String filename = "";
    String storagePath = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mRecorder = new MediaRecorder();
        mRecorder.reset();

        phone = intent.getStringExtra("number");
        Log.d(TAG, "onStartCommand: number = "+phone);

        name = getContactName(phone, getApplicationContext());
        Log.i(TAG, "onStartCommand: name = "+name);

        filename = phone+"_"+getTime()+".mp4";

        storagePath = getPath();

        path = ""+storagePath+"/"+filename;
        Log.i(TAG, "onStartCommand: "+path);

        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setOutputFile(path);
        try {
            mRecorder.prepare();
            mRecorder.start();
            isRecording = true;
            Log.d(TAG, "onStartCommand: "+"Recording started");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;
        Log.d(TAG, "onDestroy: "+"Recording stopped");

        isRecording = false;

        if (!isRecording) {
            addRecords();
        }
    }

    private void addRecords() {

        Call<ResponseBody> call = RetrofitClient
                .getmInstance()
                .getApi()
                .register(name, phone, audioUrl+filename, getDate(), getTime());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String s = response.body().string();
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    uploadAudio();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadAudio(){

        File file = new File(path);
        Log.i(TAG, "uploadAudio: path = "+path);
        final RequestBody requestFile = RequestBody.create(MediaType.parse("audio/*"), file);
        Log.i(TAG, "uploadAudio: filename: " +file.getName());
        MultipartBody.Part body = MultipartBody.Part.createFormData("audio", file.getName(), requestFile);

        Call<ResponseBody> call = RetrofitClient
                .getmInstance()
                .getApi()
                .upload(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String s = null;
                try {
                    s = response.body().string();
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onResponse: response = "+s);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //deleteAudioFromMemory(path);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onFailure: response = "+t.getMessage());
            }
        });
    }

    private void deleteAudioFromMemory(String path) {
        File file = new File(path);
        if (file.exists()){
            file.delete();
            Log.i(TAG, "deleteAudioFromMemory: "+filename+ " was deleted");
        } else {
            Log.i(TAG, "deleteAudioFromMemory: file does not exist");
        }
    }


    public String getDate(){

        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int day=calendar.get(Calendar.DATE);
        String date=String.valueOf(day)+"."+String.valueOf(month)+"."+String.valueOf(year);
        Log.d(TAG, "Date "+date);
        return date;
    }

    public String getTime() {
        String am_pm="";
        int sec=calendar.get(Calendar.SECOND);
        int min=calendar.get(Calendar.MINUTE);
        int hr=calendar.get(Calendar.HOUR);
        int amPm=calendar.get(Calendar.AM_PM);
        if(amPm==1)
            am_pm="PM";
        else if(amPm==0)
            am_pm="AM";

        String time=String.valueOf(hr)+":"+String.valueOf(min)+":"+String.valueOf(sec)+""+am_pm;

        Log.d(TAG, "Date "+time);
        return time;
    }

    public String getPath(){
        String internalFile = getDate();
        String appName = String.valueOf(getApplicationInfo().loadLabel(getPackageManager()));
        Log.i(TAG, "getPath: appNAme = " + appName);
        File file=new File(Environment.getExternalStorageDirectory()+"/"+appName+"/");
        File file1=new File(Environment.getExternalStorageDirectory()+"/"+appName+"/"+internalFile+"/");
        if(!file.exists())
        {
            file.mkdir();
        }
        if(!file1.exists())
            file1.mkdir();


        String path=file1.getAbsolutePath();
        Log.d(TAG, "Path "+path);

        return path;
    }

    public String getContactName(final String number,Context context) {

        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(number));
        String[] projection=new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
        String contactName="";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);
        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }

        if(contactName!=null && !contactName.equals(""))
            return contactName;
        else
            return "Unknown";
    }

}
