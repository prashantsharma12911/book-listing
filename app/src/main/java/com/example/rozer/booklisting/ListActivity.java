package com.example.rozer.booklisting;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Rozer on 1/31/2020.
 */
public class ListActivity extends AppCompatActivity implements LoaderCallbacks<ArrayList<Book>> {
    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int BOOK_LOADER_ID = 1;
    ArrayList<Book> books = new ArrayList<Book>();
    Book bookInfo = null;
    String downloadImagePath = null;
    String bookImageLink = null;
    String urlLink = null;
    private final int PERMISSION_CODE = 1;
    String searchKey = null;
    BookAdapter adapter = null;
    public static boolean WRITE_PERMISSION = false;
    ProgressBar progressBar;
    ListView listView;
    TextView tv;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

         progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        listView = (ListView) findViewById(R.id.books_layout);
        listView.setEmptyView(progressBar);
        tv = (TextView) findViewById(R.id.record_notFound_text);
        tv.setVisibility(View.GONE);
        Intent intent = getIntent();
        searchKey = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        // Get a reference to the LoaderManager, in order to interact with loaders.



        String url = "http://books.google.com/books/content?id=qKFDDAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api";
        urlLink = "https://www.googleapis.com/books/v1/volumes?q="+searchKey;

        android.app.LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(BOOK_LOADER_ID, null, this);


    }

    @Override
    public Loader<ArrayList<Book>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
            WRITE_PERMISSION = checkWritePermission();
            return new BookLoader(this, urlLink);
    }
    public void onLoaderReset(Loader<ArrayList<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        if(adapter != null) {
            adapter.clear();
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Book>> loader, ArrayList<Book> books) {
        // Clear the adapter of previous earthquake data

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        progressBar.setVisibility(View.GONE);
        if(books.isEmpty()){
            tv.setVisibility(View.VISIBLE);
            listView.setEmptyView(tv);
        }
        if (books != null && !books.isEmpty()) {
            adapter = adapter = new BookAdapter(this, books);
            final ListView listView = (ListView) findViewById(R.id.books_layout);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Book clickedBook = adapter.getItem(position);
                    String url = clickedBook.getPreviewLink();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            });

        }
    }
    public Context getContext(){
        return ListActivity.this;
    }

    public boolean checkWritePermission(){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(PERMISSION_CODE);
        }
        return ContextCompat.checkSelfPermission(ListActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

    }
        private void requestPermission(int PERMISSION_CODE) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(ListActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(ListActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(ListActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_CODE);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               String[] permissions, int[] grantResults) {
            switch (requestCode) {
                case PERMISSION_CODE: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                    } else {
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                    return;
                }

                // other 'case' lines to check for other
                // permissions this app might request.
            }
        }

    }



