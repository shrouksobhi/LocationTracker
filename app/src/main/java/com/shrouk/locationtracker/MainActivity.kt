package com.shrouk.locationtracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.core.content.getSystemService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

class MainActivity : AppCompatActivity() {
    private lateinit var locationManager: LocationManager
    private lateinit var mylocation :TextView
    private lateinit var getlocation:Button
    private val REQUEST_LOCATION_PERMISSIN_CODE =1

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var notification: NotificationManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mylocation = findViewById(R.id.textView)
        getlocation = findViewById(R.id.getlocation)


        getlocation.setOnClickListener {
getMyLocation()
            startTrackerService()
        }
    }

    fun startTrackerService() {
        val serviceIntent = Intent(this, LocationService::class.java)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            ContextCompat.startForegroundService(Intent(context, LocationService::class.java))
//        } else {
//            startService(Intent(this, LocationService::class.java))
//        }
        startService(serviceIntent)
      //  ContextCompat.startForegroundService(this, serviceIntent);
    }
   private fun getMyLocation() {
      // locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
       if (ContextCompat.checkSelfPermission
               (
               this,
               Manifest.permission.ACCESS_FINE_LOCATION
           ) != PackageManager.PERMISSION_GRANTED
       ) {
           ActivityCompat.requestPermissions(
               this,
               arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSIN_CODE
           )
       }
     //  fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
//           var longitude = location?.longitude
//           var latitude = location?.latitude
    //  mylocation.text="Your Current location : $latitude , $longitude"

        //   notification.sendNotification("You are now in : $latitude ,$longitude",this)

       }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSIN_CODE){
            if (grantResults.isEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
            }
        }
    }
