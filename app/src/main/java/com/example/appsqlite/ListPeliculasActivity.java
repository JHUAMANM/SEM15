package com.example.appsqlite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.appsqlite.adapters.PeliculaAdapter;
import com.example.appsqlite.entities.Pelicula;
import com.example.appsqlite.services.PeliculaService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListPeliculasActivity extends AppCompatActivity {

    private RecyclerView rvPelicula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_peliculas);

        rvPelicula = findViewById(R.id.rvPelicula);
        rvPelicula.setLayoutManager(new LinearLayoutManager(this));

        //Retrofit retrofit = new RetrofitFactory(this).build();
        Retrofit retrofit3 = new Retrofit.Builder()
                .baseUrl("https://6359bece38725a1746b71b5e.mockapi.io/")// -> Aquí va la URL sin el Path
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PeliculaService servise = retrofit3.create(PeliculaService.class);

        servise.listPeliculas().enqueue(new Callback<List<Pelicula>>() {
            @Override
            public void onResponse(Call<List<Pelicula>> call, Response<List<Pelicula>> response) {

                rvPelicula.setAdapter(new PeliculaAdapter(response.body()));

                Log.i("MAIN_APP", "Response: " + response.body().size());
                //Log.i("NAME_APP", new Gson().toJson(data));

            }

            @Override
            public void onFailure(Call<List<Pelicula>> call, Throwable t) {
                Log.e("MAIN_APP", t.toString());
            }
        });
    }
}