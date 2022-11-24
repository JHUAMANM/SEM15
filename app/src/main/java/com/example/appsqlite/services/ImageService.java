package com.example.appsqlite.services;

import com.example.appsqlite.entities.Image;
import com.example.appsqlite.entities.ImagenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ImageService {

    //Guardar
    @POST("3/image")
    Call<ImagenResponse> sendImage(@Body Image image);
}
