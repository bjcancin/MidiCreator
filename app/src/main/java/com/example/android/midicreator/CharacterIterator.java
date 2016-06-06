package com.example.android.midicreator;

import java.util.Iterator;

class CharacterIterator implements Iterator<Character> {

    private String str;
    private int pos = 0;

    public CharacterIterator(String str) {
        this.str = str;
    }

    public boolean hasNext() {
        return pos < str.length();
    }

    public Character next() {
        return str.charAt(pos++);
    }

    public void restart(){
        pos = 0;
    }

    public void setString(String string){
        this.str = string;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public int getPos(){return pos;}
}
