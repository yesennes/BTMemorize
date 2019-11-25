package com.lsenseney.btmemorize;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.lsenseney.btmemorize.model.APIBible;
import com.lsenseney.btmemorize.model.Book;
import com.lsenseney.btmemorize.model.ChapterReference;

import java.util.ArrayList;

public class MemorizationSelection extends AppCompatActivity {
    private ArrayAdapter<String> versionAdepter;
    private ArrayAdapter<Book> bookAdapter;
    private ArrayAdapter<ChapterReference> startChapterAdapter;
    private ArrayAdapter<ChapterReference> endChapterAdapter;

    private APIBible apiBible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memorization_selection);

        apiBible = APIBible.getInstance(this);

        Spinner version = findViewById(R.id.translation_selector);
        versionAdepter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(apiBible.getVersions()));
        version.setAdapter(versionAdepter);
        version.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedVersion = (String)versionAdepter.getItem(position);
                apiBible.getBooks(selectedVersion, books -> {
                    bookAdapter.clear();
                    bookAdapter.addAll(books);
                    bookAdapter.notifyDataSetChanged();
                }, null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner bookSpinner = findViewById(R.id.book_selector);
        bookAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        bookSpinner.setAdapter(bookAdapter);
        bookSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startChapterAdapter.clear();
                startChapterAdapter.addAll(bookAdapter.getItem(position).chapters);
                startChapterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner startChapterSpinner = findViewById(R.id.start_chapter_selector);
        startChapterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        startChapterSpinner.setAdapter(startChapterAdapter);
        startChapterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endChapterAdapter.clear();
                for (int i = position; i < startChapterAdapter.getCount(); i++) {
                    endChapterAdapter.add(startChapterAdapter.getItem(i));
                }
                endChapterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner endChapterSpinner = findViewById(R.id.end_chapter_selector);
        endChapterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        endChapterSpinner.setAdapter(endChapterAdapter);

        version.setSelection(0);
    }
}
