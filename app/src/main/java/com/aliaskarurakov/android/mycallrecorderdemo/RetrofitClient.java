package com.aliaskarurakov.android.mycallrecorderdemo;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private Retrofit mRetrofit;
    private static RetrofitClient mInstance;

    private RetrofitClient(){

        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://kangal9k.atwebpages.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getmInstance(){
        if (mInstance == null){
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public Api getApi(){
        return mRetrofit.create(Api.class);
    }
}
