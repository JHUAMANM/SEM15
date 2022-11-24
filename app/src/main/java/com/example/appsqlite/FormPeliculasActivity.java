package com.example.appsqlite;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.appsqlite.entities.Image;
import com.example.appsqlite.entities.ImagenResponse;
import com.example.appsqlite.entities.Pelicula;
import com.example.appsqlite.factories.RetrofitFactory;
import com.example.appsqlite.services.ImageService;
import com.example.appsqlite.services.PeliculaService;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FormPeliculasActivity extends AppCompatActivity {

        private final static int CAMERA_REQUEST = 1000;

        private Pelicula pelicula = new Pelicula();
        private EditText etFormPeliculaTitulo;
        private EditText etFormPeliculainopsis;
        private EditText etFormPeliculaPosterURL;
        private Button btnSavePelicula;
        private Button btnTakePhoto;

        private ImageView ivPhoto;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_form_peliculas);

            etFormPeliculaTitulo = findViewById(R.id.etFormPeliculaTitulo);
            etFormPeliculainopsis = findViewById(R.id.etFormPeliculainopsis);
            etFormPeliculaPosterURL = findViewById(R.id.etFormPeliculaPosterURL);

            //etFormPeliculaPosterURL.setText("https://i.pinimg.com/originals/48/fc/89/48fc89a0e6eba920f0b90c1f54c58539.png");

            btnSavePelicula = findViewById(R.id.btnSavePelicula);
            btnTakePhoto = findViewById(R.id.btnTakePhoto);


            ivPhoto = findViewById(R.id.ivPhoto);


            Intent intent = getIntent();
            String peliculaJson = intent.getStringExtra("PELICULA_DATA");

            Log.i("MAIN_APP", "animeJson:" + peliculaJson);

            if(peliculaJson != null){
                pelicula = new Gson().fromJson(peliculaJson, Pelicula.class);
                etFormPeliculaTitulo.setText(pelicula.titulo);
                etFormPeliculainopsis.setText(pelicula.sinopsis);
                etFormPeliculaPosterURL.setText(pelicula.image);
            }



            //Boton para abrir la camara
            btnTakePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                        abrirCamara();
                    }
                    else{
                        requestPermissions(new String[] {Manifest.permission.CAMERA}, 100);
                    }

                }
            });



            btnSavePelicula.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    savePelicula();
                }
            });
        }


        private void savePelicula() {
            if(pelicula.titulo == "" || pelicula.sinopsis == "" || pelicula.image == ""){

                Toast.makeText(this, "LLenar datos obligatorios", Toast.LENGTH_SHORT).show();
                return;

            }
            pelicula.titulo = etFormPeliculaTitulo.getText().toString();
            pelicula.sinopsis = etFormPeliculainopsis.getText().toString();
            pelicula.image = etFormPeliculaPosterURL.getText().toString();


            Log.i("MAIN_ACTIVITY", new Gson().toJson(pelicula));

            if(pelicula.id == 0){
                callCreateAPI(pelicula);
            }
            else{
                CallUpdateAPI(pelicula);
            }
        }

        private void openGallery() {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1001);

        }

        private void abrirCamara() {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST);
        }


        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ivPhoto.setImageBitmap(imageBitmap);


                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();

                String imgBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

                Retrofit retrofit = new RetrofitFactory(this)
                        .build("https://api.imgur.com/", "Client-ID 8bcc638875f89d9");

                ImageService imageService = retrofit.create(ImageService.class);
                Image image = new Image();
                image.image = imgBase64;


                imageService.sendImage(image).enqueue(new Callback<ImagenResponse>() {
                    @Override
                    public void onResponse(Call<ImagenResponse> call, Response<ImagenResponse> response) {
                        ImagenResponse r = response.body();

                        etFormPeliculaPosterURL.setText(r.data.link);

                        //link = r.data.link;

                        GuardarPelicula();

                    }

                    @Override
                    public void onFailure(Call<ImagenResponse> call, Throwable t) {

                    }
                });




            }

            if(requestCode == 1001) {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(data.getData(), filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath);
                ivPhoto.setImageBitmap(imageBitmap);


                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();

                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                Log.i("MAIN_APP", encoded);
            }
        }

        private void GuardarPelicula() {

            Retrofit r2 = new RetrofitFactory(FormPeliculasActivity.this)
                    .build("https://api.imgur.com/", "Client-ID 8bcc638875f89d9");

            PeliculaService s = r2.create(PeliculaService.class);

            s.create(new Pelicula()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {


                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });


        }




        private void CallUpdateAPI(Pelicula pelicula) {

            Retrofit retrofit3 = new Retrofit.Builder()
                    .baseUrl("https://6359bece38725a1746b71b5e.mockapi.io/")// -> Aquí va la URL sin el Path
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            PeliculaService service = retrofit3.create(PeliculaService.class);
            service.update(pelicula, pelicula.id).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(FormPeliculasActivity.this, "Se actualizo correctamente", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(FormPeliculasActivity.this, "No se pudo actualizar los datos...", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                    Log.e("MAIN_APP", t.toString());

                }
            });
        }

        private void callCreateAPI(Pelicula pelicula) {
            Retrofit retrofit3 = new Retrofit.Builder()
                    .baseUrl("https://6359bece38725a1746b71b5e.mockapi.io/")// -> Aquí va la URL sin el Path
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            PeliculaService service = retrofit3.create(PeliculaService.class);
            service.create(pelicula).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(FormPeliculasActivity.this, "Se guardo correctamente", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(FormPeliculasActivity.this, "Error en el servidor...", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                    Log.e("MAIN_APP", t.toString());

                }
            });
        }
    }

