package com.example.kent.hyperdeals.MyAdapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kent.hyperdeals.Model.SubcategoryParse
import com.example.kent.hyperdeals.R
import kotlinx.android.synthetic.main.model_subcategory_businessman.view.*
import org.jetbrains.anko.textColorResource


class SubcategoryAdapterBusiness (var context: Context, var subcategoryList : ArrayList<SubcategoryParse>, var myPosition:Int) : RecyclerView.Adapter<SubcategoryAdapterBusiness.ViewHolder>(){
    var myList = subcategoryList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.model_subcategory_businessman,parent,false))

    override fun getItemCount(): Int = subcategoryList.size


    override fun onBindViewHolder(holder: SubcategoryAdapterBusiness.ViewHolder, position: Int) {
        val mysubCategory = subcategoryList[position]

        holder.btn_subcat.text = mysubCategory.SubcategoryName


        if(mysubCategory.itemSelected)
        {
            holder.btn_subcat.setBackgroundResource(R.drawable.subcategory_shape_selected)
            holder.btn_subcat.setTextColor(context.resources.getColor(R.color.white))

        }

        holder.btn_subcat.setOnClickListener {
            if(mysubCategory.itemSelected){
                subcategoryList[position].itemSelected = false
                CategoryAdapterBusiness.globalCategoryList[myPosition].Subcategories[position].itemSelected = false
                holder.btn_subcat.setBackgroundResource(R.drawable.subcategory_shape)
                holder.btn_subcat.setTextColor(context.resources.getColor(R.color.colorPrimaryDark))


            }
            else{
                subcategoryList[position].itemSelected = true
                CategoryAdapterBusiness.globalCategoryList[myPosition].Subcategories[position].itemSelected = true

                holder.btn_subcat.setBackgroundResource(R.drawable.subcategory_shape_selected)
                holder.btn_subcat.setTextColor(context.resources.getColor(R.color.white))

            }


        }


    }




    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
     val btn_subcat = view.btn_subcategory
    }


}