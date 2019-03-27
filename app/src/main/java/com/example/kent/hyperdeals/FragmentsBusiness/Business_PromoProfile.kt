package com.example.kent.hyperdeals.FragmentsBusiness

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.util.Log
import com.example.kent.hyperdeals.MainActivity
import com.example.kent.hyperdeals.Model.PromoModel
import com.example.kent.hyperdeals.MyAdapters.PromoListAdapter
import com.example.kent.hyperdeals.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_business__promo_profile.*
import java.lang.Exception

class Business_PromoProfile : AppCompatActivity() {
val TAG = "Business_promoProfile"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business__promo_profile)
        try {


            var atay = intent.getParcelableExtra<PromoModel>("object")
            Log.e(TAG, "get intent ${atay.promoStore}")
            PromoListAdapter.promoProfile = atay
        }
        catch (e:Exception){
            print(e)
        }

        buttonBack.setOnClickListener { finish()}
        Picasso.get()
                .load(PromoListAdapter.promoProfile.promoImageLink)
                .placeholder(R.drawable.hyperdealslogofinal)
                .into(iv_promo_image)

        val viewPager   = findViewById<ViewPager>(R.id.viewPagerBusinessman)
        val tabLayout   = findViewById<TabLayout>(R.id.tabLayout)

        tabLayout.setupWithViewPager(viewPager)
        val adapter = com.example.kent.hyperdeals.MyAdapters.PagerAdapter(supportFragmentManager)
        adapter.addFragments(FragmentPrompDetailsBusiness(),"Details")
        adapter.addFragments(FragmentPromoSaleBusiness(),"Items")

        if(MainActivity.userLog){

            adapter.addFragments(FragmentRelatedPromos(),"Similar Promos")

        }
        else{
            adapter.addFragments(FragmentDashboardBusiness(),"Dashboard")

        }
        viewPager.adapter = adapter

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        Log.e(TAG,"resultCode $resultCode "+data!!.getStringExtra("key"))

        super.onActivityResult(requestCode, resultCode, data)


    }
}
