package com.example.p5;


import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
{
    //Declaramos variables tipo botón que vamos a relacionar con las variables de la interfaz
    //Atributos
    Button bv1, bv2, bv3, bApagarAbanico;
    Switch  enchufe1,enchufe2,idSwLucesExternas, idSwLucesInternas, idSwCerradura, idSwCafetera;
    TextView idBufferIn, txtLucesExternas, txtLucesInternas, txtSeguro, txtCafetera, txtAbanico;
    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la direccion MAC
    private static String address = null;


    @SuppressLint("HandlerLeak")
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Relacionar las variables botón con las respectivas interfaces

        bv1= (Button) findViewById(R.id.bv1);
        bv2= (Button) findViewById(R.id.bv2);
        bv3= (Button) findViewById(R.id.bv3);
        bApagarAbanico= (Button) findViewById(R.id.bApagarAbanico);
        idBufferIn = (TextView) findViewById(R.id.idBufferIn);

        txtLucesExternas=(TextView) findViewById(R.id.txtLucesExternas);
        txtLucesInternas=(TextView) findViewById(R.id.txtLucesInternas);
        txtSeguro=(TextView) findViewById(R.id.txtSeguro);
        txtCafetera=(TextView) findViewById(R.id.txtCafetera);
        txtAbanico=(TextView) findViewById(R.id.txtAbanico);

        idSwLucesExternas=(Switch) findViewById(R.id.idSwLucesExternas);
        idSwLucesInternas=(Switch) findViewById(R.id.idSwLucesInternas);
        idSwCerradura=(Switch) findViewById(R.id.idSwCerradura);
        idSwCafetera=(Switch) findViewById(R.id.idSwCafetera);
        enchufe1=(Switch) findViewById(R.id.enchufe1);
        enchufe2=(Switch) findViewById(R.id.enchufe2);
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

                    if (endOfLineIndex > 0)
                    {
                        //Variable que recibe los datos
                        String dataInPrint = DataStringIN.substring(0, endOfLineIndex);
                        idBufferIn.setText("Temperatura: " + dataInPrint);//<-<- PARTE A MODIFICAR >->->
                        DataStringIN.delete(0, DataStringIN.length());
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter
        VerificarEstadoBT();
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

        return super.onOptionsItemSelected(item);
    }
//MENU





    //Crear una conexión segura
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        //crea un conexion de salida segura para el dispositivo
        //usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //Consigue la direccion MAC desde DeviceListActivity via intent
        Intent intent = getIntent();
        //Consigue la direccion MAC desde DeviceListActivity via EXTRA
        address = intent.getStringExtra(DispositivosBT.EXTRA_DEVICE_ADDRESS);
        //Setea la direccion MAC
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket falló", Toast.LENGTH_LONG).show();
        }
        // Establece la conexión con el socket Bluetooth.
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
            }
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try { // Cuando se sale de la aplicación esta parte permite
            // que no se deje abierto el socket
            btSocket.close();
        } catch (IOException e2) {

        }
    }

    //Comprueba que el dispositivo Bluetooth Bluetooth está disponible y solicita que se active si está desactivado
    //Es decir, verifica si el dispositivo soporta o no soporta bluetooth.
    private void VerificarEstadoBT()
    {

        if (btAdapter == null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }






    //ACCIONES DE LOS SWITCHES


    public void onclickLE(View view)
    {
        //Luces externas
        if (view.getId()==R.id.idSwLucesExternas)
        {
            //Evento de arrastrar
            if(idSwLucesExternas.isChecked()){
                txtLucesExternas.setText("Prendidas");
                MyConexionBT.write("3");

            }else{
                MyConexionBT.write("4");
                txtLucesExternas.setText("Apagadas");}
        }
    }

    public void onclickLI(View view)
    {
        //Luces internas
        if (view.getId()==R.id.idSwLucesInternas)
        {
            //Evento de arrastrar
            if(idSwLucesInternas.isChecked()){
                txtLucesInternas.setText("Prendidas");
                MyConexionBT.write("5");

            }else{
                MyConexionBT.write("6");
                txtLucesInternas.setText("Apagadas");}
        }
    }

    public void onclickSeguro(View view)
    {
        //Seguro
        if (view.getId()==R.id.idSwCerradura)
        {
            //Evento de arrastrar
            if(idSwCerradura.isChecked()){
                txtSeguro.setText("Activado");
                MyConexionBT.write("7");

            }else{
                MyConexionBT.write("8");
                txtSeguro.setText("Desactivado");}
        }
    }
    public void onclickCafetera(View view)
    {
        //Cafetera
        if (view.getId()==R.id.idSwCafetera)
        {
            //Evento de arrastrar
            if(idSwCafetera.isChecked()){
                txtCafetera.setText("Prendida");
                MyConexionBT.write("9");

            }else{
                MyConexionBT.write("a");
                txtCafetera.setText("Apagada");}
        }
    }
    //Onclick para botones
   // bv1.setOnClickListener(new View.OnClickListener()
    //{
        public void onclickB1(View view)
        {
            txtAbanico.setText("V1");
            MyConexionBT.write("b");
        }
    //});

    public void onclickB2(View view)
    {
        txtAbanico.setText("V2");
        MyConexionBT.write("c");
    }
    public void onclickB3(View view)
    {
        txtAbanico.setText("V3");
        MyConexionBT.write("d");
    }
    public void onclickAAbanico(View view)
    {
        txtAbanico.setText("Apagado");
        MyConexionBT.write("e");
    }
    public void subirP(View view)
    {

        MyConexionBT.write("f");
    }
    public void pararP(View view)
    {

        MyConexionBT.write("h");
    }
    public void bajarP(View view)
    {

        MyConexionBT.write("i");
    }

    public void ench1(View view)
    {
        //Luces externas
        if (view.getId()==R.id.enchufe1)
        {
            //Evento de arrastrar
            if(enchufe1.isChecked()){

                MyConexionBT.write("j");

            }else{
                MyConexionBT.write("k");
                ;}
        }
    }
    public void ench2(View view)
    {
        //Luces externas
        if (view.getId()==R.id.enchufe2)
        {
            //Evento de arrastrar
            if(enchufe1.isChecked()){

                MyConexionBT.write("l");

            }else{
                MyConexionBT.write("m");
                ;}
        }
    }


    //Crea la clase que permite crear el evento de conexion
    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;


        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        //Envio de trama
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            } catch (IOException e) {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(getBaseContext(), "La Conexión falló", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }



}
