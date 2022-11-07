package com.example.androidapp;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CSVReader extends AppCompatActivity {

    private List<WeatherSample> weatherSamples = new ArrayList<>();

    public void readWeatherData() {
        // Read the raw csv file
        InputStream is = getResources().openRawResource(R.raw.data);

        // Reads text from character-input stream, buffering characters for efficient reading
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        // Initialization
        String line = "";

        // Initialization
        try {
            // Step over headers
            reader.readLine();

            // If buffer is not empty
            while ((line = reader.readLine()) != null) {
                Log.d("MyActivity","Line: " + line);
                // use comma as separator columns of CSV
                String[] tokens = line.split(",");
                // Read the data
                WeatherSample sample = new WeatherSample();

                // Setters
                sample.setMonth(tokens[0]);
                sample.setRainfall(Double.parseDouble(tokens[1]));
                sample.setSumHours(Integer.parseInt(tokens[2]));

                // Adding object to a class
                weatherSamples.add(sample);

                // Log the object
                Log.d("My Activity", "Just created: " + sample);
            }

        } catch (IOException e) {
            // Logs error with priority level
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);

            // Prints throwable details
            e.printStackTrace();
        }
    }
}
