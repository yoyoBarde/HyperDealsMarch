package com.example.kent.hyperdeals.FragmentActivities

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RemoteViews
import com.example.kent.hyperdeals.*
import com.example.kent.hyperdeals.FragmentsBusiness.Business_PromoProfile
import com.example.kent.hyperdeals.Home.HomeAdapter
import com.example.kent.hyperdeals.Home.PreferedPromoAdapter
import com.example.kent.hyperdeals.Model.*
import com.example.kent.hyperdeals.MyAdapters.HottestPromoAdapter
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.internal.zzahn.runOnUiThread
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragmentcategory.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.uiThread
import java.lang.Exception
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class FragmentCategory: Fragment() {
    companion object {
        lateinit var userLatLng: LatLng
        lateinit var globalPromoList:ArrayList<PromoModel>
         var globalUserPreferredTime:userPreferredTimeParce?=null
    }
    var database = FirebaseFirestore.getInstance()

    var notifIDCounter = 102
    private var promolist1= ArrayList<PromoModelBusinessman>()
    private var mAdapter : HomeAdapter? = null
    private var mSelected: SparseBooleanArray = SparseBooleanArray()
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var mFirebaseFirestore = FirebaseFirestore.getInstance()
    private var promolist = ArrayList<PromoModel>()
    private var userCategories = ArrayList<CategoryParse>()

    lateinit var  geoFire:GeoFire
    lateinit var  ref:DatabaseReference
    lateinit var globalBitmap:Bitmap
    var currentDate = Calendar.getInstance()
    var minimize_pref = true
    var minimize_hottes = true
    var minimize_nearb = true
    var TAG = "FragmentCategory"
    var notificationList = arrayListOf<String>("Firstnull")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragmentcategory, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ref = FirebaseDatabase.getInstance().getReference("Geofences")
        geoFire = GeoFire(ref)
        getUserPreferredTime()
        getUserCategories()





        promolist = ArrayList()
        val layoutManager = LinearLayoutManager(context)
        my_recycler_view111.layoutManager = layoutManager
        my_recycler_view111.itemAnimator = DefaultItemAnimator()





  minimize_hottest.setOnClickListener {
      if(minimize_hottes){
          minimize_hottest.setImageResource(R.mipmap.ic_arrow_blue_right)

          minimize_hottes = false
          reclerView_hottest_promo.visibility = View.GONE
      }
      else{
          minimize_hottes = true
          reclerView_hottest_promo.visibility = View.VISIBLE
          Log.e(TAG,"show")
          minimize_hottest.setImageResource(R.mipmap.ic_arrow_blue_down)


      }

  }
  minimize_nearby.setOnClickListener {

      if(minimize_nearb){
          minimize_nearby.setImageResource(R.mipmap.ic_arrow_blue_right)
          minimize_nearb = false
          my_recycler_view111.visibility = View.GONE

      }
      else{
          minimize_nearb = true
          my_recycler_view111.visibility = View.VISIBLE
          minimize_nearby.setImageResource(R.mipmap.ic_arrow_blue_down)

        Log.e(TAG,"show")
      }

  }
  minimize_preferred.setOnClickListener {

      if(minimize_pref){
          minimize_pref = false
          recyclerview_prefered_promo.visibility = View.GONE
          minimize_preferred.setImageResource(R.mipmap.ic_arrow_blue_right)
      }
      else{
          Log.e(TAG,"show")

          minimize_pref = true
          recyclerview_prefered_promo.visibility = View.VISIBLE
          minimize_preferred.setImageResource(R.mipmap.ic_arrow_blue_down)


      }
  }

    }

    fun setPreferencePromoAdapter(preferPromoList:ArrayList<PromoModel>){
        Log.e(TAG,"setPreferedPromoAdapter")
        var myAdapter = PreferedPromoAdapter(activity!!,preferPromoList)
        recyclerview_prefered_promo.layoutManager = LinearLayoutManager(activity!!, LinearLayout.VERTICAL,false)
        recyclerview_prefered_promo.adapter = myAdapter
        minimize_preferred.setImageResource(R.mipmap.ic_arrow_blue_down)

    }




fun getPromos(){

    doAsync {
        database.collection("PromoDetails").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (DocumentSnapshot in task.result) {
                    var upload = DocumentSnapshot.toObject(PromoModel::class.java)
                    Log.d(TAG, DocumentSnapshot.getId() + " => " + DocumentSnapshot.getData())
                    var geoPoint = DocumentSnapshot.getGeoPoint("promoGeo")
                    upload.promoLocation = LatLng(geoPoint.latitude, geoPoint.longitude)
                    upload.startDateCalendar.set(upload.startDateYear, upload.startDateMonth - 1, upload.startDateDay)
                    upload.endDateCalendar.set(upload.endDateYear, upload.endDateMonth - 1, upload.endDateDay)


                    if (currentDate.timeInMillis <= upload.endDateCalendar.timeInMillis)
                    {
                        promolist.add(upload)

                }
                }
                getPreferenceMatched()



            } else
                toast("error")
        }



    }


}
fun setHomeAdapter(){

    var sortTedPromo = promolist.sortedWith(compareBy {it.distance})
    var finalList = ArrayList<PromoModel>()
    for(i in 0 until sortTedPromo.size){
        if(sortTedPromo[i].distance.toDouble()<10.0){
            finalList.add(sortTedPromo[i])
        }
    }
    mAdapter = HomeAdapter(activity!!,mSelected, finalList)
    my_recycler_view111.adapter = mAdapter
    minimize_nearby.setImageResource(R.mipmap.ic_arrow_blue_down)


}
    fun getLocation() {


        locationManager = activity!!.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                userLatLng = LatLng(location.latitude,location.longitude)
                Log.e(TAG,"Manufcaturer ${android.os.Build.MANUFACTURER}")
                    detectGeofence(GeoLocation(location.latitude, location.longitude))

                try {
                    for(i in 0 until  promolist.size){


                        var distanceFormatted = String.format("%.2f",CalculationByDistance(userLatLng,promolist[i].promoLocation))
                        promolist[i].distance = distanceFormatted


                    }

                    runOnUiThread {


                    setHomeAdapter()
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Exception raised $e")
                }
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

            }

            override fun onProviderEnabled(provider: String) {

            }

            override fun onProviderDisabled(provider: String) {

            }
        }
        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(
                            activity!!.applicationContext,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            activity!!.applicationContext,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)

            // locationManager.!!requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0f,);

        } else {
            if (ActivityCompat.checkSelfPermission(
                            activity!!.applicationContext,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            activity!!.applicationContext, Manifest.permission
                            .ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                return
            } else {
                //       locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
                // locationManager!!.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener);
                try {
                    locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)

                }
                catch (e:Exception){
                    Log.e(TAG,"LOL")
                }
                Log.e(TAG, "this part")
            }

        }
    }

    fun promoOnPreferredTime( myPromo:PromoModel):Boolean{
        var TAG = true
        if (globalUserPreferredTime!=null) {
             TAG = false
            var promoStart = Calendar.getInstance()
            var promoEnd = Calendar.getInstance()
            var userPrefStart = Calendar.getInstance()
            var userPrefEnd = Calendar.getInstance()
            promoStart.set(2019, 3, 27, myPromo.startTimeHour, myPromo.startTimeMinute)
            promoEnd.set(2019, 3, 27, myPromo.endTimeHour, myPromo.endTimeMinute)
            userPrefStart.set(2019, 3, 27, globalUserPreferredTime!!.startTimeHour, globalUserPreferredTime!!.startTimeMinutes)
            userPrefEnd.set(2019, 3, 27, globalUserPreferredTime!!.endTimeHour, globalUserPreferredTime!!.endTimeMinutes)
            if (globalUserPreferredTime!!.enabled) {
                TAG = (promoStart.timeInMillis >= userPrefStart.timeInMillis && promoStart.timeInMillis <= userPrefEnd.timeInMillis
                        || promoEnd.timeInMillis >= userPrefStart.timeInMillis && promoEnd.timeInMillis <= userPrefEnd.timeInMillis)
            } else {
                TAG = true
            }

        }
return TAG
    }
    fun detectGeofence(userGeo:GeoLocation) {
        Log.e(TAG,"detectGeofence")

        val geoQuery = geoFire.queryAtLocation(userGeo, .5)
        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onKeyEntered(key: String, location: GeoLocation) {
        Log.e(TAG,"atay na")
                if(!notificationList.contains(key)) {
                    notificationList.add(key)
                    Log.e(TAG, key)
                    notifIDCounter += 1

                    for(i in 0 until promolist.size) {
                        if(promolist[i].promoStore.matches(key.toRegex())){

                            doAsync {
                                try {
                                    var srt = java.net.URL(promolist[i].promoImageLink).openStream()
                                    var bitmap = BitmapFactory.decodeStream(srt)
                                    Log.e(TAG, "GAGO " + bitmap.toString())
                                    promolist[i].promoImageBitmap = bitmap

                                    uiThread {
                                        if(promolist[i].preferenceMatched!=0) {
                                            if (promoOnPreferredTime(promolist[i])) {
                                                Log.e(TAG,"onPreferredTime")

                                                if (android.os.Build.MANUFACTURER == "Allwinner") {
                                                    Log.e(TAG, "notification pushed ${promolist[i].promoStore}")

                                                } else {
                                                    Log.e(TAG, " else notification pushed ${promolist[i].promoStore}")
                                                    displayNotification(key, notifIDCounter, promolist[i])

                                                }
                                            }
                                            else{
                                                Log.e(TAG,"Not on preferred Time")
                                            }
                                            }
                                    }
                                    Log.e(TAG, "Ngeek")

                                } catch (e: Exception) {
                                    uiThread {
                                        if(android.os.Build.MANUFACTURER=="Allwinner") {
                                            Log.e(TAG,"notification pushed ${promolist[i].promoStore}")

                                        }
                                        else {
                                            displayNotification(key, notifIDCounter, promolist[i])
                                        }                                    }
                                    Log.e(TAG, "walakadetect")
                                }

                            }


                            Log.e(TAG,"matched "+key)

                        }
                    }

                }


            }

            override fun onKeyExited(key: String) {}

            override fun onKeyMoved(key: String, location: GeoLocation) {}

            override fun onGeoQueryReady() {

            }

            override fun onGeoQueryError(error: DatabaseError) {
                Log.d("ERROR", "" + error)
            }
        })
    }
    fun CalculationByDistance(StartP: LatLng, EndP: LatLng): Double {


        val Radius = 6371// radius of earth in Km
        val lat1 = StartP.latitude
        val lat2 = EndP.latitude
        val lon1 = StartP.longitude
        val lon2 = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2))
        val c = 2 * Math.asin(Math.sqrt(a))
        val valueResult = Radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec = Integer.valueOf(newFormat.format(meter))
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec)

        return Radius * c
    }
    fun displayNotification(Channel: String, notificationID: Int,myPromoModel:PromoModel) {
        var rand =  Random()
        var n = rand.nextInt(1000)
        var NotifcationID2 = Channel.length + n
        Log.e("Notification test", "Succeed")

        var resultIntent = Intent(activity!!, Business_PromoProfile::class.java)

        var actionIntent = Intent(activity!!,NotificationReceiver::class.java)
        val actionPendingIntent: PendingIntent = PendingIntent.getActivity(activity!!, NotifcationID2, actionIntent
                .putExtra("key",Channel)
                .putExtra("object",myPromoModel)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT),0)


        val normal_layout = RemoteViews(activity!!.packageName, R.layout.notification_fence_normal)
        val expanded_layout = RemoteViews(activity!!.packageName, R.layout.notification_fence_expanded)

        normal_layout.setTextViewText(R.id.tv_notifstore,myPromoModel.promoStore+" is having a promo")
        expanded_layout.setTextViewText(R.id.tv_notifstore2,myPromoModel.promoStore+" is having a promo")
        expanded_layout.setTextViewText(R.id.tv_notif_description,myPromoModel.promodescription)
        val resultPendingIntent = PendingIntent.getActivity(activity!!, NotifcationID2, resultIntent
                .putExtra("key", Channel)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT),0)
        try {

            expanded_layout.setImageViewBitmap(R.id.iv_notifpromoimage, myPromoModel.promoImageBitmap)
        }
        catch (e:Exception){

            print(e)
        }
        createNotificationChannel(Channel)

        val builder = NotificationCompat.Builder(activity!!, Channel)
        builder.setSmallIcon(R.drawable.hyperdealslogofinal)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
        builder.setCustomContentView(normal_layout)
        builder.setCustomBigContentView(expanded_layout)
        builder.setContentIntent(resultPendingIntent)
        builder.color = Color.BLUE
        builder.setOnlyAlertOnce(true)
        builder.addAction(R.drawable.hyperdealslogofinal,"Interested",actionPendingIntent)
        builder.addAction(R.drawable.hyperdealslogofinal,"Dismiss",actionPendingIntent)
        builder.setAutoCancel(true)
        val notificationManagerCompat = NotificationManagerCompat.from(activity!!)
        notificationManagerCompat.notify(NotifcationID2, builder.build())

    }
    @SuppressLint("NewApi")
    internal fun createNotificationChannel(Channel: String) {
        Log.e("CreateNotification", "Created")
        val name = "personal notification"
        val description = "include all notification"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(Channel, name, importance)
        channel.description = description
        val notificationManager = activity!!.getSystemService<NotificationManager>(NotificationManager::class.java)
        notificationManager!!.createNotificationChannel(channel)

    }
    fun getHottestPromo(){
        for( i in 0 until promolist.size){

            promolist[i].hottestPoints = (promolist[i].interested * 8) + (promolist[i].viewed * 5)
            Log.e(TAG,"${promolist[i].promoStore}  points ${promolist[i].hottestPoints}")
        }

        var hottestList = ArrayList<PromoModel>()
        var sortTedPromo = promolist.sortedWith(compareByDescending {it.hottestPoints})
        for(i in 0 until 5){
            Log.e(TAG,"${promolist[i].promoStore}  pointsSorted ${promolist[i].hottestPoints}")

            hottestList.add(sortTedPromo[i])
        }



        var myAdapter = HottestPromoAdapter(activity!!,hottestList)
        reclerView_hottest_promo.layoutManager = LinearLayoutManager(activity!!, LinearLayout.VERTICAL,false)
        reclerView_hottest_promo.adapter = myAdapter
        minimize_hottest.setImageResource(R.mipmap.ic_arrow_blue_down)

    }
   fun  getPreferenceNoMached(){
       var preferedPromoList = ArrayList<PromoModel>()
        for(i in 0 until promolist.size){



            for(j in 0 until promolist[i].subcategories.size){
                promolist[i].subcategories[j]
                for(k in 0 until userCategories.size){
                    for( p in 0 until userCategories[k].Subcategories.size){
                        if(userCategories[k].Subcategories[p].SubcategoryName==promolist[i].subcategories[j]){
                            if(userCategories[k].Subcategories[p].Selected) {
                                promolist[i].preferenceMatched = promolist[i].preferenceMatched + 1
                            }
                        }


                    }


                }



            }

            Log.e(TAG,"preference match for ${promolist[i].promoStore} is ${promolist[i].preferenceMatched}")
            if(promolist[i].preferenceMatched!=0){
                preferedPromoList.add(promolist[i])
            }

        }
       globalPromoList = promolist
Log.e(TAG,"Mana jud")
       setPreferencePromoAdapter(preferedPromoList)
       getLocation()
       getHottestPromo()

    }
    fun getUserPreferredTime(){
        doAsync {
            database.collection("UserPreferredTime").document(LoginActivity.userUIDS).get().addOnSuccessListener { document ->

                if (document.exists()) {

                    var promoLikeCountParce = document.toObject(userPreferredTimeParce::class.java)
                    globalUserPreferredTime = promoLikeCountParce

                } else {
                    Log.e(TAG, "dont exist")
                }

            }
        }
    }
   fun getPreferenceMatched() {
       var count = 0
       doAsync {
           for (i in 0 until promolist.size) {

               doAsync {
                   database.collection("PromoIntrested").document(promolist[i].promoStore).get().addOnSuccessListener { document ->

                       if (document.exists()) {

                           var promoLikeCountParce = document.toObject(promoLikesCountParce::class.java)
                           var promoLikes = promoLikeCountParce.LikeCount
                           promolist[i].interested = promoLikes
                           Log.e(TAG,"${promolist[i].promoStore} likes ${promolist[i].interested}")

                       } else {
                           Log.e(TAG, "dont exist")
                       }

                   }
               }

               doAsync {
                   var mPromoViews = promoViewsParde()
                   var promoViewsCount = 0
                   database.collection("PromoViews").document(promolist[i].promoStore)
                           .get().addOnSuccessListener { document ->
                               if (document.exists()) {
                                   mPromoViews = document.toObject(promoViewsParde::class.java)
                                   Log.e(TAG, "promo views get ${mPromoViews.promoViews}")
                                   promoViewsCount = mPromoViews.promoViews
                                   promolist[i].viewed = promoViewsCount
                                   Log.e(TAG,"${promolist[i].promoStore} views ${promolist[i].viewed}")
                               }
                           }
               }
    database.collection("PromoCategories").document(promolist[i].promoStore).collection("Subcategories").get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            for (DocumentSnapshot in task.result) {
                var subcategory = DocumentSnapshot.toObject(promoSubcategoryParce::class.java)
                promolist[i].subcategories.add(subcategory.SubcategoryName)
                Log.e(TAG, "${promolist[i].promoStore} - ${subcategory.SubcategoryName}")

            }
            count+=1
            Log.e(TAG, "promosubCategory size ${promolist[i].promoStore} - ${promolist[i].subcategories.size} index $count equals ${promolist.size}")
            if(count==promolist.size){
                getPreferenceNoMached()
            }

        }
    }


}



       }
   }
    fun getUserCategories() {
        doAsync {
            Log.e(TAG, "retreiving ${LoginActivity.userUIDS} Categories")
            var pistira = ArrayList<CategoryParse>()
            database.collection("UserCategories").document(LoginActivity.userUIDS).collection("Categories").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        var userCategory = document.toObject(CategoryParse::class.java)
                        Log.e(TAG, pistira.size.toString() + " totalCount retrieve ${userCategory.categoryName}")

                        pistira.add(userCategory)
                    }
                   getPromos()

                    Log.e(TAG, task.result.toString() + "   atay     " + task.isSuccessful.toString())
                    userCategories = pistira
                }


            }.addOnFailureListener {
            }

        }
    }

}





