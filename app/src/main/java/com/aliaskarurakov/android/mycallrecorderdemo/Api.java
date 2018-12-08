package com.aliaskarurakov.android.mycallrecorderdemo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface Api {

    @FormUrlEncoded
    @POST("/callrecorder/add_records.php")
    Call<ResponseBody> register (
            @Field("name") String name,
            @Field("phone") String phone,
            @Field("path") String path,
            @Field("date") String date,
            @Field("time") String time
    );

    @Multipart
    @POST("/callrecorder/add_audio.php")
    Call<ResponseBody> upload (@Part MultipartBody.Part file);

    @GET("/callrecorder/get_records.php")
    Call<List<CallDetails>> getRecords ();

    @GET
    Call<ResponseBody> downloadAudio(@Url String fileUrl);
}
