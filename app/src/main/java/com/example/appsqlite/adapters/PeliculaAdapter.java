package com.example.appsqlite.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appsqlite.FormPeliculasActivity;
import com.example.appsqlite.R;
import com.example.appsqlite.entities.Pelicula;
import com.example.appsqlite.services.PeliculaService;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PeliculaAdapter extends RecyclerView.Adapter {

    List<Pelicula> dataPelicula;

    public PeliculaAdapter(List<Pelicula> data){
        this.dataPelicula = data;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_pelicula, parent, false);
        return new PeliculaViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,@SuppressLint("RecyclerView") int position) {

        Pelicula pelicula = dataPelicula.get(position);

        ImageView ivPeliculaPoster = holder.itemView.findViewById(R.id.ivPeliculaPoster);
        TextView tvPeliculaTitulo = holder.itemView.findViewById(R.id.tvPeliculaTitulo);
        TextView tvPeliculaSinopsis = holder.itemView.findViewById(R.id.tvPeliculaSinopsis);

        Picasso.get().load(pelicula.image).into(ivPeliculaPoster);
        tvPeliculaTitulo.setText(pelicula.titulo);
        tvPeliculaSinopsis.setText(pelicula.sinopsis);

        ImageView btnEdit = holder.itemView.findViewById(R.id.btnEdit);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(holder.itemView.getContext(), FormPeliculasActivity.class);
                intent.putExtra("PELICULA_DATA", new Gson().toJson(pelicula));

                holder.itemView.getContext().startActivity(intent);

            }
        });

        ImageView btnDelete = holder.itemView.findViewById(R.id.btnDelete);



        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://6359bece38725a1746b71b5e.mockapi.io/")// -> Aquí va la URL sin el Path
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                PeliculaService service = retrofit.create(PeliculaService.class);

                service.delete(pelicula.id).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if(response.isSuccessful()){
                            dataPelicula.remove(position);
                            notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return dataPelicula.size();
    }

    public class PeliculaViewHolder extends RecyclerView.ViewHolder{

        public PeliculaViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
