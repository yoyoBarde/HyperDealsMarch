package com.example.kent.hyperdeals.NavigationOptionsActivity

import android.app.TimePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.TimePicker
import com.example.kent.hyperdeals.LoginActivity
import com.example.kent.hyperdeals.Model.userPreferredTime
import com.example.kent.hyperdeals.Model.userPreferredTimeParce
import com.example.kent.hyperdeals.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_preferred_time.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

class UserPreferredTime : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {
    var selected = 0
    val TAG = "UserPreferredTime"
    var startTimeHour =8
    var startTimeMinutes = 0
    var endTimeHour = 5
    var endTimeMinutes = 0
    var prefTimeEnabled:Boolean = false
    var database = FirebaseFirestore.getInstance()
    lateinit var userPrefer:userPreferredTimeParce
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_preferred_time)
        doAsync {
            database.collection("UserPreferredTime").document(LoginActivity.userUIDS).get().addOnSuccessListener { document ->

                if (document.exists()) {

                    var promoLikeCountParce = document.toObject(userPreferredTimeParce::class.java)
                    uiThread {             tv_startTime.text = "${promoLikeCountParce.startTimeHour}:${promoLikeCountParce.startTimeMinutes}"
                        tv_endTime.text = "${promoLikeCountParce.endTimeHour}:${promoLikeCountParce.endTimeMinutes}"

                    switch_on.isChecked = promoLikeCountParce.enabled
                    }
                    startTimeHour = promoLikeCountParce.startTimeHour
                    startTimeMinutes = promoLikeCountParce.startTimeMinutes
                    endTimeHour =  promoLikeCountParce.endTimeHour
                    endTimeMinutes = promoLikeCountParce.endTimeMinutes
                    userPrefer = promoLikeCountParce

                } else {
                    Log.e(TAG, "dont exist")
                }

            }
        }




        switch_on.setOnCheckedChangeListener { compoundButton, b ->

            prefTimeEnabled = b
            btn_set.visibility = View.VISIBLE
        }
        tv_startTime.setOnClickListener {
            selected  = 1
            var myTimepicker =  TimePickerDialog(this,this,8,0, DateFormat.is24HourFormat(this))
            myTimepicker.show()
        }
        tv_endTime.setOnClickListener {
            selected = 2
            var myTimepicker =  TimePickerDialog(this,this,5,0,DateFormat.is24HourFormat(this))
            myTimepicker.show()

        }

        btn_set.setOnClickListener {
            btn_set.visibility = View.GONE

          var userPrefTIme  =   userPreferredTime(startTimeHour,startTimeMinutes,endTimeHour,endTimeMinutes,prefTimeEnabled)
            database.collection("UserPreferredTime").document(LoginActivity.userUIDS).set(userPrefTIme)

        finish()

        }
    }


    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        btn_set.visibility = View.VISIBLE
        if(selected==1){
            var kk = Calendar.getInstance()
            kk.set(2019,3,27,p1,p2)
            startTimeHour = p1
            startTimeMinutes= p2
            Log.e(TAG,"${kk.time}   ${kk.timeInMillis}")
            tv_startTime.text = "${p1}:${p2}"

        }
        else if (selected==2){
            var kk = Calendar.getInstance()
            kk.set(2019,3,27,p1,p2)
            endTimeHour = p1
            endTimeMinutes = p2
            Log.e(TAG,"${kk.time}   ${kk.timeInMillis}")
            tv_endTime.text = "${p1}:${p2}"


        }

    }
}
