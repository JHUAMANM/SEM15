package com.example.appsqlite.factories;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    SharedPreferences sharedPreferences;

    public RetrofitFactory(Context context) {
        sharedPreferences = context.getSharedPreferences("com.example.vj20222.MyAPPVJ" , Context.MODE_PRIVATE);
    }


    public Retrofit build(String baseUrl, String auth) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                //.header("Authorization", sharedPreferences.getString("AUTHORIZATION", null))

                Request request = original.newBuilder()
                        .header("Authorization", auth)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });


        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;

    }

}


