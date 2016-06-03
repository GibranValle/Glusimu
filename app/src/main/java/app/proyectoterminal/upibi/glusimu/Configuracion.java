package app.proyectoterminal.upibi.glusimu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ESTA ACTIVITY TIENE APARIENCIA DE DIALOGO (CONFIGURADA EN EL MANIFEST)
 * SI EXISTEN DISPOSITIVOS VINCULADOS, LOS ARREGLA AL PRIMER ARREGO
 * SE ENLISTAN MAS DEVICES AL HACE UN DISCOVERY CUANDO SE ELIGE UN DEVICE
 * SE REGRESA LA DIRECCION MAC A LA PARENT ACTIVITY EN EL INTENT RESULT
 */
public class Configuracion extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    Button aceptar, cancelar;
    SeekBar slider;
    TextView texzto_slider, titulo;
    SharedPreferences respaldo;
    SharedPreferences.Editor editor;
    String TAG = "Interfaz";
    LinearLayout corazon, status, ingreso, consumo, eliminacion, test;
    CheckBox EVCD, EVCI, EVMD, EVMI, EVH, EVP, EVI;

    int gasto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // cargar la ventana
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.config_layout);

        Intent i= getIntent();
        Bundle b = i.getExtras();
        String opcion = b.getString("opcion");
        //Toast.makeText(Configuracion.this, "desde: "+opcion, Toast.LENGTH_SHORT).show();
        respaldo = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
        gasto = respaldo.getInt("gasto",100);

        aceptar = (Button) findViewById(R.id.b_aceptar);
        cancelar = (Button) findViewById(R.id.b_cancelar);
        slider = (SeekBar) findViewById(R.id.slider);
        texzto_slider = (TextView) findViewById(R.id.texto_slider);
        titulo = (TextView) findViewById(R.id.tituloFragment);
        corazon = (LinearLayout) findViewById(R.id.corazon);
        status = (LinearLayout) findViewById(R.id.status);
        ingreso = (LinearLayout) findViewById(R.id.ingreso);
        consumo = (LinearLayout) findViewById(R.id.consumo);
        eliminacion = (LinearLayout) findViewById(R.id.eliminacion);
        test = (LinearLayout) findViewById(R.id.test);
        EVCD = (CheckBox) findViewById(R.id.EVC1);
        EVCI = (CheckBox) findViewById(R.id.EVC2);
        EVMD = (CheckBox) findViewById(R.id.EVMD);
        EVMI = (CheckBox) findViewById(R.id.EVMI);
        EVH =  (CheckBox) findViewById(R.id.EVH);
        EVI = (CheckBox) findViewById(R.id.EVI);
        EVP = (CheckBox) findViewById(R.id.EVP);

        if(opcion.equals("corazon"))
        {
            corazon.setVisibility(View.VISIBLE);
            status.setVisibility(View.GONE);
            ingreso.setVisibility(View.GONE);
            consumo.setVisibility(View.GONE);
            eliminacion.setVisibility(View.GONE);
            test.setVisibility(View.GONE);
            // CARGAR LA CONFIG DE CORAZON
            slider.setProgress(gasto/10);
            texzto_slider.setText("Gasto cardiaco: "+gasto+" mL/min");
            Log.d(TAG,"gasto: "+gasto);
        }

        if(opcion.equals("cerebro") || opcion.equals("musculos"))
        {
            corazon.setVisibility(View.GONE);
            status.setVisibility(View.GONE);
            ingreso.setVisibility(View.GONE);
            consumo.setVisibility(View.VISIBLE);
            eliminacion.setVisibility(View.GONE);
            test.setVisibility(View.GONE);
            // CARGAR LA CONFIG DE CORAZON
            Log.d(TAG,"consumo: "+gasto);
        }

        if(opcion.equals("intestino") || opcion.equals("higado"))
        {
            corazon.setVisibility(View.GONE);
            status.setVisibility(View.GONE);
            ingreso.setVisibility(View.VISIBLE);
            consumo.setVisibility(View.GONE);
            eliminacion.setVisibility(View.GONE);
            test.setVisibility(View.GONE);
            // CARGAR LA CONFIG DE CORAZON
            Log.d(TAG,"ingreso: "+gasto);
        }

        if(opcion.equals("riñones"))
        {
            corazon.setVisibility(View.GONE);
            status.setVisibility(View.GONE);
            ingreso.setVisibility(View.GONE);
            consumo.setVisibility(View.GONE);
            eliminacion.setVisibility(View.VISIBLE);
            test.setVisibility(View.GONE);
            // CARGAR LA CONFIG DE CORAZON
            Log.d(TAG,"eliminacion: "+gasto);
        }

        if(opcion.equals("pancreas"))
        {
            corazon.setVisibility(View.GONE);
            status.setVisibility(View.VISIBLE);
            ingreso.setVisibility(View.GONE);
            consumo.setVisibility(View.GONE);
            eliminacion.setVisibility(View.GONE);
            test.setVisibility(View.GONE);
            // CARGAR LA CONFIG DE CORAZON
            Log.d(TAG,"eliminacion: "+gasto);
        }

        if(opcion.equals("test"))
        {
            corazon.setVisibility(View.GONE);
            status.setVisibility(View.GONE);
            ingreso.setVisibility(View.GONE);
            consumo.setVisibility(View.GONE);
            eliminacion.setVisibility(View.GONE);
            test.setVisibility(View.VISIBLE);
            // CARGAR LA CONFIG DE CORAZON
            Log.d(TAG,"eliminacion: "+gasto);
            titulo.setText("Apertura manual de electroválvulas");
        }

        aceptar.setOnClickListener(this);
        cancelar.setOnClickListener(this);
        slider.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        vibrar(100);

        if(id == R.id.b_aceptar)
        {
            editor = respaldo.edit();
            editor.putInt("gasto", gasto);
            if(editor.commit())
            {
                finish();
                Toast.makeText(Configuracion.this, "Modificado correctamente", Toast.LENGTH_SHORT).show();
            }
        }

        else if (id == R.id.b_cancelar)
        {
            finish();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        gasto = progress*10;
        texzto_slider.setText("Gasto cardiaco: "+gasto+" mL/min");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {

    }

    void vibrar(int ms)
    {
        Vibrator vibrador = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrador.vibrate(ms);
    }

}

