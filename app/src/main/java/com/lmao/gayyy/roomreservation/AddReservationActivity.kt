package com.lmao.gayyy.roomreservation

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView

import org.json.JSONException
import org.json.JSONObject

import java.io.IOException

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

class AddReservationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reservation)
    }

    fun addReservationButtonClicked(view: View) {
        val userIdField = findViewById<EditText>(R.id.addReservation_userId_edittext)
        val roomIdField = findViewById<EditText>(R.id.addReservation_roomId_edittext)
        val fromTimeField = findViewById<EditText>(R.id.addReservation_fromTime_edittext)
        val toTimeField = findViewById<EditText>(R.id.addReservation_toTime_edittext)
        val purposeField = findViewById<EditText>(R.id.addReservation_purpose_edittext)

        val userId = userIdField.text.toString()
        val roomId = roomIdField.text.toString()
        val fromTime = fromTimeField.text.toString()
        val toTime = toTimeField.text.toString()
        val purpose = purposeField.text.toString()

        val messageView = findViewById<TextView>(R.id.addReservation_message_textview)

        try { // create JSON document
            val jsonObject = JSONObject()
            jsonObject.put("userId", userId)
            jsonObject.put("roomId", roomId)
            jsonObject.put("fromTimeString", fromTime)
            jsonObject.put("toTimeString", toTime)
            jsonObject.put("purpose", purpose)
            val jsonDocument = jsonObject.toString()
            messageView.text = jsonDocument
            val task = AddReservationTask()
            task.execute("https://anbo-roomreservation.azurewebsites.net/api/reservations", jsonDocument)
        } catch (ex: JSONException) {
            messageView.text = ex.message
        }

    }

    private fun done() {
        finish()
    }

    fun cancelButtonClicked(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private inner class AddReservationTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg strings: String): String? {
            val uri = strings[0]
            val jsonString = strings[1]
            val client = OkHttpClient()
            val body = RequestBody.create(JSON_MEDIA_TYPE, jsonString)
            val request = Request.Builder()
                    .url(uri)
                    .post(body)
                    .build()
            try {
                client.newCall(request).execute().use { response -> return response.body()!!.string() }
            } catch (ex: IOException) {
                Log.e(CommonStuff.TAG, ex.message)
                return ex.message
            }

        }

        override fun onPostExecute(s: String) {
            Log.d(CommonStuff.TAG, "Reservation added")
            done()
        }

        override fun onCancelled(s: String) {
            Log.d(CommonStuff.TAG, "Problem: Reservation add")
        }
    }

    companion object {
        private val JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8")
    }
}