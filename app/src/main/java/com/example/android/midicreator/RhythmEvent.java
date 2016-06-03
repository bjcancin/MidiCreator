package com.example.android.midicreator;

public class RhythmEvent {

    private int tempo;
    private String event;

    public RhythmEvent(int tempo, String event){
        this.tempo = tempo;
        this.event = event;
    }


    public int getTempo(){
        return tempo;
    }

    public String getEvent(){
        return event;
    }

    public void setTempo(int tempo){
        this.tempo = tempo;
    }

}