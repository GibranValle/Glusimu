package app.proyectoterminal.upibi.glusimu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import app.proyectoterminal.upibi.glusimu.Bluetooth.BluetoothSPP;
import app.proyectoterminal.upibi.glusimu.Bluetooth.BluetoothState;
import app.proyectoterminal.upibi.glusimu.Bluetooth.DeviceList;

public class Interfaz extends AppCompatActivity implements View.OnTouchListener {

    String TAG = "Interfaz";

    // VARIABLES PARA GRAFICAR
    ImageView espacio;
    Button estado_conexion;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;
    TextView consola, titulo;
    int alto, ancho;
    float x, y;

    boolean conectado = false;

    String accion = "nula";
    int objetivo = 0;
    String estado = "nula";
    int gasto = 0;

    // PARA RECORDAR DATOS
    SharedPreferences respaldo;
    SharedPreferences.Editor editor;
    //editor = respaldo.edit();
    //editor.putInt("max", max);
    //if(editor.commit())

    // Variable bluetooth
    BluetoothSPP bt;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interfaz);

        espacio = (ImageView) findViewById(R.id.espacio);
        espacio.setOnTouchListener(this);
        estado_conexion = (Button) findViewById(R.id.estado_conexion);
        consola = (TextView) findViewById(R.id.consola);
        titulo = (TextView) findViewById(R.id.titulo_simulador);

        accion = "nula";
        respaldo = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
        editor = respaldo.edit();
        editor.putString("accion",accion);
        if(editor.commit())
        {
        }

        //  ----------------- BLUETOOTH -----------------------------------------//
        bt = new BluetoothSPP(this);
        if(!bt.isBluetoothAvailable())
        {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }


        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message)
            {
                String dato;
                int valor;
                //textRead.append(message + "\n");
                if(message.startsWith("G"))
                {
                    Log.d(TAG,"mensaje recibido: "+message+" largo: "+message.length());
                    dato = message.substring(1,message.length());
                    valor = Integer.parseInt(dato);
                    Log.d(TAG,"conversion correcta a entero: "+valor);
                    consola.setText("Glucemia:\n"+valor + " mg/dL");

                    editor = respaldo.edit();
                    editor.putInt("glucemia", valor);
                    if(editor.commit())
                    {
                    }
                }

                if(message.startsWith("L"))
                {
                    Log.d(TAG,"mensaje recibido: "+message+" largo: "+message.length());
                    dato = message.substring(1,message.length());
                    valor = Integer.parseInt(dato);
                    Log.d(TAG,"conversion correcta a entero: LED: "+valor);

                    editor = respaldo.edit();
                    editor.putInt("ledsChecked", valor);
                    if(editor.commit())
                    {
                    }
                }

                if(message.startsWith("V"))
                {
                    Log.d(TAG,"mensaje recibido: "+message+" largo: "+message.length());
                    dato = message.substring(1,message.length());
                    valor = Integer.parseInt(dato);
                    Log.d(TAG,"conversion correcta a entero: "+valor);
                    consola.setText("Glucemia:\n"+valor + " mg/dL");

                    editor = respaldo.edit();
                    editor.putInt("valvulasChecked", valor);
                    if(editor.commit())
                    {
                    }
                }


            }
        });


        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener()
        {
            public void onDeviceDisconnected()
            {
                // CAMBIAR TEXTO DE CONSOLA CONEXION
                estado_conexion.setText(R.string.bt_dc);
                estado_conexion.setBackgroundResource(R.color.colorOff);
                Log.e(TAG,"SE PERDIÓ LA CONEXION");
                consola.setText("Glucemia:\n - - mg/dL");
                consola.setBackgroundResource(R.color.colorOff);
                conectado = false;
            }

            public void onDeviceConnectionFailed()
            {
                // CAMBIAR TEXTO DE CONSOLA CONEXION
                estado_conexion.setText(R.string.bt_error);
                estado_conexion.setBackgroundResource(R.color.colorOff);
                Log.e(TAG,"SE PERDIÓ LA CONEXION");
                consola.setText("Glucemia:\n - - mg/dL");
                consola.setBackgroundResource(R.color.colorOff);
                conectado = false;
            }

            public void onDeviceConnected(String name, String address)
            {
                conectado = true;
            }
        });

        bt.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener()
        {
            public void onServiceStateChanged(int state)
            {
                if(state == BluetoothState.STATE_CONNECTED)
                {
                    estado_conexion.setText(R.string.bt_ct);
                    estado_conexion.setBackgroundResource(R.color.colorOn);
                    consola.setBackgroundResource(R.color.colorOn);
                    conectado = true;
                }
                else if(state == BluetoothState.STATE_CONNECTING)
                {
                    estado_conexion.setText(R.string.bt_cting);
                    estado_conexion.setBackgroundResource(R.color.colorConecting);
                }
                else if(state == BluetoothState.STATE_LISTEN)
                {
                    // Do something when device is waiting for connection
                }
                else if(state == BluetoothState.STATE_NONE)
                {
                    consola.setText("Glucemia:\n - - mg/dL");
                    estado_conexion.setText(R.string.bt_off);
                    estado_conexion.setBackgroundResource(R.color.colorOff);
                    consola.setBackgroundResource(R.color.colorOff);

                    // Do something when device don't have any connection
                }
            }
        });
        //  ----------------- BLUETOOTH -----------------------------------------//

        // medidor

        ViewTreeObserver vto = espacio.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                // CONSIGUE LOS DATOS DE ANCHO Y ALTO HASTA ESTE PUNTO
                alto = espacio.getHeight();
                ancho = espacio.getWidth();
                Log.i(TAG,"alto: "+alto+" ancho: "+ancho);

                ViewTreeObserver obs = espacio.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                bitmap = Bitmap.createBitmap(ancho, alto, Bitmap.Config.ARGB_8888);
                Bitmap b= BitmapFactory.decodeResource(getResources(), R.drawable.contorno_ps);
                canvas = new Canvas(bitmap);
                paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                canvas.drawBitmap(b,0,0,paint);
                espacio.setImageBitmap(b);
            }
        });
    }
    protected void onResume()
    {
        super.onResume();
        Log.v(TAG, "On Resume");

        respaldo = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
        accion = respaldo.getString("accion","nula");
        objetivo = respaldo.getInt("objetivo", 100);
        estado = respaldo.getString("estado", "nula");

        if(accion.equals("nula"))
        {
            titulo.setText(R.string.titulo_nulo);
        }

        if(accion.equals("higado"))
        {
            titulo.setText("Elevando niveles de glucosa: "+objetivo);
            // CAMBIAR GLUCOSA
            if(conectado)
            {
                if( objetivo >= 100 && objetivo <= 999)
                {
                    bt.send("H0"+objetivo,true);
                }

                else if(objetivo >= 10 && objetivo <= 99)
                {
                    bt.send("H00"+objetivo,true);
                }

                else if(objetivo >= 0 && objetivo <= 9)
                {
                    bt.send("H000"+objetivo,true);
                }
            }
        }

        if(accion.equals("intestino"))
        {
            titulo.setText("Elevando niveles de glucosa: "+objetivo);
            // CAMBIAR GLUCOSA
            if(conectado)
            {
                if( objetivo >= 100 && objetivo <= 999)
                {
                    bt.send("I0"+objetivo,true);
                }

                else if(objetivo >= 10 && objetivo <= 99)
                {
                    bt.send("I00"+objetivo,true);
                }

                else if(objetivo >= 0 && objetivo <= 9)
                {
                    bt.send("I000"+objetivo,true);
                }
            }
        }

        if(accion.equals("musculos"))
        {
            titulo.setText("Disminuyendo niveles de glucosa: "+objetivo);
            // CAMBIAR GLUCOSA
            if(conectado)
            {
                if( objetivo >= 100 && objetivo <= 999)
                {
                    bt.send("M0"+objetivo,true);
                }

                else if(objetivo >= 10 && objetivo <= 99)
                {
                    bt.send("M00"+objetivo,true);
                }

                else if(objetivo >= 0 && objetivo <= 9)
                {
                    bt.send("M000"+objetivo,true);
                }
            }
        }

        if(accion.equals("cerebro"))
        {
            titulo.setText("Disminuyendo niveles de glucosa: "+objetivo);
            // CAMBIAR GLUCOSA
            if(conectado)
            {
                if( objetivo >= 100 && objetivo <= 999)
                {
                    bt.send("C0"+objetivo,true);
                }

                else if(objetivo >= 10 && objetivo <= 99)
                {
                    bt.send("C00"+objetivo,true);
                }

                else if(objetivo >= 0 && objetivo <= 9)
                {
                    bt.send("C000"+objetivo,true);
                }
            }
        }

        if(accion.equals("corazon"))
        {
            titulo.setText("Disminuyendo niveles de glucosa: "+objetivo);
            // CAMBIAR GLUCOSA
            if(conectado)
            {
                if( objetivo >= 1000)
                {
                    bt.send("F"+objetivo,true);
                }
                if( objetivo >= 100 && objetivo <= 999)
                {
                    bt.send("F0"+objetivo,true);
                }

                else if(objetivo >= 10 && objetivo <= 99)
                {
                    bt.send("F00"+objetivo,true);
                }

                else if(objetivo >= 0 && objetivo <= 9)
                {
                    bt.send("F000"+objetivo,true);
                }
            }
        }

        if(accion.equals("sim"))
        {
            if(conectado)
            {
                titulo.setText(R.string.titulo_sim+" "+estado);

                if(estado.equals("sano"))
                {
                    bt.send("ES"+objetivo,true);
                }

                else if(estado.equals("diabetico"))
                {
                    bt.send("ED"+objetivo,true);
                }

                else if(estado.equals("hipoglucemico"))
                {
                    bt.send("EH"+objetivo,true);
                }
            }
        }

        if(accion.equals("test"))
        {
            if(conectado)
            {
                bt.send("T"+objetivo,true);
            }
        }

        if(accion.equals("test_leds"))
        {
            if(conectado)
            {
                bt.send("L"+objetivo,true);
            }
        }



        if (!bt.isBluetoothEnabled()) // no habilitado
        {
            // para arrancar el bluetooth sin pedir permiso:
            bt.enable();

            // PARA PEDIR PERMISO PARA REALIZAR LA CONEXION:
            /*
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
            */
        }
        else
        {
            if(!bt.isServiceAvailable())
            {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            }
        }
    }
    protected void onDestroy()
    {
        super.onDestroy();

        if (bt.isBluetoothEnabled()) // no habilitado
        {
            bt.stopService();
            bt.disconnect();
            bt.disable();
        }
    }


    /** -------------------- METODOS DE MENU --------------------------------*/
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_interfaz, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.test)
        {
            vibrar(100);
            Intent i = new Intent(this, Configuracion.class);
            i.putExtra("opcion","test");
            startActivity(i);
            return true;
        }

        if (id == R.id.conectar)
        {
            vibrar(100);
            Intent i = new Intent(this, DeviceList.class);
            startActivity(i);
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.configuracion)
        {
            vibrar(100);
            Intent i = new Intent(this, Configuracion.class);
            i.putExtra("opcion","config");
            startActivity(i);
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.test_leds)
        {
            vibrar(100);
            Intent i = new Intent(this, Configuracion.class);
            i.putExtra("opcion","test_leds");
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /** -------------------- METODOS DE MENU --------------------------------*/

    void vibrar(int ms)
    {
        Vibrator vibrador = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrador.vibrate(ms);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        int action = MotionEventCompat.getActionMasked(event);
        x = event.getX();
        y = event.getY();
        float pcX, pcY;
        switch (action)
        {
            case (MotionEvent.ACTION_DOWN):
                //Log.d(TAG, "La accion ha sido APRIETA x:"+x +" y: "+y);
                return true;
            case (MotionEvent.ACTION_MOVE):
                //Log.d(TAG, "La acción ha sido APRETANDO x:"+x +" y: "+y);
                return true;
            case (MotionEvent.ACTION_UP):
                vibrar(100);
                pcX = x/ancho*100;
                pcY = y/alto*100;
                Log.d(TAG," CLICK x: "+pcX+" y: "+pcY);
                if (  (pcX > 40 && pcX <60)  && (pcY > 3 && pcY < 12)  )
                {
                    //Toast.makeText(Interfaz.this, "Click al cerebro", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, Configuracion.class);
                    i.putExtra("opcion","cerebro");
                    startActivity(i);
                }

                else if (  (pcX > 44 && pcX <59)  && (pcY > 44 && pcY < 51)  )
                {
                    //Toast.makeText(Interfaz.this, "Click al corazón", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, Configuracion.class);
                    i.putExtra("opcion","corazon");
                    startActivity(i);
                }

                else if ( ( (pcX > 32 && pcX <44) || (pcX > 60 && pcX <70) ) && (pcY > 33 && pcY < 50)  )
                {
                    //Toast.makeText(Interfaz.this, "Click al pulmon", Toast.LENGTH_SHORT).show();
                }

                else if ( ( (pcX > 18 && pcX <32) || (pcX > 70 && pcX <85) ) && (pcY > 43 && pcY < 60)  )
                {
                    //Toast.makeText(Interfaz.this, "Click al musculo", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, Configuracion.class);
                    i.putExtra("opcion","musculos");
                    startActivity(i);
                }

                else if (  (pcX > 35 && pcX <60)  && (pcY > 55 && pcY < 65)  )
                {
                    //Toast.makeText(Interfaz.this, "Click al higado", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, Configuracion.class);
                    i.putExtra("opcion","higado");
                    startActivity(i);
                }

                else if ( ( (pcX > 31 && pcX <36) || (pcX > 65 && pcX <70) ) && (pcY > 70 && pcY < 78)  )
                {
                    //Toast.makeText(Interfaz.this, "Click al riñones", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, Configuracion.class);
                    i.putExtra("opcion","riñones");
                    startActivity(i);
                }

                else if (  (pcX > 45 && pcX <60)  && (pcY > 65 && pcY < 73)  )
                {
                    //Toast.makeText(Interfaz.this, "Click al pancreas", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, Configuracion.class);
                    i.putExtra("opcion","pancreas");
                    startActivity(i);
                }

                else if (  (pcX > 35 && pcX <65)  && (pcY > 80 && pcY < 90)  )
                {
                    //Toast.makeText(Interfaz.this, "Click al intestino", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, Configuracion.class);
                    i.putExtra("opcion","intestino");
                    startActivity(i);
                }



                return true;
            case (MotionEvent.ACTION_CANCEL):
                Log.d(TAG, "La accion ha sido CANCELAR x:"+x +" y: "+y);
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                Log.d(TAG,
                        "La accion ha sido fuera del elemento de la pantalla x:"+x +" y: "+y);
                return true;
            default:
                return true;
        }
    }
}
