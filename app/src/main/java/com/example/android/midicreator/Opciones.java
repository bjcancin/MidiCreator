package com.example.android.midicreator;

import android.content.SharedPreferences;

import java.util.Map;

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
            editor.putBoolean("Metrónomo", false);
            editor.putBoolean("Tiempo de Palmas", false);
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

    public Map<String, Boolean> getOptions(){
        Map<String, Boolean> opcionesMap = (Map<String, Boolean>) sharedPref.getAll();
        opcionesMap.remove("INI");

        return opcionesMap;
    }
}
