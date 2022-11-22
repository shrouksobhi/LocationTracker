package com.shrouk.locationtracker

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices


class LocationService : Service() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

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

        Log.e("TAG", "onCreate: SERVICE  ")
        requestLocationUpdate()
    }
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        super.onStartCommand(intent, flags, startId)
//        return START_NOT_STICKY
//    }

    fun requestLocationUpdate() {
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
                "Your current location is : latitude $latitude " + ", longitude $longitude",
               p0!!
            )
            Log.e("TAG", "onReceive: $latitude $longitude", )

        }
    }

    override fun onDestroy() {

            unregisterReceiver(broadcastReceiver)

        super.onDestroy()
    }
}