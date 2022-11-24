package com.example.appsqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton btnGoPeliculaListar;
    private FloatingActionButton btnGoPeliculaForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnGoPeliculaForm = findViewById(R.id.btnGoPeliculaForm);

        btnGoPeliculaForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FormPeliculasActivity.class);
                startActivity(intent);
            }
        });

        btnGoPeliculaListar = findViewById(R.id.btnGoPeliculaListar);

        btnGoPeliculaListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListPeliculasActivity.class);
                startActivity(intent);
            }
        });

    }
}