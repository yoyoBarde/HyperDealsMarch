package com.example.kent.hyperdeals.FragmentActivities

import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import com.example.kent.hyperdeals.BusinessActivities.AddPromo
import com.example.kent.hyperdeals.BusinessActivities.AddStore
import com.example.kent.hyperdeals.MyAdapters.CategoryAdapterBusiness
import com.example.kent.hyperdeals.R
import com.example.kent.hyperdeals.Model.myInterfaces
import kotlinx.android.synthetic.main.activity_dialog_fragment_add_category_business.*


class DialogFragmentAddCategoryBusiness : DialogFragment() {

    var myAdapter:CategoryAdapterBusiness?=null
val TAG = "DialogFragment"
    fun newInstance(): DialogFragmentAddCategoryBusiness {
        return DialogFragmentAddCategoryBusiness()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {




        return inflater.inflate(R.layout.activity_dialog_fragment_add_category_business, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)


        for(i in 0 until AddPromo.globalCategorylist.size){

     Log.e(TAG,"${AddPromo.globalCategorylist[i].Subcategories.size} - ${AddPromo.globalCategorylist[i].categoryName}")

        }

                myAdapter = CategoryAdapterBusiness(activity!!,AddPromo.globalCategorylist)

                recyclerViewCategories.layoutManager = LinearLayoutManager(activity!!)
                recyclerViewCategories.adapter = myAdapter
                iv_close.setOnClickListener {
                    dismiss()

                }
        btn_set_category_business.setOnClickListener {


            if(AddStore.Store){
                var myInterface =  AddStore() as (myInterfaces)
                myInterface.saveCategoriesBusiness(CategoryAdapterBusiness.globalCategoryList)

            }
            else {
                var myInterface =  AddPromo() as (myInterfaces)
                myInterface.saveCategoriesBusiness(CategoryAdapterBusiness.globalCategoryList)
            }
                dismiss()

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Sample)
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
         //   dialog.window!!.setGravity(Gravity.BOTTOM)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.white )
        }
    }

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog.window!!
                .attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setBackgroundDrawableResource(R.color.black_alpha_80)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        //  dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        return dialog
    }





}



