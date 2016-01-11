package com.shekoofeh.example;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.shekoofeh.example.model.Flower;
import com.shekoofeh.example.parser.FlowerJSONParser;
import com.shekoofeh.example.parser.FlowerXMLParser;
import com.shekoofeh.example.parser.GeoIpParser;

//in the next version a list view of flower images plus a text (name , category, price,
// instructions) of each flower  must be shown
public class MainActivity extends Activity {

    TextView output;
    ProgressBar pb;
    List<MyTask> tasks;

    enum Action {J, X, G}

    ;
    Action action;
    List<Flower> flowerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//		Initialize the TextView for vertical scrolling
        output = (TextView) findViewById(R.id.textView);
        output.setMovementMethod(new ScrollingMovementMethod());

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);
        tasks = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isOnline()) {

            if (item.getItemId() == R.id.action_flower_json) {
                action = Action.J;
                requestData("http://services.hanselandpetal.com/feeds/flowers.json");
            } else if (item.getItemId() == R.id.action_flower_xml) {
                action = Action.X;
                requestData("http://services.hanselandpetal.com/feeds/flowers.xml");
            } else if (item.getItemId() == R.id.action_geo_finder) {
                action = Action.G;
                requestData("http://www.webservicex.net/geoipservice.asmx/GetGeoIP?IpAddress=54.174.31.254");
            }
        } else {
            Toast.makeText(this, "No data connection available", Toast.LENGTH_LONG).show();
        }

        return false;
    }


    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }

    protected void updateDisplay() {
        StringBuffer sb = new StringBuffer("");
        if (flowerList != null) {
            for (Flower flower : flowerList) {
                sb.append(flower.getName() + "\n");
            }

            output.append(sb.toString());
        }
    }

    protected void updateDisplay(String str) {
        StringBuffer sb = new StringBuffer("1.Received Response:\n");
        sb.append("------------------------------\n");
        sb.append(str);
        sb.append("\n------------------------------\n\n");
        sb.append("2.Parsed Response (only list of flower names :\n");
        sb.append("------------------------------\n");
        output.setText(sb.toString());

    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private class MyTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected String doInBackground(String... params) {

            String content = HTTPManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            updateDisplay(result);
            if (action == Action.J)
                getFlowerJson(result);
            else if (action == Action.X)
                getFlowerXML(result);
            else if (action == Action.G)
                getGeoIp(result);

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            //updateDisplay(values[0]);
        }

        public void getFlowerJson(String result) {
            updateDisplay(result);
            flowerList = FlowerJSONParser.parseFeed(result);
            updateDisplay();
        }

        public void getFlowerXML(String result) {
            updateDisplay(result);
            flowerList = FlowerXMLParser.parseFeed(result);
            updateDisplay();
        }

        public void getGeoIp(String result) {
            updateDisplay(result);
            String CountryName = GeoIpParser.parseFeed(result);
            if (CountryName != null) {
                output.append(CountryName);
            }
        }

    }

}