package com.lsenseney.btmemorize.model;

public class ChapterReference {
    public final String number;
    public final int position;

    //Empty constructor for moshi
    @SuppressWarnings("unused")
    public ChapterReference() {
        this("0", 0);
    }

    public ChapterReference(String number, int position) {
        this.number = number;
        this.position = position;
    }

    @Override
    public String toString() {
        return number;
    }
}
