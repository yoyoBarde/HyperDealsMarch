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
import android.widget.RemoteViews
import com.example.kent.hyperdeals.*
import com.example.kent.hyperdeals.MyAdapters.PromoModel
import com.example.kent.hyperdeals.MyAdapters.PromoModelBusinessman
import com.example.kent.hyperdeals.Home.HomeAdapter
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


class FragmentCategory: Fragment() {
companion object {
    lateinit var userLatLng: LatLng
}
    var notifIDCounter = 102
    private var promolist1= ArrayList<PromoModelBusinessman>()
    private var mAdapter : HomeAdapter? = null
    private var mSelected: SparseBooleanArray = SparseBooleanArray()
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var mFirebaseFirestore = FirebaseFirestore.getInstance()
    private var promolist = ArrayList<PromoModel>()
    lateinit var  geoFire:GeoFire
    lateinit var  ref:DatabaseReference
    lateinit var globalBitmap:Bitmap
    var TAG = "FragmentCategory"
   var notificationList = arrayListOf<String>("Firstnull")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragmentcategory, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ref = FirebaseDatabase.getInstance().getReference("Geofences")

        geoFire = GeoFire(ref)

        /*  mFirebaseFirestore.collection("PromoDetails").document(promos.promoStore).update(KEY_VIEWED,promos.viewed+5)*/


     var myIntent = Intent(activity!!,LocationService::class.java)
activity!!.startService(myIntent)
      val image =  mFirebaseFirestore.collection("PromoDetails").document("Bench").collection("promoImageLink").get()
        val text = mFirebaseFirestore.collection("PromoDetails").document("Benchm").collection("promoname")
        promolist = ArrayList()

        val database = FirebaseFirestore.getInstance()

        val layoutManager = LinearLayoutManager(context)
        my_recycler_view111.layoutManager = layoutManager
        my_recycler_view111.itemAnimator = DefaultItemAnimator()

doAsync {
    database.collection("PromoDetails").get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            for (DocumentSnapshot in task.result) {
                var upload = DocumentSnapshot.toObject(PromoModel::class.java)
                Log.d(TAG, DocumentSnapshot.getId() + " => " + DocumentSnapshot.getData())

                var geoPoint=DocumentSnapshot.getGeoPoint("promoGeo")
                upload.promoLocation= LatLng(geoPoint.latitude,geoPoint.longitude)
              //  upload.promoImageBitmap =  UrltoBitmap(upload.promoImageLink)
           promolist.add(upload)
                Log.e(TAG,"Location of the fcking user "+upload.promoLocation.latitude.toString()+upload.promoLocation.longitude.toString())

                Log.e(TAG,promolist.toString())

            }
            getLocation()



        } else
            toast("error")
    }



}


    }


    fun getDistance(){

//        for( i in 0 until promolist.size){
//            promolist[i].promoLatLng
//
//        }
//



    }


    fun getLocation() {


        locationManager = activity!!.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                userLatLng = LatLng(location.latitude,location.longitude)


                detectGeofence(GeoLocation(location.latitude,location.longitude))

                try {
                for(i in 0 until  promolist.size){


                    var distanceFormatted = String.format("%.2f",CalculationByDistance(userLatLng,promolist[i].promoLocation))
                  promolist[i].distance = distanceFormatted


                }

runOnUiThread {


    mAdapter = HomeAdapter(activity!!,mSelected, promolist)
    my_recycler_view111.adapter = mAdapter


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

    fun detectGeofence(userGeo:GeoLocation) {

        val geoQuery = geoFire.queryAtLocation(userGeo, 20.0)
        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onKeyEntered(key: String, location: GeoLocation) {

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
                                       displayNotification(key, notifIDCounter, promolist[i])
                                   }
                                    Log.e(TAG, "Ngeek")

                                } catch (e: Exception) {
                                    uiThread {
                                        displayNotification(key, notifIDCounter, promolist[i])
                                    }
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

        var resultIntent = Intent(activity!!,Business_PromoProfile::class.java)

        var actionIntent = Intent(activity!!,NotificationReceiver::class.java)
        val actionPendingIntent: PendingIntent = PendingIntent.getActivity(activity!!, NotifcationID2, actionIntent
                .putExtra("key",Channel)
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
}







