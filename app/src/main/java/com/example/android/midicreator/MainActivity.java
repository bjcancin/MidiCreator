package com.example.android.midicreator;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private File file;
    private MediaPlayer mediaPlayer;
    private SharedPreferences sharedPref, ritmoOptionSharedPref, optionsSharedPref;
    private Opciones options;
    private Ritmos ritmos;
    private String music;
    private int tempo, repeat;
    private int tempoPosMax;

    Spinner ritmos_spinner;
    String[] ritmos_list;
    ArrayAdapter<String> spinnerRitmosAdapter;
    Button play_button, stop_button, tempo_minnus_button, tempo_plus_button, repeat_minnus_button, repeat_plus_button, create_button;
    TextView tempo_text, repeat_text, crono_text;
    ImageView ball_imageView;
    Bitmap ball;
    Canvas ball_canvas;

    Toast toast;

    private int mInterval;
    private Handler mHandler;

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("¿Quieres Salir?")
                .setMessage("¿Seguro que quieres salir?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                }).create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        if(mediaPlayer != null){
            mediaPlayer.stop();
        }



        switch (item.getItemId()) {
            case R.id.options_options:
                intent = new Intent(MainActivity.this, OptionsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            case R.id.options_help:
                intent = new Intent(MainActivity.this, HelpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;

            case R.id.options_about:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ////////////////
        // Declaraciones
        ////////////////

        setTitle("Soleá MF");

        sharedPref = getSharedPreferences("Ritmos", MODE_PRIVATE);
        ritmoOptionSharedPref = getSharedPreferences("RitmosOptions", MODE_PRIVATE);

        ritmos = new Ritmos(sharedPref, ritmoOptionSharedPref);

        file = new File(this.getFilesDir(),"OutputMidi.mid");

        ball = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        ball_canvas = new Canvas(ball);

        ///////////
        // Opciones
        ///////////

        optionsSharedPref = getSharedPreferences("Options", MODE_PRIVATE);
        options = new Opciones(optionsSharedPref);

        //////////
        // Botones
        //////////

        tempo_text = (TextView) findViewById(R.id.tempo_text);
        repeat_text = (TextView) findViewById(R.id.repeat_text);
        crono_text = (TextView) findViewById(R.id.crono_text);

        play_button = (Button) findViewById(R.id.play_button);
        play_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                play();
                toastMessage("Reproduciendo ...");
            }
        });

        stop_button = (Button) findViewById(R.id.stop_button);
        stop_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mediaPlayer != null){
                    if(mediaPlayer.isPlaying())
                        toastMessage("Stop");
                    mediaPlayer.stop();
                }
            }
        });

        tempo_minnus_button = (Button) findViewById(R.id.tempo_minnus_button);
        tempo_minnus_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                int tempo = Integer.parseInt((String) tempo_text.getText());
                tempo-=5;
                tempo = Math.max(5,tempo);
                ritmos.setTempo(ritmos_spinner.getSelectedItem().toString(),tempo);
                tempo_text.setText(""+tempo);
                changeTempo();
            }
        });

        tempo_plus_button = (Button) findViewById(R.id.tempo_plus_button);
        tempo_plus_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                int tempo = Integer.parseInt((String) tempo_text.getText());
                tempo+=5;
                ritmos.setTempo(ritmos_spinner.getSelectedItem().toString(),tempo);
                tempo_text.setText(""+tempo);
                changeTempo();
            }
        });

        repeat_minnus_button = (Button) findViewById(R.id.repeat_minnus_button);
        repeat_minnus_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                int repeat = Integer.parseInt((String) repeat_text.getText());
                repeat-=1;
                repeat = Math.max(1,repeat);
                ritmos.setRepeat(ritmos_spinner.getSelectedItem().toString(),repeat);
                repeat_text.setText(""+repeat);
            }
        });

        repeat_plus_button = (Button) findViewById(R.id.repeat_plus_button);
        repeat_plus_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                int repeat = Integer.parseInt((String) repeat_text.getText());
                repeat+=1;
                ritmos.setRepeat(ritmos_spinner.getSelectedItem().toString(),repeat);
                repeat_text.setText(""+repeat);
            }
        });

        create_button = (Button) findViewById(R.id.create_button);
        create_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                showCreateEditPopupMenu(v);
            }
        });

        //////////
        // Spinner
        //////////

        ritmos_spinner = (Spinner) findViewById(R.id.ritmos_spinner);
        ritmos_list = ritmos.getRitmos().keySet().toArray(new String[ritmos.getRitmos().keySet().size()]);
        spinnerRitmosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ritmos_list);
        spinnerRitmosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ritmos_spinner.setAdapter(spinnerRitmosAdapter);

        ritmos_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if(!ritmos.ini()) {
                    tempo_text.setText("" + ritmos.getTempo(ritmos_spinner.getSelectedItem().toString()));
                    repeat_text.setText("" + ritmos.getRepeat(ritmos_spinner.getSelectedItem().toString()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        ///////////
        // Contador
        ///////////

        mHandler = new Handler();
    }

    //////////////////
    // Otras Funciones
    //////////////////

    private void showCreateEditPopupMenu(View v){

        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.create_edit_popup_menu,popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(mediaPlayer != null){
                    mediaPlayer.stop();
                }

                Intent intent;

                switch(item.getItemId()){
                    case R.id.menu_create:
                        intent = new Intent(MainActivity.this, CreateActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        return true;

                    case R.id.menu_edit:
                        intent = new Intent(MainActivity.this, EditActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        return true;

                    default:
                        return false;
                }
            }
        });

        popup.show();

    };

    void startmCount() {
        mCount.run();
    }

    Runnable mCount = new Runnable() {
        @Override
        public void run() {
            try {
                // Código
                mCountUpdate();
            } finally {
                if(mediaPlayer.isPlaying())
                    mHandler.postDelayed(mCount, mInterval);
            }
        }
    };

    public void mCountUpdate(){
        if(mediaPlayer.isPlaying())
        {
            int currentTime = mediaPlayer.getCurrentPosition();
            int musicLength = music.length();
            int musicLengthHalf = musicLength/2;
            int deltaTempo = 125*120/tempo;

            if(options.getOption("Tiempo de Palmas")){
                int tempoPos = (currentTime % (2 * musicLength * deltaTempo)) / (2 * deltaTempo) + 1;
                tempoPos = Rhythm2Event.mapPalmas(music).get(tempoPos);

                if(tempoPos == 0)
                    tempoPos = Rhythm2Event.mapPalmas(music).get(music.length());

                crono_text.setText("" + tempoPos + "/" + Rhythm2Event.mapPalmas(music).get(music.length()));
            }

            else
            {

                int tempoPos = (currentTime % (2 * musicLength * deltaTempo)) / (4 * deltaTempo) + 1;
                crono_text.setText("" + tempoPos + "/" + musicLengthHalf);
            }
        }

    }


    public void play(){

        music = ritmos.getRitmo(ritmos_spinner.getSelectedItem().toString());
        tempo = Integer.parseInt((String) tempo_text.getText());
        repeat = Integer.parseInt((String) repeat_text.getText());

        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying())
                mediaPlayer.stop();
        }


        try {
            Rhythm2Event.WriteMidi(file, music, tempo, repeat, options.getOptions());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer = MediaPlayer.create(this, Uri.fromFile(file));
        mediaPlayer.setVolume(1,1);

        mediaPlayer.start();

        mInterval = 125/tempo;
        tempoPosMax = 0;
        startmCount();
    }

    public void changeTempo(){

        if(mediaPlayer != null)
        {
            if(mediaPlayer.isPlaying())
            {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int currentDuration = mediaPlayer.getDuration();
                float currentPercent = (float) currentPosition/ (float) currentDuration;

                mediaPlayer.stop();

                tempo = Integer.parseInt((String) tempo_text.getText());

                try {
                    Rhythm2Event.WriteMidi(file, music, tempo, repeat, options.getOptions());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer = MediaPlayer.create(this, Uri.fromFile(file));
                mediaPlayer.setVolume(1,1);

                mediaPlayer.start();

                float seekTo = (float)mediaPlayer.getDuration()*currentPercent;

                mediaPlayer.seekTo((int) seekTo);

                mInterval = 125/tempo;
                startmCount();

            }
        }
    }

    public void toastMessage(String message){

        if(toast != null)
            toast.cancel();

        toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }
}
