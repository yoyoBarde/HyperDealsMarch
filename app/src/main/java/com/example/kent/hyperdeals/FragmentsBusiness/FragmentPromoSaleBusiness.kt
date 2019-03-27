package com.example.kent.hyperdeals.FragmentsBusiness

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kent.hyperdeals.FragmentActivities.DialogFragmentAddPromoItem
import com.example.kent.hyperdeals.MainActivity
import com.example.kent.hyperdeals.Model.myInterfacesAddItem
import com.example.kent.hyperdeals.Model.promoItem
import com.example.kent.hyperdeals.Model.promoItemParcelable
import com.example.kent.hyperdeals.MyAdapters.PromoItemAdapterBusinessman
import com.example.kent.hyperdeals.MyAdapters.PromoListAdapter
import com.example.kent.hyperdeals.R
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_fragment_promo_sale_business.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.toast


class FragmentPromoSaleBusiness : Fragment(),myInterfacesAddItem {
    companion object {
        lateinit var promoItemListParcelable:ArrayList<promoItemParcelable>
    }
    var database = FirebaseFirestore.getInstance()

    val TAG = "FragmentPromoSale"
    lateinit var recyclerPromoSale:RecyclerView
    lateinit var myAdapter:PromoItemAdapterBusinessman

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_promo_sale_business, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPromoItem()
        if (MainActivity.userLog){
            btn_addItem.visibility = View.GONE

        }
        recyclerPromoSale = activity!!.findViewById(R.id.recylerviwSellItem) as RecyclerView



        btn_addItem.setOnClickListener {

        var myDialog = DialogFragmentAddPromoItem().newInstance()

        myDialog.show(activity!!.fragmentManager,"myCustomDialog")

    }
    }



    override fun addPromoItemBusiness(myPromoItemParseList:ArrayList<promoItemParcelable>) {

        setAdapter(myPromoItemParseList)
 }


    fun setAdapter (myPromoItemParseList: ArrayList<promoItemParcelable>){
        Log.e(TAG," interface size ${myPromoItemParseList.size}")
//        var myAdapter = PromoItemAdapterBusinessman(activity!!,myPromoItemParseList)
//        recyclerPromoSale.layoutManager = GridLayoutManager(activity!!,2)
//        recyclerPromoSale.adapter = myAdapter
    }
    fun getPromoItemUpdate(){

        database.collection("StoreItems").document(PromoListAdapter.promoProfile.promoStore).collection("PromoItems").addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                    if (e != null) {
                        Log.w(TAG, "listen:error", e)
                        return@EventListener
                    }

                    for (dc in snapshots!!.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            var uploaded = dc.document.toObject(promoItemParcelable::class.java)
                       var myAdapterer = PromoItemAdapterBusinessman(activity!!,promoItemListParcelable)
                            recylerviwSellItem.layoutManager = GridLayoutManager(activity!!,2)
                            recylerviwSellItem.adapter = myAdapterer
                            Log.e(TAG, "New Item: ${dc.document.data} ${uploaded.itemName}")

                        }
                    }
                })

    }
    fun getPromoItem() {


         promoItemListParcelable = ArrayList<promoItemParcelable>()
        doAsync {

            database.collection("StoreItems").document(PromoListAdapter.promoProfile.promoStore).collection("PromoItems").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (DocumentSnapshot in task.result) {
                        var uploaded = DocumentSnapshot.toObject(promoItemParcelable::class.java)
                            promoItemListParcelable.add(uploaded)

                    }

                    Log.e(TAG,"Retrieved ${promoItemListParcelable.size}")

                    getPromoItemUpdate()



                } else
                    toast("error")


            }


        }
    }
    }




