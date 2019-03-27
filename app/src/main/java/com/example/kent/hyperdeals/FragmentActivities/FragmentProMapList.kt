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
import com.example.kent.hyperdeals.Model.PromoModelBusinessman

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
    }

    }
