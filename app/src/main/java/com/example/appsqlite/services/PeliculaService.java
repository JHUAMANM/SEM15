package com.example.appsqlite.services;

import com.example.appsqlite.entities.Pelicula;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PeliculaService {

    @GET("pelicula")
    Call<List<Pelicula>> listPeliculas();

    //Guardar
    @POST("pelicula")
    Call<Void> create(@Body Pelicula pelicula);

    //Actualizar
    @PUT("pelicula/{idPelicula}")
    Call<Void> update(@Body Pelicula pelicula, @Path("idPelicula") int id);

    @DELETE("pelicula/{idPelicula}")
    Call<Void> delete(@Path("idPelicula") int id);

}
