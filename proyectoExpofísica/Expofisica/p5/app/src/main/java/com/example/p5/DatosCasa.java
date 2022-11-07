package com.example.p5;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import java.io.IOException;

public class DatosCasa extends AppCompatActivity
{
    TextView txtTemp, txtHumedad;
    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    //private DatosCasa.ConnectedThread MyConexionBT;
    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la direccion MAC
    private static String address = null;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_casa);

        txtTemp=(TextView) findViewById(R.id.txtTemp);
        txtHumedad=(TextView) findViewById(R.id.txtHumedad);

        //Aquí llega la información
        bluetoothIn = new Handler()
        {
            public void handleMessage(android.os.Message msg)
            {
                if (msg.what == handlerState)
                {
                    String readMessage = (String) msg.obj;
                    DataStringIN.append(readMessage);

                    int endOfLineIndex = DataStringIN.indexOf("#");
                    int endHumedad= DataStringIN.indexOf("/");

                    if (endOfLineIndex > 0)
                    {
                        //Variable que recibe los datos de la temperatura
                        String dataInPrint = DataStringIN.substring(0, endOfLineIndex);
                        txtTemp.setText("Temperatura: " + dataInPrint);
                        DataStringIN.delete(0, DataStringIN.length());
                    }
                    if(endHumedad>0)
                    {
                        //Variable que recibe los datos de la humedad
                        String dataHum= DataStringIN.substring(0, endHumedad);
                        txtHumedad.setText("Humedad:" + dataHum);
                    }
                }
            }
        };
    }


    //MENÚ
    //Método para arrastras y ocultar el menú
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //El R es porque está en la carpeta Res
        getMenuInflater().inflate(R.menu.overflow, menu);
        return true;
    }

    //Método para asignar las opciones correspondientes al menú
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        if (id==R.id.itemDesconectar)
        {
            if (btSocket != null)
            {
                try
                {
                    //VERIFICAR ESTO
                    btSocket.close();
                } catch (IOException e)
                {
                    Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                    ;
                }
            }
            finish();

        }else if (id==R.id.itemInicio)
        {
            Intent siguiente= new Intent(this, Bienvenida.class);
            startActivity(siguiente);
        }else if (id== R.id.itemEstado)
        {
            Intent siguiente= new Intent(this, Estado.class);
            startActivity(siguiente);
        }
        else if (id==R.id.itemd)
        {
            Intent siguiente= new Intent(this, DatosCasa.class);
            startActivity(siguiente);
        }
        else if (id==R.id.itemCasa)
        {
            Intent siguiente= new Intent(this, MainActivity.class);
            startActivity(siguiente);
        }
        return super.onOptionsItemSelected(item);
    }
    //MENÚ


}
