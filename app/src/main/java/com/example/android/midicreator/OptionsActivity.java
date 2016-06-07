package com.example.android.midicreator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptionsActivity extends AppCompatActivity {

    private SharedPreferences optionsSharedPref;
    private Opciones options;

    Button save_button, restore_button;
    List<String> optionList;
    ListView options_listview;
    ArrayAdapter<String> optionsAdapter;

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
        setContentView(R.layout.activity_options);

        /////////////
        // Action Bar
        /////////////

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ////////////////
        // Declaraciones
        ////////////////

        optionsSharedPref = getSharedPreferences("Options", MODE_PRIVATE);
        options = new Opciones(optionsSharedPref);
        optionList = new ArrayList(Arrays.asList(options.getOptions().keySet().toArray(new String[options.getOptions().keySet().size()])));



        ///////////
        // ListView
        ///////////

        options_listview = (ListView) findViewById(R.id.options_listView);

        //options_listview.setChoiceMode(options_listview.CHOICE_MODE_MULTIPLE);
        options_listview.setTextFilterEnabled(true);

        optionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, optionList);
        options_listview.setAdapter(optionsAdapter);

        for(String optionString : optionList){
            options_listview.setItemChecked(optionList.indexOf(optionString),options.getOption(optionString));
        }

        //////////
        // Botones
        //////////

        save_button = (Button) findViewById(R.id.save_button);
        restore_button = (Button) findViewById(R.id.restore_button);

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SparseBooleanArray checked = options_listview.getCheckedItemPositions();

                for (int i = 0; i < checked.size(); i++) {
                    options.setOption(optionList.get(i),checked.valueAt(i));
                }
            }
        });

        restore_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(String optionString : optionList){
                    options_listview.setItemChecked(optionList.indexOf(optionString),options.getOption(optionString));
                }
            }
        });

    }
}
