package com.example.android.midicreator;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class OptionsActivity extends AppCompatActivity {

    private SharedPreferences optionsSharedPref;
    private Opciones options;

    Button save_button, restore_button;
    List<String> optionList;
    ListView options_listview;
    ArrayAdapter<String> optionsAdapter;

    Toast toast;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OptionsActivity.this, MainActivity.class);
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
                intent = new Intent(OptionsActivity.this, OptionsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            case R.id.options_help:
                intent = new Intent(OptionsActivity.this, HelpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;

            case R.id.options_about:
                AboutFragment dialog = new AboutFragment();
                FragmentManager fm = getFragmentManager();
                dialog.show(fm, "Hola");
                return true;

            case android.R.id.home:
                intent = new Intent(OptionsActivity.this, MainActivity.class);
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
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_options);

        /////////////
        // Action Bar
        /////////////

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle("Opciones");

        ////////////
        // Fragments
        ////////////

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new OptionsFragment())
                .commit();
    }
}
