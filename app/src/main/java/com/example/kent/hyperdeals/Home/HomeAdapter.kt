package com.example.kent.hyperdeals.Home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.kent.hyperdeals.MyAdapters.PromoModel
import com.example.kent.hyperdeals.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.zzhomelistitem.view.*
import org.jetbrains.anko.layoutInflater

class HomeAdapter( private val context:Context, private val selectedItem: SparseBooleanArray, private val promolist : ArrayList<PromoModel>) : RecyclerView.Adapter<HomeAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            HomeAdapter.ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.zzhomelistitem,parent,false))

    override fun getItemCount(): Int  = promolist.size



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val promos = promolist[position]


        Picasso.get()
                .load(promos.promoImageLink)
                .placeholder(R.drawable.hyperdealslogofinal)
                .into(holder.ivHomeImage)

        holder.tvHomeText.text = promos.promoname
        holder.tvHomeStore.text = promos.promoStore
        holder.tv_distance.text = promos.distance +" KM"

    }


    inner class ViewHolder (view: View): RecyclerView.ViewHolder(view){
        val ivHomeImage = view.homePromoImage!!
        val tvHomeText = view.homePromoName!!
        val tvHomeStore = view.homePromoStore!!
        val tv_distance = view.tv_distance!!
    }


    fun showDialog(position:Int) {


        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = context.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialogbox, null)

        dialogBuilder.setCancelable(false)

        dialogBuilder.setView(dialogView)


        val interested = dialogView.findViewById(R.id.interested) as ImageView
        val call = dialogView.findViewById(R.id.call) as ImageView
        val showNavigation = dialogView.findViewById(R.id.map) as ImageView


        val b = dialogBuilder.create()
        b.show()

        interested.setOnClickListener {  }
        call.setOnClickListener {

       var intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${promolist[position].promoContactNumber}")
            context.startActivity(intent)

        }
        showNavigation.setOnClickListener {

            val gmmIntentUri = Uri.parse("google.navigation:q="+ promolist[position].promoLatLng)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.`package` = "com.google.android.apps.maps"
            context.startActivity(mapIntent)
        }
    }

}