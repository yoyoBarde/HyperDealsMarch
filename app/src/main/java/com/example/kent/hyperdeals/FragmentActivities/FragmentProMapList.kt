package com.example.kent.hyperdeals.FragmentActivities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.util.SparseBooleanArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kent.hyperdeals.FragmentsBusiness.Business_PromoProfile
import com.example.kent.hyperdeals.MyAdapters.PromoListAdapter
import com.example.kent.hyperdeals.Model.PromoModel
import com.example.kent.hyperdeals.MyAdapters.PromoModelBusinessman

import com.example.kent.hyperdeals.Interface.RecyclerTouchListener
import com.example.kent.hyperdeals.LoginActivity
import com.example.kent.hyperdeals.Model.*
import com.example.kent.hyperdeals.R
import com.google.firebase.firestore.*
import com.google.firebase.firestore.Transaction
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialogbox.*
import kotlinx.android.synthetic.main.fragmentpromaplist.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*

class FragmentProMapList: Fragment() {



    private var myDialog: Dialog? = null

    private var promolist = ArrayList<PromoModel>()
    private var promolist1= ArrayList<PromoModelBusinessman>()
    private var mAdapter : PromoListAdapter? = null
    private var mSelected: SparseBooleanArray = SparseBooleanArray()
    private var mFirebaseFirestore = FirebaseFirestore.getInstance()


    val KEY_SENT = "sent"
    val KEY_VIEWED= "viewed"
    val KEY_INTERESTED = "interested"

    var TAG = "Hyperdeals"

    companion object {

        const val KEY = "asdad"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragmentpromaplist,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        promolist = ArrayList()

        val database = FirebaseFirestore.getInstance()

        val layoutManager = LinearLayoutManager(context)
        recyclerViewProMapList.layoutManager = layoutManager
        recyclerViewProMapList.itemAnimator = DefaultItemAnimator()


        database.collection("PromoDetails").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (DocumentSnapshot in task.result) {
                    val upload = DocumentSnapshot.toObject(PromoModel::class.java)
                    Log.d(TAG, DocumentSnapshot.getId() + " => " + DocumentSnapshot.getData())
                    promolist.add(upload)
                    toast("success")

                    mAdapter = PromoListAdapter(activity!!,mSelected, promolist)
                    recyclerViewProMapList.adapter = mAdapter

                }

            } else
                toast("error")
        }

      /*   val email: String
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        var documentReference: DocumentReference = db.collection("EmailUID").document(email)
        for ()*/
fun incrementPromos(myModel: PromoModel){

          var  dateFormat = SimpleDateFormat("dd/yyyy")
          Log.e(TAG," current date $ ${dateFormat.format(Calendar.getInstance().time)}")
          database.collection("PromoViews").document(myModel.promoStore).collection("UserViews").document(LoginActivity.userUIDS).get().addOnSuccessListener {
              document ->
              if(document.exists()){
                  var userViewParce = document.toObject(userPromoViewsParce::class.java)

                  if(userViewParce.date.equals(dateFormat.format(Calendar.getInstance().time))){
                      Log.e(TAG,"dateMatched no Action")
                  }
                  else {
                      doAsync {

                          database.collection("PromoViews").document(myModel.promoStore).collection("UserViews").document(LoginActivity.userUIDS)
                                  .set(userPromoViews(dateFormat.format(Calendar.getInstance().time)))
                          var mPromoViews = promoViewsParde()

                          database.collection("PromoViews").document(myModel.promoStore)
                                  .get().addOnSuccessListener { document ->

                                      if (document.exists()) {
                                          mPromoViews = document.toObject(promoViewsParde::class.java)
                                          Log.e(TAG, "promo views get ${mPromoViews.promoViews}")
                                          var fPromoViews = promoViews(mPromoViews.promoViews + 1)
                                          database.collection("PromoViews").document(myModel.promoStore).set(fPromoViews)
                                      }
                                      else{
                                          var fPromoViews = promoViews(mPromoViews.promoViews + 1)
                                          database.collection("PromoViews").document(myModel.promoStore).set(fPromoViews)
                                      }
                                  }


                      }
                  }

              }
              else{
                  doAsync {
                      database.collection("PromoViews").document(myModel.promoStore).collection("UserViews").document(LoginActivity.userUIDS)
                              .set(userPromoViews(dateFormat.format(Calendar.getInstance().time)))
                      var mPromoViews = promoViewsParde()
                      database.collection("PromoViews").document(myModel.promoStore)
                              .get().addOnSuccessListener { document ->

                                  if (document.exists()) {
                                      mPromoViews = document.toObject(promoViewsParde::class.java)
                                      Log.e(TAG, "promo views get ${mPromoViews.promoViews}")
                                      var fPromoViews = promoViews(mPromoViews.promoViews + 1)
                                      database.collection("PromoViews").document(myModel.promoStore).set(fPromoViews)
                                  }
                                  else{
                                      var fPromoViews = promoViews(mPromoViews.promoViews + 1)
                                      database.collection("PromoViews").document(myModel.promoStore).set(fPromoViews)
                                  }
                              }


                  }
              }

          }


      }
        recyclerViewProMapList.addOnItemTouchListener(RecyclerTouchListener(this.context!!.applicationContext, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View, position: Int) {

                val fragmentContainer = specificPromoContainer
                val bundle = Bundle()
                val promos = promolist[position]

                val transaction : Transaction?=null

                val db = FirebaseFirestore.getInstance()
                val promoDetailsReference = db.collection("PromoDetails").document(promos.promoStore)

                db.runTransaction({
                    val snapshot = it.get(promoDetailsReference)
                    val updatedView = snapshot.getDouble(KEY_SENT) + 1
                    it.update(promoDetailsReference,KEY_SENT,updatedView)

                    //PLEASE CHECK IF NIGANA BA NI - KENT MODIFIED
                 /*   Handler().postDelayed({
                    }, 5000)
*/


                }).addOnSuccessListener {
                    toast("Nice")


                }.addOnFailureListener{
                    toast("Failed")
                }

                db.runTransaction({
                    val snapshott = it.get(promoDetailsReference)
                    val updatedVieww = snapshott.getDouble(KEY_VIEWED) + 1
                    it.update(promoDetailsReference,KEY_VIEWED,updatedVieww)


                }).addOnSuccessListener {
                    toast("Nice")
                }.addOnFailureListener{
                    toast("Failed")
                }





           /*     mFirebaseFirestore.collection("PromoDetails").document(promos.promoStore).update(KEY_VIEWED,promos.viewed+5)
                toast("Nice")*/



                myDialog = Dialog(activity)
                myDialog?.setContentView(R.layout.dialogbox)


                Picasso.get()
                        .load(promos.promoImageLink)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(myDialog!!.promoPicture)

                myDialog!!.specificPromoContainer.setOnClickListener {
                incrementPromos(promolist[position])






                        myDialog!!.dismiss()
                    val intent = Intent(activity!!, Business_PromoProfile::class.java)
                    PromoListAdapter.promoProfile = promolist[position]
                            startActivity(intent)



                }





                myDialog!!.promoNAME.text = promolist[position].promoname
                myDialog!!.promoDESCRIPTION.text = promolist[position].promodescription
                myDialog!!.promoLOCATION.text = promolist[position].promoPlace
                myDialog!!.promoNUMBER.text = promolist[position].promoContactNumber
                myDialog!!.promoSTORE.text = promolist[position].promoStore

var likeRetrieved = false
                database.collection("PromoIntrested").document(promolist[position].promoStore).
                        collection("interested_users").document(LoginActivity.userUIDS).get().addOnSuccessListener { document -> Log.e(TAG,"Naa")

                    if(document.exists())
                    {

                        var userLikeParce = document.toObject(userLikeParce::class.java)
                        likeRetrieved =true

                        myDialog!!.interested.setImageResource(R.drawable.ic_liked_k)
                    }
                    else{
                        Log.e(TAG,"dont exist")
                        likeRetrieved =false

                    }

                }.addOnFailureListener {

                    Log.e(TAG,"WALA")
                }
                myDialog!!.interested.setOnClickListener {
                    doAsync {


                        if (!likeRetrieved) {
                            uiThread {     myDialog!!.interested.setImageResource(R.drawable.ic_liked_k)}
                            var myUserLike = userLike(LoginActivity.userUIDS, true)
                            database.collection("PromoIntrested").document(promolist[position].promoStore).collection("interested_users").document(LoginActivity.userUIDS).set(myUserLike).addOnCompleteListener {
                                Log.e(TAG, "liked")
                                likeRetrieved = true
                                database.collection("UserLikes").document(LoginActivity.userUIDS).collection("Promo").document(promolist[position].promoStore).set(userPromoiked(promolist[position].promoStore))

                            }

                        } else {
                            uiThread {   myDialog!!.interested.setImageResource(R.drawable.interested)}
                            database.collection("PromoIntrested").document(promolist[position].promoStore).collection("interested_users").document(LoginActivity.userUIDS).delete().addOnCompleteListener {
                                Log.e(TAG, "deleted")
                                likeRetrieved = false
                                    database.collection("UserLikes").document(LoginActivity.userUIDS).collection("Promo").document(promolist[position].promoStore).delete()

                            }


                        }

                    }



                }

                //Inflating of images in Dialog builder: Work in Progress Kent

           /*   myDialog!!.promoPicture.set(promolist[position].promoImageLink)*/

              /*  myDialog?.close!!.setOnClickListener {
                    myDialog?.dismiss()

                }
*/

                myDialog?.call!!.setOnClickListener {
                    val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + promolist[position].promoContactNumber))
                    startActivity(callIntent)
                }

                    myDialog?.map?.setOnClickListener {
                        val gmmIntentUri = Uri.parse("google.navigation:q="+ promolist[position].promoLatLng)
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.`package` = "com.google.android.apps.maps"
                        startActivity(mapIntent)
                    }



                            myDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            myDialog?.window?.setGravity(Gravity.CENTER)
                            myDialog?.show()

                            /*   myDialog?.close!!.setOnClickListener {
                    myDialog?.dismiss()

                }

                myDialog?.call!!.setOnClickListener{
                    val callIntent = Intent (Intent.ACTION_CALL, Uri.parse("tel:" + "09154638674"))
                    startActivity(callIntent)
                }


                myDialog?.map?.setOnClickListener{
                    val gmmIntentUri = Uri.parse("google.navigation:q=10.2804,123.8818")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.`package` = "com.google.android.apps.maps"
                    startActivity(mapIntent)
                }

                myDialog?.interested?.setOnClickListener{
                    Toast.makeText(activity,"Thank you for your feedback.", Toast.LENGTH_SHORT).show()
                    myDialog?.dismiss()

                }

                myDialog?.window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT))
                myDialog?.window?.setGravity(Gravity.CENTER)
                myDialog?.show()


            }


        }))*/

                        }


    }))
    }

    }
