package com.example.android.midicreator;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class CreateActivity extends AppCompatActivity {

    private Ritmos ritmos;
    private SharedPreferences ritmosSharedPreferences, ritmosOptionsSharedPreferences;
    private MediaPlayer mediaPlayer;
    private File file;

    Button probe_button, create_button;
    EditText name_text, ritmo_text;

    Toast toast;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CreateActivity.this, MainActivity.class);
        startActivity(intent);
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

        switch (item.getItemId()) {
            case R.id.options_options:
                intent = new Intent(CreateActivity.this, OptionsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            case R.id.options_help:
                intent = new Intent(CreateActivity.this, HelpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;

            case R.id.options_about:
                return true;

            case android.R.id.home:
                intent = new Intent(CreateActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        /////////////
        // Action Bar
        /////////////

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle("Crear Ritmo");

        ////////////////
        // Declaraciones
        ////////////////

        ritmosSharedPreferences = getSharedPreferences("Ritmos", MODE_PRIVATE);
        ritmosOptionsSharedPreferences = getSharedPreferences("RitmosOptions", MODE_PRIVATE);

        ritmos = new Ritmos(ritmosSharedPreferences, ritmosOptionsSharedPreferences);
        file = new File(this.getFilesDir(),"OutputMidi.mid");

        ////////////////////
        // Entradas de Texto
        ////////////////////

        name_text = (EditText) findViewById(R.id.name_input);
        ritmo_text = (EditText) findViewById(R.id.ritmo_input);

        //////////
        // Botones
        //////////

        probe_button = (Button) findViewById(R.id.probe_button);
        create_button = (Button) findViewById(R.id.create_button);

        probe_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String input_ritmo = ritmo_text.getText().toString();

                if(ritmos.validateRitmo(input_ritmo)){
                    probeRitmo(ritmos.cleanRitmo(input_ritmo));
                    toastMessage("Reproduciendo ...");
                }
                else
                    toastMessage("Ritmo mal ingresado. Consulta la ayuda");
            }
        });

        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String input_name = name_text.getText().toString();
                String input_ritmo = ritmo_text.getText().toString();

                boolean isCreate = createRitmo(input_name, input_ritmo);

                if(isCreate)
                    toastMessage("Ritmo Guardado con Éxito");
                else
                    toastMessage("Nombre o Ritmo mal ingresado. Consulta la ayuda");

            }
        });

    }

    ////////////
    // Funciones
    ////////////

    public void probeRitmo(String ritmo){

        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying())
                mediaPlayer.stop();
        }

        try {
            Rhythm2Event.WriteMidi(file, ritmo, 100, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer = MediaPlayer.create(this, Uri.fromFile(file));
        mediaPlayer.setVolume(1,1);

        mediaPlayer.start();
    }

    public boolean createRitmo(String name, String ritmo){
        return ritmos.createRitmo(name,ritmo);
    }

    public void toastMessage(String message){

        if(toast != null)
            toast.cancel();

        toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }
}
