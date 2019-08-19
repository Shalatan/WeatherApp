package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultTextView;

    public class downloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data!=-1)
                {
                    char currrent = (char) data;
                    result += currrent;
                    data = reader.read();
                }
                return result;
            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_SHORT).show();
                return null;
            }

        }
        @Override
        protected void onPostExecute(String s) {                // S = result
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");
                String mainInfo = jsonObject.getString("main");

                JSONArray weatherArray = new JSONArray(weatherInfo);
                JSONArray mainArray = new JSONArray(mainInfo);

                String main = null , description = null , temp = null;
                String message = null;

                for (int i = 0; i < weatherArray.length(); i++) {
                    JSONObject jsonPart1 = weatherArray.getJSONObject(i);
                    main = jsonPart1.getString("main");
                    description = jsonPart1.getString("description");
                    message = "Main - " + main + "\n" + "Descitpion - " + description;
                }
                for(int j=0; j < mainArray.length();j++) {
                    JSONObject jsonPart2 = mainArray.getJSONObject(j);
                    temp = jsonPart2.getString("temp");
                    message += "Temperature - " + temp;
                }
                if (!message.equals(""))
                    resultTextView.setText(message);
                else
                    Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_SHORT).show();
            }
        catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not finnd weather",Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        resultTextView = findViewById(R.id.resultTextView);
    }

    public void getWeather(View view)
    {
        String s = editText.getText().toString();
        downloadTask task = new downloadTask();
        task.execute("https://openweathermap.org/data/2.5/weather?q=" + s + "&appid=b6907d289e10d714a6e88b30761fae22");

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }
}
