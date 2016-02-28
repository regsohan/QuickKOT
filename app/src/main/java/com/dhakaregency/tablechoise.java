package com.dhakaregency;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.dhakaregency.quickkot.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class tablechoise extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablechoise);
        Bundle b = getIntent().getExtras();
        String moduleid="02";// b.getString("moduleid");

        ArrayList<String> passing = new ArrayList<String>();
        passing.add(moduleid);
        passing.add(moduleid);
        LoadTables  loadTables=new  LoadTables();
        loadTables.execute(passing);

    }

    public void PopulateOutlets(ArrayList<TableList> tableListArrayList) {
        boolean isFirstTime = true;
        boolean isColumnCountingFinished = false;
        TableLayout tableLayout = (TableLayout) findViewById(R.id.tableListLayout);
        TableRow tableRow = null;
        int col = 0;
        for (final TableList tableList: tableListArrayList) {
            if (isFirstTime) {
                tableRow = new TableRow(getApplicationContext());
                tableRow.setLayoutParams(new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT, 10f
                ));
                tableLayout.addView(tableRow);
                isFirstTime = false;
            }
            if (!isFirstTime && isColumnCountingFinished){
                tableRow = new TableRow(getApplicationContext());
                tableRow.setLayoutParams(new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT, 1.0f
                ));
                tableLayout.addView(tableRow);
                isColumnCountingFinished = false;
                col = 0;
            }
            if (!isColumnCountingFinished) {

                Button button = new Button(getApplicationContext());
                button.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT, 1.0f
                ));
                String tablecode = tableList.getCode().toString();
                int tableused = tableList.getUsed();
                button.setText(tableList.getDescription().toString());

              /*  if(tableused==0)// table is open to use for KOT
                {
                    button.setBackgroundColor(Color.GREEN);
                }
                else
                {
                    button.setBackgroundColor(Color.RED);
                }*/

                button.setPadding(1,1,1,1);

                button.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {

                                                  GotoPax("Admin", "02","");
                                              }
                                          }
                );
                tableRow.addView(button);
            }
            if (col > 2) {
                isColumnCountingFinished = true;
            }
            else {
                col++;
            }

        }

    }
    private void GotoPax(String userId,String moduleId,String registration)
    {
        Intent intent = new Intent(getApplicationContext(),pax.class );
        //Create the bundle
        Bundle bundle = new Bundle();
        //Add your data to bundle
        bundle.putString("userid", userId.toString());
        bundle.putString("moduleId", moduleId.toString());
        //Add the bundle to the intent
        intent .putExtras(bundle);
        startActivity(intent);
    }
    public class LoadTables extends AsyncTask<ArrayList<String>, Void, ArrayList<TableList>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(ArrayList<TableList> tableListArrayList) {
            super.onPostExecute(tableListArrayList);
            PopulateOutlets(tableListArrayList);
        }

        @Override
        protected ArrayList<TableList> doInBackground(ArrayList<String>... params) {

            String str = "http://192.168.99.12:8080/AuthService.svc/GetTableList";
            String response = "";
            ArrayList<TableList> tableListArrayList = new ArrayList<>();

            URL url = null;
            try {
                url = new URL(str);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {

                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) url.openConnection();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                String moduleid = params[0].toString();

                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Content-Type", "application/json");


                JSONObject jsonObject = new JSONObject();
                // Build JSON string
                JSONStringer userJson = new JSONStringer()
                        .object()
                        .key("moduleid").value("02")//Todo place your variable here
                        .endObject();

                //byte[] outputBytes = jsonParam.toString().getBytes("UTF-8");
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
                outputStreamWriter.write(userJson.toString());
                outputStreamWriter.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Integer result = 0;
            JSONObject jObject = null;
            if (!response.isEmpty()) {
                try {
                    jObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            try {
                JSONArray jsonArray = (JSONArray) jObject.getJSONArray("GetTableListResult");
                try {

                    for (int i=0;i<jsonArray.length();i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        TableList tableList= new TableList();
                        tableList.setCode(object.getString("code"));
                        tableList.setDescription(object.getString("description"));
                        tableList.setUsed(object.getInt("used"));
                        tableListArrayList.add(tableList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return tableListArrayList;
        }
    }
}


