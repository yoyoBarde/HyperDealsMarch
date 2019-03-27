package com.example.kent.hyperdeals.FragmentsBusiness

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kent.hyperdeals.LoginActivity
import com.example.kent.hyperdeals.Model.userLike
import com.example.kent.hyperdeals.Model.userLikeParce
import com.example.kent.hyperdeals.Model.userPromoiked
import com.example.kent.hyperdeals.MyAdapters.PromoListAdapter
import com.example.kent.hyperdeals.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_fragment_promp_details_business.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception


class FragmentPrompDetailsBusiness : Fragment() {
val TAG =" FragmentPrompDe"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_promp_details_business, container, false)







    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var database = FirebaseFirestore.getInstance()
var likeRetrieved = false

        var endDate = getMonthInwords(PromoListAdapter.promoProfile.endDateMonth)
        if (getMonthInwords(PromoListAdapter.promoProfile.startDateMonth)==getMonthInwords(PromoListAdapter.promoProfile.endDateMonth))
        {
            endDate= ""
        }
        promoDuration.text = getMonthInwords(PromoListAdapter.promoProfile.startDateMonth)+" ${PromoListAdapter.promoProfile.startDateDay} until ${endDate} ${PromoListAdapter.promoProfile.endDateDay}"
        tv_promoTitle.text = PromoListAdapter.promoProfile.promoname
        tv_promoStore.text = PromoListAdapter.promoProfile.promoStore
        tv_promoPlace.text = PromoListAdapter.promoProfile.promoPlace
        tv_promoDescription.text = PromoListAdapter.promoProfile.promodescription
        tv_promoContect.text = PromoListAdapter.promoProfile.promoContactNumber
        iv_call!!.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" +PromoListAdapter.promoProfile.promoContactNumber))
            startActivity(callIntent)
        }

      iv_map.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q="+PromoListAdapter.promoProfile.promoLatLng)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.`package` = "com.google.android.apps.maps"
            startActivity(mapIntent)
        }



        database.collection("PromoIntrested").document(PromoListAdapter.promoProfile.promoStore).
                collection("interested_users").document(LoginActivity.userUIDS).get().addOnSuccessListener {document -> Log.e(TAG,"Naa")

            if(document.exists())
            {

                var userLikeParce = document.toObject(userLikeParce::class.java)
                likeRetrieved =true
                try {
                    iv_thumbsup.setImageResource(R.drawable.ic_liked_k)

                }
                catch (e:Exception){

                }
                textView18.text = " "
            }
            else{
                Log.e(TAG,"dont exist")
                likeRetrieved =false

            }

        }.addOnFailureListener {

            Log.e(TAG,"WALA")
        }

        iv_thumbsup.setOnClickListener {
            doAsync {


                if (!likeRetrieved) {
                 uiThread {     iv_thumbsup.setImageResource(R.drawable.ic_liked_k)
                     textView18.text = " "

                 }
                    var myUserLike = userLike(LoginActivity.userUIDS, true)
                    database.collection("PromoIntrested").document(PromoListAdapter.promoProfile.promoStore).collection("interested_users").document(LoginActivity.userUIDS).set(myUserLike).addOnCompleteListener {
                        Log.e(TAG, "liked")
                        likeRetrieved = true


                        database.collection("UserLikes").document(LoginActivity.userUIDS).collection("Promo").document(PromoListAdapter.promoProfile.promoStore).set(userPromoiked(PromoListAdapter.promoProfile.promoStore))
                    }

                } else {
                   uiThread {   iv_thumbsup.setImageResource(R.drawable.interested)}
                    var myUserLike = userLike(LoginActivity.userUIDS, true)
                    database.collection("PromoIntrested").document(PromoListAdapter.promoProfile.promoStore).collection("interested_users").document(LoginActivity.userUIDS).delete().addOnCompleteListener {
                        Log.e(TAG, "deleted")
                        likeRetrieved = false

                        database.collection("UserLikes").document(LoginActivity.userUIDS).collection("Promo").document(PromoListAdapter.promoProfile.promoStore).delete().addOnCompleteListener {
                            Log.e(TAG, "deleted")


                        }


                    }


                }

            }
        }



    }


    fun getMonthInwords(month:Int):String{
    var detectedMonth = " "
    if(month==1){
        detectedMonth = "January"
    }
    else if(month==2){
detectedMonth = "February"
    }
    else if(month==3){
detectedMonth =  "March"
    }
    else if(month==4){
detectedMonth = "April"
    }
    else if(month==5){
detectedMonth = "May"
    }
    else if(month==6){
detectedMonth = "June"
    }
    else if(month==7){
detectedMonth = "July"
    }
    else if(month==8){
detectedMonth = "August"
    }
    else if(month==9){
detectedMonth = "September"
    }
    else if(month==10){
detectedMonth = "October"
    }
    else if(month==11){
detectedMonth = "November"
    }
    else if(month==12){
detectedMonth = "December"
    }




        return detectedMonth
    }



}
