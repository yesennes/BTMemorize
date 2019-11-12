package com.lsenseney.btmemorize.model;

public class BibleReference {
    public String version;
    public String book;
    public int startChapter;
    public int endChapter;
    public int startVerse;
    public int endVerse;

    public BibleReference(String book, String version, int chapter) {
        this(book, version, chapter, 1, Integer.MAX_VALUE);
    }

    public BibleReference(String book, String version, int chapter, int startVerse, int endVerse) {
        this(book, version, chapter, chapter, startVerse, endVerse);
    }

    public BibleReference(String book, String version, int startChappter, int endChapter, int startVerse, int endVerse) {
        this.book = book;
        this.version = version;
        this.startChapter = startChappter;
        this.endChapter = endChapter;
        this.startVerse = startVerse;
        this.endVerse = endVerse;
    }

    @Override
    public String toString() {
        String ans = this.book;
        if (endChapter == Integer.MAX_VALUE) {
            if (startChapter != 1) {
                ans += " " + startChapter + "-";
            }
        } else {
            ans += " " + startChapter;
            if (startChapter != endChapter) {
                ans += "-" + endChapter;
            }
        }
        if (endVerse == Integer.MAX_VALUE) {
            if (startVerse != 1) {
                ans += ":" + startVerse + "-";
            }
        } else {
            ans += ":" + startVerse;
            if (startVerse != endVerse) {
                ans += "-" + endVerse;
            }
        }
        return ans;
    }
}
