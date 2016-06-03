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
import android.widget.ImageView;
import android.widget.Toast;

import app.proyectoterminal.upibi.glusimu.Bluetooth.BluetoothSPP;
import app.proyectoterminal.upibi.glusimu.Bluetooth.BluetoothState;

public class Interfaz extends AppCompatActivity implements View.OnTouchListener {

    String TAG = "Interfaz";

    // VARIABLES PARA GRAFICAR
    ImageView espacio;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;
    int alto, ancho;
    float x, y;

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
                if(message.startsWith("V"))
                {
                    Log.d(TAG,"mensaje recibido: "+message+" largo: "+message.length());
                    dato = message.substring(1,message.length());
                    valor = Integer.parseInt(dato);
                    Log.d(TAG,"conversion correcta a entero: "+valor);
                }


            }
        });


        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener()
        {
            public void onDeviceDisconnected()
            {
                // CAMBIAR TEXTO DE CONSOLA CONEXION

            }

            public void onDeviceConnectionFailed()
            {
                // CAMBIAR TEXTO DE CONSOLA CONEXION
            }

            public void onDeviceConnected(String name, String address)
            {

            }
        });

        bt.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener()
        {
            public void onServiceStateChanged(int state)
            {
                if(state == BluetoothState.STATE_CONNECTED)
                {

                }
                else if(state == BluetoothState.STATE_CONNECTING)
                {

                }
                else if(state == BluetoothState.STATE_LISTEN)
                {
                    // Do something when device is waiting for connection
                }
                else if(state == BluetoothState.STATE_NONE)
                {
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

                else if ( ( (pcX > 10 && pcX <20) || (pcX > 80 && pcX <90) ) && (pcY > 45 && pcY < 60)  )
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
