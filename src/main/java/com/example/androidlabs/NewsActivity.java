package com.example.androidlabs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * The class Main activity extends application compat activity
 */
public class NewsActivity extends AppCompatActivity {
    private ArrayList<Item> elements = new ArrayList<>();
    private ProgressBar progressBar;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");
        String description = intent.getStringExtra("desc");
        String link = intent.getStringExtra("link");

        TextView t = findViewById(R.id.article_title);
        t.setText(title);
        TextView d = findViewById(R.id.article_date);
        d.setText(date);
        TextView c = findViewById(R.id.article_content);
        c.setText(description);
        Button b = findViewById(R.id.button);
        b.setOnClickListener( (click) -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(Intent.createChooser(i, "Browse with"));
        });

    }

}
