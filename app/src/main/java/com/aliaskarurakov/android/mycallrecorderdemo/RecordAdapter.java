package com.aliaskarurakov.android.mycallrecorderdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v4.content.FileProvider.getUriForFile;


public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.MyViewHolder> {

    Context context;
    List<CallDetails> mCallDetails;
    //String path ="";

    public RecordAdapter(Context context, List<CallDetails> callDetails) {
        this.context = context;
        this.mCallDetails = callDetails;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView phone, time, date, name;

        public MyViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            name = (TextView) itemView.findViewById(R.id.name);
            phone = (TextView) itemView.findViewById(R.id.number);
            time = (TextView) itemView.findViewById(R.id.time);
        }

        public void bind(final String path){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (path!="" && !path.isEmpty()) {
                        getAudio(path);
                    }
                }
            });
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
        View v = layoutInflator.inflate(R.layout.date_layout, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        mCallDetails.get(position);
        holder.name.setText(mCallDetails.get(position).getName());
        holder.phone.setText(mCallDetails.get(position).getPhone());
        holder.date.setText(mCallDetails.get(position).getDate());
        holder.time.setText(mCallDetails.get(position).getTime());
        String path = mCallDetails.get(position).getPath();
        holder.bind(path);
    }

    @Override
    public int getItemCount() {
        return mCallDetails.size();
    }

    public void getAudio(String url){
        Call<ResponseBody> call = RetrofitClient
                .getmInstance()
                .getApi()
                .downloadAudio(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("", "server contacted and has file");

                    boolean writtenToDisk = writeResponseBodyToDisk(response.body());
                    Log.d("", "file download was a success? " + writtenToDisk);

                    if (writtenToDisk){
                        playAudio();
                    }
                } else {
                    Log.d("", "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {

            File audioFile2 = new File(Environment.getExternalStorageDirectory() + "/tempAudioFile/");
            if(!audioFile2.exists())
                audioFile2.mkdir();

            File audioFile = new File(Environment.getExternalStorageDirectory() + "/tempAudioFile/temp.mp4");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(audioFile);
                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public void playAudio(){
        String path = Environment.getExternalStorageDirectory() + "/tempAudioFile/temp.mp4/";
        Log.d("path", "onClick: path = "+path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(path);
        Log.i("", "onResponse: name "+file.getName());
        if (file.isFile()){
            Log.i("", "onResponse: I EXIST");
        } else {
            Log.i("", "onResponse: dont exist?");
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(getUriForFile(context,"com.aliaskarurakov.android.mycallrecorderdemo",file), "audio/*");
        context.startActivity(intent);

    }
}
