package com.shrouk.locationtracker

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit


class LocationService : Service() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private lateinit var locationCallback: LocationCallback

    private var currentLocation: Location? = null
//    private lateinit var notification: NotificationManager
    private lateinit var locationManager: LocationManager
    var latitude: Double = 0.0
    var longitude: Double? = 0.0



    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this)
        requestLocationUpdate()

        Log.e("TAG", "onCreate: SERVICE  ")
    }
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        super.onStartCommand(intent, flags, startId)
//        return START_NOT_STICKY
//    }

    fun requestLocationUpdate() {
//        locationRequest = LocationRequest.create().apply {
//            // Sets the desired interval for active location updates. This interval is inexact. You
//            // may not receive updates at all if no location sources are available, or you may
//            // receive them less frequently than requested. You may also receive updates more
//            // frequently than requested if other applications are requesting location at a more
//            // frequent interval.
//            //
//            // IMPORTANT NOTE: Apps running on Android 8.0 and higher devices (regardless of
//            // targetSdkVersion) may receive updates less frequently than this interval when the app
//            // is no longer in the foreground.
//            interval = TimeUnit.SECONDS.toMillis(60)
//
//            // Sets the fastest rate for active location updates. This interval is exact, and your
//            // application will never receive updates more frequently than this value.
//            fastestInterval = TimeUnit.SECONDS.toMillis(30)
//
//            // Sets the maximum time when batched location updates are delivered. Updates may be
//            // delivered sooner than this interval.
//            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
//
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        }
//
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                super.onLocationResult(locationResult)
//
//                currentLocation = locationResult.lastLocation
////
////                val intent = Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
////                intent.putExtra(EXTRA_LOCATION, currentLocation)
////                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
////
////                // Updates notification content if this service is running as a foreground
////                // service.
////                if (serviceRunningInForeground) {
////                    notificationManager.notify(
////                        NOTIFICATION_ID,
////                        generateNotification(currentLocation))
////                }
//                latitude=currentLocation!!.latitude
//                longitude=currentLocation!!.longitude
//                   Log.e("TAG", "requestLocationUpdate: $latitude $longitude")
//
//                var permission = ContextCompat.checkSelfPermission(
//                    applicationContext,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                )
//                if (permission == PackageManager.PERMISSION_GRANTED) {
//                    // Request location updates and when an update is
//                    // received, store the location in Firebase
//                    var proximitys = "ACTION"
//                    var filter: IntentFilter = IntentFilter(proximitys)
//                    registerReceiver(broadcastReceiver, filter)
//                    var intent: Intent = Intent(proximitys)
//                    val proximityIntent: PendingIntent
//                    proximityIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        PendingIntent.getBroadcast(
//                            applicationContext, 0,
//                            intent, PendingIntent.FLAG_MUTABLE
//                        )
//                        // Create the persistent notification
//                    } else {
//                        PendingIntent.getBroadcast(
//                            applicationContext, 0,
//                            intent, PendingIntent.FLAG_CANCEL_CURRENT
//                        )
//                    }
//                    fusedLocationProviderClient.requestLocationUpdates(
//                        locationRequest,
//                        proximityIntent
//                    )
//
//
//                }
        var request: LocationRequest = LocationRequest.create()
        request.setInterval(1000)
        request.setFastestInterval(9000)
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            longitude = location?.longitude
            latitude = location?.latitude!!
            Log.e("TAG", "requestLocationUpdate: $latitude $longitude")
            var permission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            );
            if (permission == PackageManager.PERMISSION_GRANTED) {
                // Request location updates and when an update is
                // received, store the location in Firebase
                var proximitys = "ACTION"
                var filter: IntentFilter = IntentFilter(proximitys)
                registerReceiver(broadcastReceiver, filter)
                var intent: Intent = Intent(proximitys)
                val proximityIntent: PendingIntent
                proximityIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    PendingIntent.getBroadcast(
                        this, 0,
                        intent, PendingIntent.FLAG_MUTABLE
                    )
                    // Create the persistent notification
                } else {
                    PendingIntent.getBroadcast(
                        this, 0,
                        intent, PendingIntent.FLAG_CANCEL_CURRENT
                    )
                }
                fusedLocationProviderClient.requestLocationUpdates(request, proximityIntent)


            }
         }
       }


    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(p0: Context?, p1: Intent?) {
           var notification:NotificationManager= p0?.getSystemService(
          NotificationManager::class.java
   ) as NotificationManager
            Log.e("TAG", "onReceive: $p1", )

            notification.sendNotification(
                "Latitude : $latitude " +"\n" +"Longitude : $longitude",
               "Your current location is :",
               R.drawable.map2,
               p0
            )
            Log.e("TAG", "onReceive: $latitude $longitude", )

        }

    }

    override fun onDestroy() {

      //  unregisterReceiver(broadcastReceiver)
      LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)

        super.onDestroy()
    }
}