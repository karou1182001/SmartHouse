package com.example.p5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class Bienvenida extends AppCompatActivity
{
    Button idBienvenida, idAdios;
    SoundPool sp;
    int sonido, sonido1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        idBienvenida= (Button) findViewById(R.id.idBienvenida);
        idAdios= (Button) findViewById(R.id.idAdios);
        sp=new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        sonido=sp.load(this, R.raw.bienvenidocorto, 1);
        sonido1=sp.load(this, R.raw.adioscorto, 1);
    }
    public void AudioBienvenida(View view)
    {
        //Reproducir el sonido
        sp.play(sonido, 1,1, 1, 0, 0);
        //Siguiente pantalla tras esperar unos segundos
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run()
            {
                Intent siguiente= new Intent(Bienvenida.this, DispositivosBT.class);
                startActivity(siguiente);
            }
        }, 3000);



    }
    public void AudioAdios(View view)
    {
        sp.play(sonido1, 1,1, 1, 0, 0);

    }

}
