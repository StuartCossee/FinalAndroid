package com.example.androidlabs;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

class Item{
    String title;
    String description;
    String link;
    String date;
    Boolean b;
    Item(){

    }
    Item(String title, Boolean b){
        this.title = title;
        this.b = b;
    }
    Item(String title, String date, String link, String description, Boolean b){
        this.title = title;
        this.link = link;
        this.date = date;
        this.description = description;
        this.b = b;
    }
}

    /**
     * The class Main activity extends application compat activity
     */
public class MainActivity extends AppCompatActivity {
    private ArrayList<Item> elements = new ArrayList<>();
    private ProgressBar progressBar;
    private int progressLoop = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView list = findViewById(R.id.mobile_list);
        MyOpener db = new MyOpener(this);
        Toast.makeText(getApplicationContext(),R.string.welcome, Toast.LENGTH_SHORT).show();

        db.printCursor();
        MyListAdapter myAdapter;
        list.setAdapter(myAdapter = new MyListAdapter());
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(800);


        HTTPRequest req = new HTTPRequest();
        req.execute();

        list.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent myIntent = new Intent(this, NewsActivity.class);
            myIntent.putExtra("desc", elements.get(i).description ); //Optional parameters
            myIntent.putExtra("date", elements.get(i).date ); //Optional parameters
            myIntent.putExtra("title", elements.get(i).title ); //Optional parameters
            myIntent.putExtra("link", elements.get(i).link ); //Optional parameters
            this.startActivity(myIntent);
        });

        list.setOnItemLongClickListener((adapterView, view, i, l) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            String row = getResources().getString(R.string.selectedrow) + i;
            alertDialogBuilder.setTitle(getResources().getString(R.string.delete)).setMessage(row)
                    .setPositiveButton(R.string.yes, (click, arg) -> {
                        db.remove(i);
                        elements.remove(i);
                        myAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton(R.string.no, (click, arg) -> { })
                    .create().show();
            return false;
        });

            Button b = findViewById(R.id.btn);
            b.setOnClickListener( (click) -> {
                myAdapter.notifyDataSetChanged();
                b.setText(getResources().getString(R.string.add));
                EditText editText = findViewById(R.id.editBox);
                String s = editText.getText().toString();
                editText.setVisibility(View.VISIBLE);
                if(editText.getText().length() != 0){
                    this.setTitle(getResources().getString(R.string.app_name_revised).concat(" ").concat(editText.getText().toString()));
                    editText.setText("");
                }
                myAdapter.notifyDataSetChanged();
            });


    }


    private class HTTPRequest extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            try {
                URL url = new URL("https://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(response, null);
            int loop = 0;
                int eventType = parser.getEventType();
                Item item = new Item();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    publishProgress(progressLoop++);

                    String tagname = parser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if(parser.getName().equalsIgnoreCase("title")) {
                                item = new Item();
                                item.title = parser.nextText();
                                int u = 2;
                                elements.add(item);
                                SystemClock.sleep(400);
                            }
                            if(parser.getName().equalsIgnoreCase("description")) {
                                item.description = parser.nextText();
                            }
                            if(parser.getName().equalsIgnoreCase("link")) {
                                item.link = parser.nextText();
                            }
                            if(parser.getName().equalsIgnoreCase("pubDate")) {
                                item.date = parser.nextText();
                            }
                            break;

                        case XmlPullParser.TEXT:
                            break;

                        case XmlPullParser.END_TAG:
                            break;
                        default:
                            break;
                    }
                    eventType = parser.next();
                }

            } catch (XmlPullParserException e) {e.printStackTrace();}
            catch (IOException e) {e.printStackTrace();}

            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, R.string.finish, Snackbar.LENGTH_LONG).show();
            Button b = findViewById(R.id.btn);
            b.setEnabled(true);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
    }
    private class MyListAdapter extends BaseAdapter {

        @Override

        /**
         *
         * Gets the count
         *
         * @return the count
         */
        public int getCount() {

            return elements.size();
        }

        @Override

        /**
         *
         * Gets the item
         *
         * @param i  the i
         * @return the item
         */
        public Object getItem(int i) {

            return elements.get(i).title;
        }

        @Override

        /**
         *
         * Gets the item identifier
         *
         * @param i  the i
         * @return the item identifier
         */
        public long getItemId(int i) {

            return (long) i;
        }

        @Override

        /**
         *
         * Gets the view
         *
         * @param i  the i
         * @param view  the view
         * @param viewGroup  the view group
         * @return the view
         */
        public View getView(int i, View view, ViewGroup viewGroup) {

            View newView = view;
            LayoutInflater inflator = getLayoutInflater();

            if(newView == null){
                newView = inflator.inflate(R.layout.row_layout, viewGroup, false);
            }
            TextView textView = newView.findViewById(R.id.textBlock);
            textView.setText(getItem(i).toString());
//            if(elements.get(i).b){
//                newView.setBackgroundColor(Color.RED);
//            }
            return newView;
        }
    }


}

