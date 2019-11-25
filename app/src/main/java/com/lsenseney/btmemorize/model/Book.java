package com.lsenseney.btmemorize.model;

import java.util.List;

public class Book {
    public final String name;
    public final String id;
    public final List<ChapterReference> chapters;

    //Empty constructor for moshi
    @SuppressWarnings("unused")
    public Book(){
        this(null, null);
    }

    public Book(String name, String id) {
        this.name = name;
        this.id = id;
        chapters = null;
    }

    @Override
    public String toString() {
        return name;
    }
}
