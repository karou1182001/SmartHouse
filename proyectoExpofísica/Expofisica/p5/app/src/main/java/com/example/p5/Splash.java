package com.example.p5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable()
        {
            public void run()
            {
                Intent intent= new Intent (Splash.this, Bienvenida.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
