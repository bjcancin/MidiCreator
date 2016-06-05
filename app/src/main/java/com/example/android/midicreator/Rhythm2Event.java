package com.example.android.midicreator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Rhythm2Event {

    public static Map<String, byte[]> eventDictionary = new HashMap<String, byte[]>() {/**
     *
     */
    private static final long serialVersionUID = 1L;
        {
            put("xOn", hexStringToByteArray("993d7f"));
            put("xOff", hexStringToByteArray("893d7f"));
            put("oOn", hexStringToByteArray("99247f"));
            put("oOff", hexStringToByteArray("89247f"));
            put("midiHeader", hexStringToByteArray("4d54686400000006000100020080"));
            put("trackHeader", hexStringToByteArray("4d54726b"));
            put("closeTrack", hexStringToByteArray("00ff2f00"));
            put("trackTempoHeader", hexStringToByteArray("4d54726b0000000b00ff5103"));
        }};

    public static void WriteMidi(File file, String ritmo, int tempo, int repeat) throws IOException {
        Map<Integer, RhythmEvent> aux = eventMap(ritmo);
        WriteMidi(file, tempo, repeat, aux, ritmo);
    }

    public static void WriteMidi(File file, int tempo, int repeat, Map<Integer, RhythmEvent> eventMap, String ritmo) throws IOException{

        int count = 0;
        CharacterIterator ritmoIterator = new CharacterIterator(new StringBuffer(ritmo).reverse().toString());

        while(ritmoIterator.hasNext()){
            if(ritmoIterator.next().compareTo('.')== 0)
            {
                count++;
            }
            else
            {
                break;
            }
        }


        OutputStream out = null;

        try {
            out = new BufferedOutputStream(new FileOutputStream(file));

            //FileOutputStream out = new FileOutputStream(fileName);

            // Escribimos el header
            out.write(eventDictionary.get("midiHeader"));

            // Escribimos el tempoTrack
            out.write(eventDictionary.get("trackTempoHeader"));
            out.write(intToByteArray3(60000000/tempo));
            out.write(eventDictionary.get("closeTrack"));

            // Escribimos el track de ritmo
            out.write(eventDictionary.get("trackHeader"));

            // Primero debemos saber la cantidad de bytes que utilizará.
            // Para ello, escribiremos todas las instrucciones en una Lista de byte[]

            List<byte[]> trackList = new ArrayList<byte[]>();

            int numOfBytes = 0;

            for(int i = 0; i<repeat; i++)
            {
                for(int j=0; j<eventMap.size(); j++)
                {
                    int timeEvent;

                    if(i>0 && j == 0){
                        timeEvent = eventMap.get(j+1).getTempo() + 64*count;
                    }

                    else{
                        timeEvent = eventMap.get(j+1).getTempo();
                    }

                    // Se escribe el timestamp
                    byte[] aux = int2VarLengthbytes(timeEvent);
                    numOfBytes += aux.length;
                    trackList.add(aux);

                    // Se escribe el evento
                    byte[] aux2 = eventDictionary.get(eventMap.get(j+1).getEvent());
                    numOfBytes += aux2.length;
                    trackList.add(aux2);
                }
            }

            // Escribimos el número de bytes en el archivo
            out.write(intToByteArray4(numOfBytes+4));

            // Escribimos los archivos de la lista
            Iterator<byte[]> trackListIterator = trackList.iterator();

            while(trackListIterator.hasNext())
            {
                out.write(trackListIterator.next());
            }

            // Cerramos el track
            out.write(eventDictionary.get("closeTrack"));
        }
        finally {
                if (out != null) {
                    out.close();
                }
            }

    }

    private static byte[] intToByteArray3(int value) {
        return new byte[] {
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    private static byte[] intToByteArray4(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    private static Map<Integer, RhythmEvent> eventMap (String rhythm){

        Map<Integer, RhythmEvent> outMap = new HashMap<Integer, RhythmEvent>();

        CharacterIterator rhythmIterator = new CharacterIterator(rhythm);
        boolean pastX = false;
        boolean pastO = false;
        int tick = 0;
        int lastTick = 0;
        String event;

        int j = 1;
        int i = 0;

        for(i = 0; rhythmIterator.hasNext(); i++)
        {
            Character c = rhythmIterator.next();

            if(pastX){
                outMap.put(j++, new RhythmEvent(i*64, "xOff"));
                pastX = false;

            }
            else if(pastO){
                outMap.put(j++, new RhythmEvent(i*64, "oOff"));
                pastO = false;
            }



            if(c.compareTo('x') == 0){
                outMap.put(j++, new RhythmEvent(i*64, "xOn"));
                pastX = true;
            }
            else if(c.compareTo('o') == 0){
                outMap.put(j++, new RhythmEvent(i*64, "oOn"));
                pastO = true;
            }

        }

        if(pastX){
            outMap.put(j, new RhythmEvent(i*64, "xOff"));
            pastX = false;
        }

        else if(pastO){
            outMap.put(j, new RhythmEvent(i*64, "oOff"));
            pastO = false;
        }



        for(i = 1; i<=j; i++)
        {
            tick = outMap.get(i).getTempo();
            event = outMap.get(i).getEvent();

            outMap.put(i, new RhythmEvent(tick - lastTick, event));

            lastTick = tick;
        }


        return outMap;

    }

    private static byte[] int2VarLengthbytes (int value)
    {
        int buffer = value & 0x7F;
        int buffer2;
        int length = 0;

        while ((value >>= 7) != 0)
        {
            buffer <<= 8;
            buffer |= ((value & 0x7F) | 0x80);
        }

        buffer2 = buffer;

        while (true)
        {
            length++;
            if ((buffer2 & 0x80) != 0)
                buffer2 >>>= 8;
            else
                break;
        }

        byte[] out = new byte[length];

        for(int i = 0; i<length; i++){
            out[i] = (byte) (buffer & 0xff);
            buffer >>>= 8;
        }

        return out;
    }
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];

        for(int i = 0; i < len; i+=2){
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }

        return data;
    }
}
