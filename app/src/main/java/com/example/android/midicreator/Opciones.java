package com.example.android.midicreator;

import android.content.SharedPreferences;

public class Opciones {

    private SharedPreferences sharedPref;

    public Opciones(SharedPreferences sharedPref){
        this.sharedPref = sharedPref;
        FirstInit();
    }

    private void FirstInit(){
        if(sharedPref.getBoolean("INI",true)){
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putBoolean("INI",false);
            editor.putBoolean("METRONOME", false);
            editor.putBoolean("DANCE_TEMPO", false);
            editor.commit();
        }
    }

    public void setOption(String optionName, boolean option)
    {
       if(sharedPref.contains(optionName))
       {
           SharedPreferences.Editor editor = sharedPref.edit();
           editor.putBoolean(optionName, option);
           editor.commit();
       }
    }

    public boolean getOption(String optionName)
    {
        return sharedPref.getBoolean(optionName,false);
    }
}
