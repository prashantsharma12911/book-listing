package com.example.rozer.booklisting;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.rozer.booklisting";
    TextView text = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button searchButton = (Button) findViewById(R.id.search_button);
        text = (EditText) findViewById(R.id.editText);


            searchButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String searchkey = text.getText().toString();
                    if (!checkNetworkConnection()) {
                        Toast.makeText(MainActivity.this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();

                    }

                    else if (searchkey.length() == 0) {

                        Toast.makeText(MainActivity.this, "Please insert search key", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        final String searchKey = text.getText().toString();
                        Intent intent = new Intent(MainActivity.this, ListActivity.class);
                        intent.putExtra(EXTRA_MESSAGE, searchKey);
                        startActivity(intent);
                    }

                }
            });


    }
    private boolean checkNetworkConnection(){
        ConnectivityManager cm =
                (ConnectivityManager)MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
