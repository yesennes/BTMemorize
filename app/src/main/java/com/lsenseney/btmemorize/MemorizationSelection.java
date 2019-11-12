package com.lsenseney.btmemorize;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.lsenseney.btmemorize.model.APIBible;

import java.util.ArrayList;

public class MemorizationSelection extends AppCompatActivity {
    private SpinnerAdapter versionAdpter;
    private Spinner startChapter;
    private Spinner startVerse;
    private Spinner endVerse;
    private APIBible apiBible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memorization_selection);

        apiBible = APIBible.getInstance(this);

        Spinner version = findViewById(R.id.translation_selector);
        versionAdpter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<>(apiBible.getVersions()));
        version.setAdapter(versionAdpter);
    }
}
