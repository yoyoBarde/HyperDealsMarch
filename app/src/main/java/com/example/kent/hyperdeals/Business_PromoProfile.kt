package com.example.kent.hyperdeals

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.util.Log
import com.example.kent.hyperdeals.FragmentsBusiness.FragmentDashboardBusiness
import com.example.kent.hyperdeals.FragmentsBusiness.FragmentPromoSaleBusiness
import com.example.kent.hyperdeals.FragmentsBusiness.FragmentPrompDetailsBusiness

class Business_PromoProfile : AppCompatActivity() {
val TAG = "Business_promoProfile"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business__promo_profile)

        val viewPager   = findViewById<ViewPager>(R.id.viewPagerBusinessman)
        val tabLayout   = findViewById<TabLayout>(R.id.tabLayout)

        tabLayout.setupWithViewPager(viewPager)
        val adapter = com.example.kent.hyperdeals.MyAdapters.PagerAdapter(supportFragmentManager)
        adapter.addFragments(FragmentPrompDetailsBusiness(),"Details")
        adapter.addFragments(FragmentPromoSaleBusiness(),"Items")
        adapter.addFragments(FragmentDashboardBusiness(),"Dashboard")
        viewPager.adapter = adapter
//
//
//        Log.e(TAG,"GAGO kaba yoyo barde")
//        Log.e(TAG,"extra rice "+intent.extras.getString("key"))
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        Log.e(TAG,"resultCode $resultCode "+data!!.getStringExtra("key"))

        super.onActivityResult(requestCode, resultCode, data)


    }
}
