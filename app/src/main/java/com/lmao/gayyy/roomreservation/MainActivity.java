package com.lmao.gayyy.roomreservation;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private TextView messageView;
    private static final String uri = "https://anbo-roomreservation.azurewebsites.net/api/reservations/room/1"; //"https://reqres.in/api/users/2";
    private ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        setShareActionIntent("trollbar");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_nothing_really:
                Toast.makeText(this, "You want help? for free??? fuck off", Toast.LENGTH_LONG).show();
                return true;
            case R.id.bug:
                Toast.makeText(this, "Found a bug? let us know at at nobodyfuckincares@gaymail.com", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_absolutely_nothing:
                Toast.makeText(this, "Version 1.0.0", Toast.LENGTH_LONG).show();
                return true;
            case R.id.logout:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setShareActionIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        GetReservationsTask okHttpHandler = new GetReservationsTask();
        okHttpHandler.execute(uri);
    }

    public void Show(View view) {
        EditText room = findViewById(R.id.roomId);
        EditText date = findViewById(R.id.dateId);
        String roomId = room.getText().toString();
        String dateId = date.getText().toString();
        GetReservationsTask okHttpHandler = new GetReservationsTask();
        okHttpHandler.execute(" https://anbo-roomreservation.azurewebsites.net/api/reservations/room/"+roomId+"/date/"+dateId);
    }

    public void addReservationButtonClicked(View view) {
        Intent intent = new Intent(this, AddReservationActivity.class);
        startActivity(intent);
    }

    public void ShowReservationButtonClicked(View view) {
        Intent intent = new Intent(this, ShowReservationActivity.class);
        startActivity(intent);
    }

    private class GetReservationsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Request.Builder builder = new Request.Builder();
            builder.url(params[0]);
            Request request = builder.build();

            try {
                //OkHttpClient client = new OkHttpClient();
                // https://stackoverflow.com/questions/25953819/how-to-set-connection-timeout-with-okhttp
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
                Response response = client.newCall(request).execute();
                String jsonString = response.body().string();
                Log.d(CommonStuff.TAG, jsonString);
                return jsonString;
            } catch (Exception e) {
                Log.d("SHIT", e.getMessage());
                cancel(true);
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String jsonString) {
            Gson gson = new GsonBuilder().create();
            final Reservation[] reservations = gson.fromJson(jsonString, Reservation[].class);
            Log.d(CommonStuff.TAG, Arrays.toString(reservations));
            ArrayAdapter<Reservation> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, reservations);

            ListView listView = findViewById(R.id.mainListView);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Reservation reservation = (Reservation) adapterView.getItemAtPosition(i);
                    Intent intent = new Intent(getBaseContext(), SingleReservationActivity.class);
                    intent.putExtra(SingleReservationActivity.RESERVATION, reservation);
                    startActivity(intent);
                }
            });
        }

        @Override
        protected void onCancelled(String message) {
            messageView.setText(message);
        }
    }
}