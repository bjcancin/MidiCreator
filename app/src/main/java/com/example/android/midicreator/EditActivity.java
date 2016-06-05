package com.example.android.midicreator;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    private Ritmos ritmos;
    private SharedPreferences ritmosSharedPreferences, ritmosOptionsSharedPreferences;
    private MediaPlayer mediaPlayer;
    private File file;

    Spinner ritmos_spinner;
    List<String> ritmos_list;
    ArrayAdapter<String> spinnerRitmosAdapter;

    Button probe_button, save_button, delete_button;
    TextView ritmo_input;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditActivity.this, MainActivity.class);
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
                intent = new Intent(EditActivity.this, OptionsActivity.class);
                startActivity(intent);
                return true;
            case R.id.options_help:
                intent = new Intent(EditActivity.this, HelpActivity.class);
                startActivity(intent);
                return true;

            case R.id.options_about:

                return true;

            case android.R.id.home:
                intent = new Intent(EditActivity.this, MainActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        /////////////
        // Action Bar
        /////////////

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ////////////////
        // Declaraciones
        ////////////////

        ritmosSharedPreferences = getSharedPreferences("Ritmos", MODE_PRIVATE);
        ritmosOptionsSharedPreferences = getSharedPreferences("RitmosOptions", MODE_PRIVATE);

        ritmos = new Ritmos(ritmosSharedPreferences, ritmosOptionsSharedPreferences);
        file = new File(this.getFilesDir(),"OutputMidi.mid");

        ////////
        // Texto
        ////////

        ritmo_input = (TextView) findViewById(R.id.ritmo_input);

        //////////
        // Botones
        //////////

        probe_button = (Button) findViewById(R.id.probe_button);
        save_button = (Button) findViewById(R.id.save_button);
        delete_button = (Button) findViewById(R.id.delete_button);

        probe_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                String input_ritmo = ritmo_input.getText().toString();
                if(ritmos.validateRitmo(input_ritmo)){
                    probeRitmo(ritmos.cleanRitmo(input_ritmo));
                }


            }
        });

        save_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                boolean save = ritmos.editRitmo(ritmos_spinner.getSelectedItem().toString(),ritmo_input.getText().toString());

            }
        });

        delete_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                int pos = ritmos_spinner.getSelectedItemPosition();
                ritmos.deleteRitmo(ritmos_spinner.getSelectedItem().toString());
                ritmos_list.remove(pos);
                spinnerRitmosAdapter.notifyDataSetChanged();
            }
        });

        //////////
        // Spinner
        //////////

        ritmos_spinner = (Spinner) findViewById(R.id.ritmos_spinner);
        ritmos_list = new ArrayList(Arrays.asList(ritmos.getRitmos().keySet().toArray(new String[ritmos.getRitmos().keySet().size()])));
        spinnerRitmosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ritmos_list);
        spinnerRitmosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ritmos_spinner.setAdapter(spinnerRitmosAdapter);

        ritmos_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ritmo_input.setText(ritmos.getRitmo(ritmos_spinner.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
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
}
