package com.example.rozer.booklisting;

import android.Manifest;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Rozer on 2/7/2020.
 */
public class BookLoader extends AsyncTaskLoader<ArrayList<Book>> {

    private String urlLink = null;

    private final int PERMISSION_CODE = 1;

    public BookLoader(Context context, String url) {
        super(context);
        this.urlLink = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Book> loadInBackground() {

        if (urlLink == null) {
            return null;
        }

        URL url = null;
        try {
            url = new URL(urlLink);
        } catch (MalformedURLException e) {
            Log.e("URL CONNECTION", "URL object not created");
        }
        String jsonData = makeHttpConnection(url);
        return extractBookDataFromJson(jsonData);

    }

    private String makeHttpConnection(URL url) {
        String jsonData = "";
        HttpURLConnection urlConnection = null;
        InputStream in = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            in = urlConnection.getInputStream();
            jsonData = readData(in);

        } catch (IOException e) {
            Log.e("CONNECTING URL", "Could not connect to url");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                Log.e("INPUTSTREM", "INPUTSTREAM cannot be closed");
            }
        }
        //return jsonData;
        return jsonData;
    }

    private String readData(InputStream in) throws IOException {
        StringBuilder jsonData = new StringBuilder();
        InputStreamReader inputReader = new InputStreamReader(in);
        BufferedReader reader = new BufferedReader(inputReader);
        String line = reader.readLine();
        while (line != null) {
            jsonData.append(line);
            line = reader.readLine();
        }
        return jsonData.toString();
    }

    private ArrayList<Book> extractBookDataFromJson(String jsonData) {
        //Maximum three  authors per book
        //String authorsName[] = new String[3];
        String title = null;
        String thumbnailLink = null;
        String previewLink = null;
        DownloadBookImage downloadBookImage = new DownloadBookImage();
        ArrayList<Book> bookList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray items = jsonObject.getJSONArray("items");
            for(int i = 0;i<items.length();i++){
                JSONObject item = items.getJSONObject(i);
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                title = volumeInfo.getString("title");
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                thumbnailLink = imageLinks.getString("thumbnail");
                thumbnailLink = downloadBookImage.doInBackground(thumbnailLink,title,i);
                previewLink = volumeInfo.getString("previewLink");
                JSONArray authors = volumeInfo.getJSONArray("authors");
                String authorsName[] = new String[authors.length()];
                for (int j = 0; j < authors.length(); j++) {
                    if(authors.length()< 3)
                    authorsName[j] = authors.get(j).toString();
                    else break;
                }
                bookList.add(new Book(title, authorsName, thumbnailLink,previewLink));
            }} catch (JSONException e) {
            Log.e("JSON EXCEPTION","BOOKLOADER EXTRACT EXCEPTION");
        }





        return bookList;
    }

    private class DownloadBookImage  {

        public String doInBackground(String urlLink,String title,int uniqueKey) {
            URL url = null;
            try {
                url = new URL(urlLink);
            } catch (MalformedURLException e) {
                Log.e("URL CREATION", "URL NOT CREATED");
            }

            String imagePath = makeHttpConnection(url,title,uniqueKey);

            return imagePath;
        }
        private String makeHttpConnection(URL url,String title,int uniqueKey) {
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            String newFilePath = null;
            String imageName = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(15000);
                urlConnection.setReadTimeout(10000);
                urlConnection.connect();
                String path = String.valueOf(Environment.getDataDirectory().getAbsolutePath());

                if (ListActivity.WRITE_PERMISSION) {

                    //File new_folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"book_image");
                    //boolean flag = new_folder.exists();
                    //if(!new_folder.exists());
                    newFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Downloaded_Image/";
                    File file = new File(newFilePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    imageName = title+uniqueKey+".jpg";
                    File newFile = new File(newFilePath + imageName);

                    if (newFile.exists()) {
                        newFile.delete();
                    }

                    newFile.createNewFile();
                    boolean flag = newFile.exists();
                    int downloadSize = urlConnection.getContentLength();
                    inputStream = new BufferedInputStream(url.openStream(), 8192);
                    byte data[] = new byte[1024];

                    int count = 0;
                    FileOutputStream outputStream = new FileOutputStream(newFile);

                    while ((count = inputStream.read(data)) != -1) {
                        outputStream.write(data, 0, count);
                    }
                    inputStream.close();
                    outputStream.close();
                }


            } catch (IOException e) {
                Log.e("URL CONNECTION", "CONNECTION NOT MADE");
            }
            return newFilePath + imageName;
        }


    }

}

