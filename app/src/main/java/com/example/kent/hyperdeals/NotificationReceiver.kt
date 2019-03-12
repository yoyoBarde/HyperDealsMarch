package com.example.kent.hyperdeals
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class NotificationReceiver : BroadcastReceiver() {
  var TAG = "NotificationReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        Log.e("TAG", "gagoKaba")

        }
}
