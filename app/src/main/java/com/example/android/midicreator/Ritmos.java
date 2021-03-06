package com.example.android.midicreator;

import android.content.SharedPreferences;

import java.util.Map;


public class Ritmos {

    private SharedPreferences sharedPref, options;


    // Constructor
    public Ritmos(SharedPreferences sharedPref, SharedPreferences options){
        this.sharedPref = sharedPref;
        this.options = options;
        FirstInit();
    }

    private void FirstInit(){
        if(sharedPref.getBoolean("INI",true)){
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putBoolean("INI",false);
            editor.putString("Alegría", "xxx.o.x.x.o.xxo.xxo.x.o.");
            editor.putString("Bulería", "x.x.o.x.x.o.xxo.xxo.x.o.");
            editor.putString("Soleá", ".xx.o..xx.o..xo..xo.x.o.");
            editor.putString("Sevillanas", "x.x.o.x.x.o.");
            editor.putString("Tangos", "xxx.x...");

            editor.apply();

            editor = options.edit();

            editor.putBoolean("INI",false);
            editor.putInt("TEMPO_Alegría", 120);
            editor.putInt("TEMPO_Bulería", 120);
            editor.putInt("TEMPO_Soleá", 120);
            editor.putInt("TEMPO_Sevillanas", 120);
            editor.putInt("TEMPO_Tangos", 120);

            editor.putInt("REPEAT_Alegría", 10);
            editor.putInt("REPEAT_Bulería", 10);
            editor.putInt("REPEAT_Soleá", 10);
            editor.putInt("REPEAT_Sevillanas", 10);
            editor.putInt("REPEAT_Tangos", 10);

            editor.apply();
        }
    }

    public boolean ini(){
        return sharedPref.getBoolean("INI",true) & options.getBoolean("INI",true);
    }

    public boolean createRitmo(String name, String ritmo){

        ritmo = cleanRitmo(ritmo);
        int length_name = name.length();

        if(validateRitmo(ritmo) && length_name > 0){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(name, ritmo.toLowerCase());

            editor.apply();

            editor = options.edit();

            editor.putInt("TEMPO_" + name,120);
            editor.putInt("REPEAT_" + name,10);

            editor.apply();

            return true;
        }

        else{
            return false;
        }

    }

    public boolean editRitmo(String name, String ritmo)
    {
        if(validateRitmo(ritmo)){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(name, cleanRitmo(ritmo));

            editor.apply();
            return true;
        }

        else{
            return false;
        }
    }

    public String cleanRitmo(String ritmo){

        ritmo = ritmo.replace(",","");
        ritmo = ritmo.replace("|","");
        ritmo = ritmo.replace("-","");
        ritmo = ritmo.replace("_","");
        ritmo = ritmo.replace(" ","");

        ritmo = ritmo.toLowerCase();

        return ritmo;
    }

    public void setTempo(String name, int tempo){
        SharedPreferences.Editor editor = options.edit();
        editor.putInt("TEMPO_" + name,tempo);
        editor.apply();
    }

    public void setRepeat(String name, int repeat){
        SharedPreferences.Editor editor = options.edit();
        editor.putInt("REPEAT_" + name,repeat);
        editor.apply();
    }

    public int getTempo(String name){
        return options.getInt("TEMPO_" + name, -1);
    }

    public int getRepeat(String name){
        return options.getInt("REPEAT_" + name, -1);
    }


    public boolean validateRitmo(String ritmo){

        boolean validate = true;

        ritmo = cleanRitmo(ritmo);

        for(Character c : ritmo.toCharArray()){
            if(compareChar(c,'x') || compareChar(c,'o') || compareChar(c,'.')){
            }
            else
            {
                validate = false;
                break;
            }

        }

        return validate;
    }

    public boolean compareChar(Character a, Character b)
    {
        return a.compareTo(b) == 0;
    }

    public boolean deleteRitmo(String name){

        if(sharedPref.contains(name)){

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove(name);
            editor.apply();

            editor = options.edit();
            editor.remove("TEMPO_" + name);
            editor.remove("REPEAT_" + name);
            editor.apply();

            return true;
        }

        else
            return false;
    }

    public Map<String, ?> getRitmos(){
        Map<String, ?> ritmos = sharedPref.getAll();
        ritmos.remove("INI");

        return ritmos;
    }

    public String getRitmo(String name){
        return sharedPref.getString(name,"xox");
    }

}
