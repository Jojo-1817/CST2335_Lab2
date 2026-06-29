package com.example.androidlabs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    ImageView catImageView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        catImageView = findViewById(R.id.catImageView);
        progressBar = findViewById(R.id.progressBar);

        new CatImages().execute();
        android.util.Log.d("TEST", "App started successfully");
    }

    class CatImages extends AsyncTask<String, Integer, String> {

        Bitmap currentCat;

        @Override
        protected String doInBackground(String... strings) {
            while (true) {
                try {

                    URL jsonUrl = new URL("https://cataas.com/cat?json=true");
                    HttpURLConnection jsonConn = (HttpURLConnection) jsonUrl.openConnection();
                    jsonConn.setConnectTimeout(10000);
                    jsonConn.setReadTimeout(10000);
                    jsonConn.setRequestProperty("Accept", "application/json");
                    jsonConn.connect();

                    int responseCode = jsonConn.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        Thread.sleep(3000); // wait before retrying
                        continue;
                    }

                    Scanner scanner = new Scanner(jsonConn.getInputStream());
                    StringBuilder jsonStr = new StringBuilder();
                    while (scanner.hasNextLine()) jsonStr.append(scanner.nextLine());
                    scanner.close();
                    jsonConn.disconnect();

                    JSONObject json = new JSONObject(jsonStr.toString());


                    String id = json.has("_id") ? json.getString("_id") : json.getString("id");
                    String imagePath = json.getString("url");
                    String imageUrl = imagePath.startsWith("http") ? imagePath : "https://cataas.com" + imagePath;


                    File imageFile = new File(getFilesDir(), id + ".jpg");

                    if (imageFile.exists()) {
                        currentCat = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    } else {
                        URL imgUrl = new URL(imageUrl);
                        HttpURLConnection imgConn = (HttpURLConnection) imgUrl.openConnection();
                        imgConn.setConnectTimeout(10000);
                        imgConn.setReadTimeout(10000);
                        imgConn.connect();

                        if (imgConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            InputStream stream = imgConn.getInputStream();
                            currentCat = BitmapFactory.decodeStream(stream);
                            stream.close();
                            imgConn.disconnect();

                            if (currentCat != null) {
                                FileOutputStream fos = new FileOutputStream(imageFile);
                                currentCat.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                fos.flush();
                                fos.close();
                            }
                        }
                    }


                    publishProgress(0);
                    for (int i = 1; i <= 100; i++) {
                        publishProgress(i);
                        Thread.sleep(30);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    try { Thread.sleep(3000); } catch (Exception ignored) {} // wait before retrying on error
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progress = values[0];
            progressBar.setProgress(progress);

            // Only swap the image when progress resets to 0 (new image ready)
            if (progress == 0 && currentCat != null) {
                catImageView.setImageBitmap(currentCat);
            }
        }
    }
}