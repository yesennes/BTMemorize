package com.lsenseney.btmemorize.model;

public class BibleDatum {
    private BibleReference reference;
    private BibleReference text;
    private BibleDatum first;
    private BibleDatum second;

    public BibleDatum(BibleReference reference, String text){
            this.reference = reference;
        }

        @Override
        public String toString(){
            String ans = "BibleDatum of " + reference;
            if (!isLeaf()) {
                ans += " (" + first.reference + ", " + second.reference + ")";
            }
            return ans;
        }

        boolean isLeaf(){
            return first == null;
        }
}
