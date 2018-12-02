package com.lmao.gayyy.roomreservation;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SingleReservationActivity extends AppCompatActivity {

    public static final String RESERVATION = "reservation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_reservation);
        Intent intent = getIntent();
        Reservation reservation = (Reservation) intent.getSerializableExtra(RESERVATION);
        Log.d(CommonStuff.TAG, reservation.toString());

        TextView userIdView = findViewById(R.id.singleReservation_userId_textview);
        userIdView.setText("user: " + reservation.getUserId());

        TextView roomIdView = findViewById(R.id.singleReservation_roomId_textview);
        roomIdView.setText("room: " + reservation.getRoomId());

        TextView fromTimeView = findViewById(R.id.singleReservation_fromTime_textview);
        fromTimeView.setText("from: " + reservation.getFromString());

        TextView toTimeView = findViewById(R.id.singleReservation_toTime_textview);
        toTimeView.setText("to: " + reservation.getToString());

        TextView purposeView = findViewById(R.id.singleReservation_purpose_textview);
        purposeView.setText("purpose: " + reservation.getPurpose());
    }

    public void singleReservation_delete_button(View view) {
        Intent intent = getIntent();
        Reservation reservation = (Reservation) intent.getSerializableExtra(RESERVATION);

        int ReservationID = reservation.getId();

        SingleReservationActivity.DeleteReservationsTask task = new SingleReservationActivity.DeleteReservationsTask();
        task.execute("https://anbo-roomreservation.azurewebsites.net/api/reservations/" + ReservationID);
        finish();
    }

    public void cancelButtonClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private class DeleteReservationsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(params[0])
                    .delete()
                    .build();
            try (Response response = client.newCall(request).execute();) {
                return response.body().string();

            } catch (IOException ex) {
                Log.e(CommonStuff.TAG, ex.getMessage());
                return ex.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(CommonStuff.TAG, "DELETE SHIT");
        }
    }
}