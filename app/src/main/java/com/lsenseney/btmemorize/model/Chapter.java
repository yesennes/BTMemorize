package com.lsenseney.btmemorize.model;

public class Chapter {
    public final String number;
    public final int position;

    //Empty constructor for moshi
    @SuppressWarnings("unused")
    public Chapter() {
        this("0", 0);
    }

    public Chapter(String number, int position) {
        this.number = number;
        this.position = position;
    }

    @Override
    public String toString() {
        return number;
    }
}
