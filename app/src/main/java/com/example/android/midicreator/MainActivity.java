package com.example.android.midicreator;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


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

    Spinner ritmos_spinner;
    String[] ritmos_list;
    ArrayAdapter<String> spinnerRitmosAdapter;
    Button play_button, stop_button, tempo_minnus_button, tempo_plus_button, repeat_minnus_button, repeat_plus_button;
    TextView tempo_text, repeat_text, crono_text;

    private int mInterval;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getSharedPreferences("Ritmos", this.MODE_PRIVATE);
        ritmoOptionSharedPref = getSharedPreferences("RitmosOptions", this.MODE_PRIVATE);

        ritmos = new Ritmos(sharedPref, ritmoOptionSharedPref);

        file = new File(this.getFilesDir(),"OutputMidi.mid");

        ///////////
        // Opciones
        ///////////

        optionsSharedPref = getSharedPreferences("Options", this.MODE_PRIVATE);
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
            }
        });

        stop_button = (Button) findViewById(R.id.stop_button);
        stop_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mediaPlayer != null){
                    mediaPlayer.stop();}
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

        //////////
        // Spinner
        //////////

        ritmos_spinner = (Spinner) findViewById(R.id.ritmos_spinner);
        ritmos_list = ritmos.getRitmos().keySet().toArray(new String[ritmos.getRitmos().keySet().size()]);
        spinnerRitmosAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, ritmos_list);
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
                return;
            }

        });

        mHandler = new Handler();
    }

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

            int tempoPos = (currentTime % (2*musicLength*deltaTempo))/(4*deltaTempo) + 1;
            //int tempoPos = (currentTime / (2*musicLength*deltaTempo)) + 1;
            crono_text.setText("" + tempoPos+"/" + musicLengthHalf);
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
            Rhythm2Event.WriteMidi(file, music, tempo, repeat);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer = MediaPlayer.create(this, Uri.fromFile(file));
        mediaPlayer.setVolume(1,1);

        mediaPlayer.start();

        mInterval = 125/tempo;
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
                    Rhythm2Event.WriteMidi(file, music, tempo, repeat);
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


}
