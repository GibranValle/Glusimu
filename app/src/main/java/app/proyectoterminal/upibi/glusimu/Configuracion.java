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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ESTA ACTIVITY TIENE APARIENCIA DE DIALOGO (CONFIGURADA EN EL MANIFEST)
 * SI EXISTEN DISPOSITIVOS VINCULADOS, LOS ARREGLA AL PRIMER ARREGO
 * SE ENLISTAN MAS DEVICES AL HACE UN DISCOVERY CUANDO SE ELIGE UN DEVICE
 * SE REGRESA LA DIRECCION MAC A LA PARENT ACTIVITY EN EL INTENT RESULT
 */
public class Configuracion extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    Button aceptar, cancelar;
    SeekBar slider;
    TextView texzto_slider, titulo;
    SharedPreferences respaldo;
    SharedPreferences.Editor editor;
    String TAG = "Interfaz";
    LinearLayout corazon, pancreas, higado, intestino, config, test, musculos, cerebro, leds;
    CheckBox EVT, EVCD, EVCI, EVMD, EVMI, EVH, EVP, EVI, EVR;
    Spinner spinner;
    ArrayAdapter<String> adapter;
    CheckBox LEDT, LEDR, LEDV, LEDA, LEDC, LEDM, LEDH, LEDI, LEDP;
    EditText minOut, maxOut;

    String estados[] = {"Sano", "Diabético", "Hipoglucemico"};
    String accion = "nula";
    int objetivo = 0;
    String estado = "nula";
    int gasto;
    String opcion;

    int max, min, glucemia;

    int objetivoCerebro, objetivoMusculos, objetivoIntestino, objetivoHigado;
    int valvulasChecked, ledsChecked;

    boolean V1,V2,V3,V4,V5,V6,V7,V8;
    boolean CR,CA,CV,LC,LM,LI,LH,LP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // cargar la ventana
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.config_layout);

        Intent i= getIntent();
        Bundle b = i.getExtras();
        opcion = b.getString("opcion");
        //Toast.makeText(Configuracion.this, "desde: "+opcion, Toast.LENGTH_SHORT).show();
        respaldo = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
        gasto = respaldo.getInt("gasto",100);
        accion = respaldo.getString("accion","nula");
        objetivoCerebro = respaldo.getInt("objetivoCerebro",100);
        objetivoMusculos = respaldo.getInt("objetivoMusculos",100);
        objetivoIntestino = respaldo.getInt("objetivoIntestino",120);
        objetivoHigado = respaldo.getInt("objetivoHigado",120);
        max = respaldo.getInt("max",200);
        max = respaldo.getInt("min",50);
        estado = respaldo.getString("estado", "nula");
        glucemia = respaldo.getInt("glucemia",100);
        valvulasChecked = respaldo.getInt("valvulasChecked",0);
        ledsChecked = respaldo.getInt("ledsChecked",0);

        aceptar = (Button) findViewById(R.id.b_aceptar);
        spinner = (Spinner) findViewById(R.id.spinner);
        cancelar = (Button) findViewById(R.id.b_cancelar);
        slider = (SeekBar) findViewById(R.id.slider);
        texzto_slider = (TextView) findViewById(R.id.texto_slider);
        titulo = (TextView) findViewById(R.id.tituloFragment);
        leds = (LinearLayout) findViewById(R.id.leds);
        config = (LinearLayout) findViewById(R.id.config);
        cerebro = (LinearLayout) findViewById(R.id.cerebro);
        corazon = (LinearLayout) findViewById(R.id.corazon);
        musculos = (LinearLayout) findViewById(R.id.musculo);
        pancreas = (LinearLayout) findViewById(R.id.pancreas);
        higado = (LinearLayout) findViewById(R.id.higado);
        intestino = (LinearLayout) findViewById(R.id.intestino);
        test = (LinearLayout) findViewById(R.id.test);
        minOut = (EditText) findViewById(R.id.min);
        maxOut = (EditText) findViewById(R.id.max);

        EVT = (CheckBox) findViewById(R.id.EVT);
        EVCD = (CheckBox) findViewById(R.id.EVCD);
        EVCI = (CheckBox) findViewById(R.id.EVCI);
        EVMD = (CheckBox) findViewById(R.id.EVMD);
        EVMI = (CheckBox) findViewById(R.id.EVMI);
        EVH =  (CheckBox) findViewById(R.id.EVH);
        EVI = (CheckBox) findViewById(R.id.EVI);
        EVP = (CheckBox) findViewById(R.id.EVP);
        EVR = (CheckBox) findViewById(R.id.EVR);

        LEDT = (CheckBox) findViewById(R.id.LEDT);
        LEDR = (CheckBox) findViewById(R.id.LEDR);
        LEDV = (CheckBox) findViewById(R.id.LEDV);
        LEDA = (CheckBox) findViewById(R.id.LEDA);
        LEDC = (CheckBox) findViewById(R.id.LEDC);
        LEDH = (CheckBox) findViewById(R.id.LEDH);
        LEDI = (CheckBox) findViewById(R.id.LEDI);
        LEDM =  (CheckBox) findViewById(R.id.LEDM);
        LEDP = (CheckBox) findViewById(R.id.LEDP);

        adapter = new ArrayAdapter<String>(this,R.layout.custom_spinner_layout,estados);
        adapter.setDropDownViewResource(R.layout.custom_drop_spinner_layout);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        EVT.setOnCheckedChangeListener(this);
        EVCD.setOnCheckedChangeListener(this);
        EVCI.setOnCheckedChangeListener(this);
        EVMD.setOnCheckedChangeListener(this);
        EVMI.setOnCheckedChangeListener(this);
        EVH.setOnCheckedChangeListener(this);
        EVI.setOnCheckedChangeListener(this);
        EVP.setOnCheckedChangeListener(this);

        LEDT.setOnCheckedChangeListener(this);
        LEDR.setOnCheckedChangeListener(this);
        LEDV.setOnCheckedChangeListener(this);
        LEDA.setOnCheckedChangeListener(this);
        LEDM.setOnCheckedChangeListener(this);
        LEDP.setOnCheckedChangeListener(this);
        LEDC.setOnCheckedChangeListener(this);
        LEDI.setOnCheckedChangeListener(this);
        LEDH.setOnCheckedChangeListener(this);


        if(opcion.equals("corazon"))
        {
            leds.setVisibility(View.GONE);
            config.setVisibility(View.GONE);
            corazon.setVisibility(View.VISIBLE);
            pancreas.setVisibility(View.GONE);
            intestino.setVisibility(View.GONE);
            higado.setVisibility(View.GONE);
            test.setVisibility(View.GONE);
            musculos.setVisibility(View.GONE);
            cerebro.setVisibility(View.GONE);
            // CARGAR LA CONFIG DE CORAZON
            slider.setProgress(gasto/10);
            texzto_slider.setText("Gasto cardiaco: "+gasto+" mL/min");
            Log.d(TAG,"gasto: "+gasto);
            titulo.setText(R.string.titulo_corazon);
        }

        if(opcion.equals("cerebro") || opcion.equals("musculos"))
        {
            leds.setVisibility(View.GONE);
            config.setVisibility(View.GONE);
            corazon.setVisibility(View.GONE);
            pancreas.setVisibility(View.GONE);
            intestino.setVisibility(View.GONE);
            higado.setVisibility(View.GONE);
            test.setVisibility(View.GONE);
            musculos.setVisibility(View.GONE);
            cerebro.setVisibility(View.VISIBLE);
            // CARGAR LA CONFIG DE CORAZON
            Log.d(TAG,"consumo: "+gasto);
            titulo.setText(R.string.titulo_cerebro);
        }

        if(opcion.equals("intestino") || opcion.equals("higado"))
        {
            leds.setVisibility(View.GONE);
            config.setVisibility(View.GONE);
            corazon.setVisibility(View.GONE);
            pancreas.setVisibility(View.GONE);
            intestino.setVisibility(View.VISIBLE);
            higado.setVisibility(View.GONE);
            test.setVisibility(View.GONE);
            musculos.setVisibility(View.GONE);
            cerebro.setVisibility(View.GONE);
            // CARGAR LA CONFIG DE CORAZON
            Log.d(TAG,"ingreso: "+gasto);
            titulo.setText(R.string.titulo_intestino);
        }

        if(opcion.equals("higado") || opcion.equals("higado"))
        {
            leds.setVisibility(View.GONE);
            config.setVisibility(View.GONE);
            corazon.setVisibility(View.GONE);
            pancreas.setVisibility(View.GONE);
            intestino.setVisibility(View.VISIBLE);
            higado.setVisibility(View.GONE);
            test.setVisibility(View.GONE);
            musculos.setVisibility(View.GONE);
            cerebro.setVisibility(View.GONE);
            // CARGAR LA CONFIG DE CORAZON
            Log.d(TAG,"ingreso: "+gasto);
            titulo.setText(R.string.titulo_higado);
        }

        if(opcion.equals("riñones"))
        {
            // CARGAR LA CONFIG DE CORAZON
            Log.d(TAG,"eliminacion: "+gasto);
        }

        if(opcion.equals("pancreas"))
        {
            leds.setVisibility(View.GONE);
            config.setVisibility(View.GONE);
            corazon.setVisibility(View.GONE);
            pancreas.setVisibility(View.VISIBLE);
            intestino.setVisibility(View.GONE);
            higado.setVisibility(View.GONE);
            test.setVisibility(View.GONE);
            musculos.setVisibility(View.GONE);
            cerebro.setVisibility(View.GONE);
            // CARGAR LA CONFIG DE CORAZON
            Log.d(TAG,"eliminacion: "+gasto);
            titulo.setText(R.string.titulo_pancreas);
        }

        if(opcion.equals("musculos"))
        {
            leds.setVisibility(View.GONE);
            config.setVisibility(View.GONE);
            corazon.setVisibility(View.GONE);
            pancreas.setVisibility(View.GONE);
            intestino.setVisibility(View.GONE);
            higado.setVisibility(View.GONE);
            test.setVisibility(View.GONE);
            musculos.setVisibility(View.VISIBLE);
            cerebro.setVisibility(View.GONE);
            // CARGAR LA CONFIG DE CORAZON
            Log.d(TAG,"eliminacion: "+gasto);
            titulo.setText(R.string.titulo_musculo);
        }

        if(opcion.equals("test"))
        {
            leds.setVisibility(View.GONE);
            config.setVisibility(View.GONE);
            corazon.setVisibility(View.GONE);
            pancreas.setVisibility(View.GONE);
            intestino.setVisibility(View.GONE);
            higado.setVisibility(View.GONE);
            test.setVisibility(View.VISIBLE);
            musculos.setVisibility(View.GONE);
            cerebro.setVisibility(View.GONE);
            // CARGAR LA CONFIG DE CORAZON
            Log.d(TAG,"eliminacion: "+gasto);
            titulo.setText(R.string.titulo_test);

            if(valvulasChecked == 255)
            {
                EVT.setChecked(true);
                valvulasChecked = 0;
            }

            if(valvulasChecked >= 128)
            {
                V8 = true;
                EVR.setChecked(true);
                valvulasChecked -= 128;
            }
            if(valvulasChecked >= 64)
            {
                V7 = true;
                valvulasChecked -= 64;
                EVP.setChecked(true);
            }
            if(valvulasChecked >= 32)
            {
                V6 = true;
                valvulasChecked -= 32;
                EVMI.setChecked(true);
            }

            if(valvulasChecked >= 16)
            {
                V5 = true;
                valvulasChecked -= 16;
                EVMD.setChecked(true);
            }

            if(valvulasChecked >= 8)
            {
                V5 = true;
                valvulasChecked -= 8;
                EVI.setChecked(true);
            }

            if(valvulasChecked >= 4)
            {
                V5 = true;
                valvulasChecked -= 4;
                EVH.setChecked(true);
            }


            if(valvulasChecked >= 2)
            {
                V5 = true;
                valvulasChecked -= 2;
                EVCI.setChecked(true);
            }

            if(valvulasChecked >= 1)
            {
                V5 = true;
                valvulasChecked -= 1;
                EVCD.setChecked(true);
            }

        }

        if(opcion.equals("config"))
        {
            leds.setVisibility(View.GONE);
            config.setVisibility(View.VISIBLE);
            corazon.setVisibility(View.GONE);
            pancreas.setVisibility(View.GONE);
            intestino.setVisibility(View.GONE);
            higado.setVisibility(View.GONE);
            test.setVisibility(View.GONE);
            musculos.setVisibility(View.GONE);
            cerebro.setVisibility(View.GONE);
            // CARGAR LA CONFIG DE CORAZON
            Log.d(TAG,"eliminacion: "+gasto);
            titulo.setText(R.string.titulo_config);
            maxOut.setText(""+max);
            minOut.setText(""+min);



        }

        if(opcion.equals("test_leds"))
        {
            leds.setVisibility(View.VISIBLE);
            config.setVisibility(View.GONE);
            corazon.setVisibility(View.GONE);
            pancreas.setVisibility(View.GONE);
            intestino.setVisibility(View.GONE);
            higado.setVisibility(View.GONE);
            test.setVisibility(View.GONE);
            musculos.setVisibility(View.GONE);
            cerebro.setVisibility(View.GONE);
            // CARGAR LA CONFIG DE CORAZON
            Log.d(TAG,"eliminacion: "+gasto);
            titulo.setText(R.string.titulo_config);

            if(ledsChecked == 255)
            {
                LEDT.setChecked(true);
                ledsChecked = 0;
            }

            if(ledsChecked >= 128)
            {
                LP = true;
                LEDP.setChecked(true);
                ledsChecked -= 128;
            }
            if(ledsChecked >= 64)
            {
                LH = true;
                ledsChecked -= 64;
                LEDH.setChecked(true);
            }
            if(ledsChecked >= 32)
            {
                LI = true;
                ledsChecked -= 32;
                LEDI.setChecked(true);
            }

            if(ledsChecked >= 16)
            {
                LM = true;
                ledsChecked -= 16;
                LEDM.setChecked(true);
            }

            if(ledsChecked >= 8)
            {
                LC = true;
                ledsChecked -= 8;
                LEDC.setChecked(true);
            }

            if(ledsChecked >= 4)
            {
                CA = true;
                ledsChecked -= 4;
                LEDA.setChecked(true);
            }


            if(ledsChecked >= 2)
            {
                CV = true;
                ledsChecked -= 2;
                LEDV.setChecked(true);
            }

            if(ledsChecked >= 1)
            {
                CR = true;
                ledsChecked -= 1;
                LEDR.setChecked(true);
            }
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
            if(opcion.equals("corazon"))
            {
                editor = respaldo.edit();
                editor.putInt("gasto", gasto);
                editor.putInt("objetivo", gasto);
                editor.putString("accion", "gasto");
                if(editor.commit())
                {
                    finish();
                    Toast.makeText(Configuracion.this, "Modificado correctamente", Toast.LENGTH_SHORT).show();
                }
            }

            if(opcion.equals("cerebro"))
            {
                editor = respaldo.edit();
                editor.putInt("objetivo", objetivo);
                editor.putInt("objetivoCerebro", objetivo);
                editor.putString("accion", "cerebro");
                if(editor.commit())
                {
                    finish();
                    Toast.makeText(Configuracion.this, "Modificado correctamente", Toast.LENGTH_SHORT).show();
                }
            }

            if(opcion.equals("musculos"))
            {
                editor = respaldo.edit();
                editor.putInt("objetivo", objetivo);
                editor.putString("accion", "musculos");
                editor.putInt("objetivoMusculos", objetivo);
                if(editor.commit())
                {
                    finish();
                    Toast.makeText(Configuracion.this, "Modificado correctamente", Toast.LENGTH_SHORT).show();
                }
            }

            if(opcion.equals("intestino"))
            {
                editor = respaldo.edit();
                editor.putInt("objetivo", objetivo);
                editor.putString("accion", "intestino");
                editor.putInt("objetivoIntestino", objetivo);

                if(editor.commit())
                {
                    finish();
                    Toast.makeText(Configuracion.this, "Modificado correctamente", Toast.LENGTH_SHORT).show();
                }
            }

            if(opcion.equals("higado"))
            {
                editor = respaldo.edit();
                editor.putInt("objetivo", objetivo);
                editor.putString("accion", "higado");
                editor.putInt("objetivoHigado", objetivo);

                if(editor.commit())
                {
                    finish();
                    Toast.makeText(Configuracion.this, "Modificado correctamente", Toast.LENGTH_SHORT).show();
                }
            }

            if(opcion.equals("riñones"))
            {
                // CARGAR LA CONFIG DE CORAZON
                Log.d(TAG,"eliminacion: "+gasto);
            }

            if(opcion.equals("pancreas"))
            {
                editor = respaldo.edit();
                editor.putInt("objetivo", objetivo);
                editor.putString("accion", "higado");
                editor.putInt("objetivoPancreas", objetivo);

                if(editor.commit())
                {
                    finish();
                    Toast.makeText(Configuracion.this, "Modificado correctamente", Toast.LENGTH_SHORT).show();
                }
            }

            if(opcion.equals("test"))
            {
                valvulasChecked = 0;
                if(V1)
                {
                    valvulasChecked += 1;
                }
                if(V2)
                {
                    valvulasChecked += 2;
                }
                if(V3)
                {
                    valvulasChecked += 4;
                }
                if(V4)
                {
                    valvulasChecked += 8;
                }
                if(V5)
                {
                    valvulasChecked += 16;
                }
                if(V6)
                {
                    valvulasChecked += 32;
                }
                if(V7)
                {
                    valvulasChecked += 64;
                }
                if(V8)
                {
                    valvulasChecked += 128;
                }
                editor = respaldo.edit();
                editor.putString("accion", "test");
                editor.putString("objetivo", "0");
                editor.putInt("valvulasChecked", valvulasChecked);
                editor.putInt("objetivo", valvulasChecked);
                if(editor.commit())
                {
                    finish();
                    Log.d(TAG,"leds: "+ledsChecked + " valvs: "+valvulasChecked);
                    Toast.makeText(Configuracion.this, "Modificado correctamente", Toast.LENGTH_SHORT).show();
                }
            }

            if(opcion.equals("config"))
            {
                editor = respaldo.edit();
                max = Integer.parseInt(maxOut.getText().toString());
                min = Integer.parseInt(minOut.getText().toString());
                if(min > 10 && max < 600)
                {
                    editor.putInt("max", max);
                    editor.putInt("min", min);
                    if(editor.commit())
                    {
                        finish();
                        Toast.makeText(Configuracion.this, "Modificado correctamente", Toast.LENGTH_SHORT).show();
                    }
                }
                else if( min < 0)
                {
                    Toast.makeText(Configuracion.this, "Elegir un valor minimo positivo", Toast.LENGTH_SHORT).show();
                }

                else if( max > 600)
                {
                    Toast.makeText(Configuracion.this, "Elegir un valor maximo menor a 600", Toast.LENGTH_SHORT).show();
                }

            }

            if(opcion.equals("test_leds"))
            {
                ledsChecked = 0;
                if(CR)
                {
                    ledsChecked += 1;
                }
                if(CA)
                {
                    ledsChecked += 2;
                }
                if(CV)
                {
                    ledsChecked += 4;
                }
                if(LC)
                {
                    ledsChecked += 8;
                }
                if(LM)
                {
                    ledsChecked += 16;
                }
                if(LI)
                {
                    ledsChecked += 32;
                }
                if(LH)
                {
                    ledsChecked += 64;
                }
                if(LP)
                {
                    ledsChecked += 128;
                }

                editor = respaldo.edit();
                editor.putString("accion", "test");
                editor.putString("objetivo", "0");
                editor.putInt("ledsChecked", ledsChecked);
                editor.putInt("objetivo", ledsChecked);
                if(editor.commit())
                {
                    finish();
                    Toast.makeText(Configuracion.this, "Modificado correctamente", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"leds: "+ledsChecked + " valvs: "+valvulasChecked);
                }
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
        int m, b;
        if(opcion.equals("corazon"))
        {
            gasto = progress*10;
            texzto_slider.setText("Gasto cardiaco: "+gasto+" mL/min");
        }

        if(opcion.equals("intestino") || opcion.equals("higado") || opcion.equals("riñones"))
        {
            // no pueden bajar más de la actual
            m = (max - min)/100;
            b = min;
            objetivo = m*progress+b;
            if(objetivo < glucemia)
            {
                objetivo = glucemia;
                Toast.makeText(Configuracion.this, "Este organo no puede disminuir la glucemia", Toast.LENGTH_SHORT).show();
            }
        }

        if(opcion.equals("cerebro") || opcion.equals("musculos"))
        {
            // no puede subir más de la actual
            m = (max - min)/100;
            b = min;
            objetivo = m*progress+b;

            if(objetivo > glucemia)
            {
                objetivo = glucemia;
                Toast.makeText(Configuracion.this, "Este organo no puede incrementar la glucemia", Toast.LENGTH_SHORT).show();
            }
        }
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        Log.d(TAG,"checkbox precionado");
        if(isChecked)
        {
            if(buttonView.getId() == R.id.LEDT)
            {
                // ch
                // CR,CA,CV,LC,LM,LI,LH,LP;
                CR = true;
                CV = true;
                CA = true;
                LC = true;
                LM = true;
                LI = true;
                LH = true;
                LP = true;

                LEDR.setChecked(true);
                LEDV.setChecked(true);
                LEDA.setChecked(true);
                LEDC.setChecked(true);
                LEDM.setChecked(true);
                LEDI.setChecked(true);
                LEDH.setChecked(true);
                LEDP.setChecked(true);
            }

            if(buttonView.getId() == R.id.EVT)
            {
                V1 = true;
                V2 = true;
                V3 = true;
                V4 = true;
                V5 = true;
                V6 = true;
                V7 = true;
                V8 = true;
                EVCD.setChecked(true);
                EVCI.setChecked(true);
                EVH.setChecked(true);
                EVI.setChecked(true);
                EVMD.setChecked(true);
                EVMI.setChecked(true);
                EVP.setChecked(true);
                EVR.setChecked(true);

            }

            if(buttonView.getId() == R.id.EVCD)
            {
                V1 = true;
            }
            if(buttonView.getId() == R.id.EVCI)
            {
                V2 = true;
            }
            if(buttonView.getId() == R.id.EVH)
            {
                V3 = true;
            }
            if(buttonView.getId() == R.id.EVI)
            {
                V4 = true;
            }
            if(buttonView.getId() == R.id.EVMD)
            {
                V5 = true;
            }
            if(buttonView.getId() == R.id.EVMI)
            {
                V6 = true;
            }
            if(buttonView.getId() == R.id.EVP)
            {
                V7 = true;

            }
            if(buttonView.getId() == R.id.EVR)
            {
                V8 = true;
            }


            // LEDS
            if(buttonView.getId() == R.id.LEDR)
            {
                CR = true;
            }
            if(buttonView.getId() == R.id.LEDV)
            {
                CV = true;
            }
            if(buttonView.getId() == R.id.LEDA)
            {
                CA = true;
            }
            if(buttonView.getId() == R.id.LEDC)
            {
                LC = true;
            }
            if(buttonView.getId() == R.id.LEDM)
            {
                LM = true;
            }
            if(buttonView.getId() == R.id.LEDI)
            {
                LI = true;
            }
            if(buttonView.getId() == R.id.LEDH)
            {
                LH = true;
            }
            if(buttonView.getId() == R.id.LEDP)
            {
                LP = true;
            }
        }


        if(!isChecked)
        {
            if(buttonView.getId() == R.id.LEDT)
            {
                // CR,CA,CV,LC,LM,LI,LH,LP;
                CR = false;
                CA = false;
                CV = false;
                LC = false;
                LM = false;
                LI = false;
                LH = false;
                LP = false;

                LEDR.setChecked(false);
                LEDV.setChecked(false);
                LEDA.setChecked(false);
                LEDC.setChecked(false);
                LEDM.setChecked(false);
                LEDI.setChecked(false);
                LEDH.setChecked(false);
                LEDP.setChecked(false);
            }

            if(buttonView.getId() == R.id.EVT)
            {
                V1 = false;
                V2 = false;
                V3 = false;
                V4 = false;
                V5 = false;
                V6 = false;
                V7 = false;
                V8 = false;
                EVCD.setChecked(false);
                EVCI.setChecked(false);
                EVMD.setChecked(false);
                EVMI.setChecked(false);
                EVP.setChecked(false);
                EVR.setChecked(false);
                EVP.setChecked(false);
                EVH.setChecked(false);
                EVI.setChecked(false);
            }

            if(buttonView.getId() == R.id.EVCD)
            {
                V1 = false;
            }

            if(buttonView.getId() == R.id.EVCI)
            {
                V2 = false;
            }

            if(buttonView.getId() == R.id.EVMD)
            {
                V3 = false;
            }

            if(buttonView.getId() == R.id.EVMI)
            {
                V4 = false;
            }

            if(buttonView.getId() == R.id.EVH)
            {
                V5 = false;
            }

            if(buttonView.getId() == R.id.EVI)
            {
                V6 = false;
            }

            if(buttonView.getId() == R.id.EVP)
            {
                V7 = false;
            }

            if(buttonView.getId() == R.id.EVR)
            {
                V8 = false;
            }


            // LEDS
            if(buttonView.getId() == R.id.LEDR)
            {
                CR = false;
            }
            if(buttonView.getId() == R.id.LEDV)
            {
                CV = false;
            }
            if(buttonView.getId() == R.id.LEDA)
            {
                CA = false;
            }
            if(buttonView.getId() == R.id.LEDC)
            {
                LC = false;
            }
            if(buttonView.getId() == R.id.LEDM)
            {
                LM = false;
            }
            if(buttonView.getId() == R.id.LEDI)
            {
                LI = false;
            }
            if(buttonView.getId() == R.id.LEDH)
            {
                LH = false;
            }
            if(buttonView.getId() == R.id.LEDP)
            {
                LP = false;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

