package hu.bme.aut.android.sporttracker.data.service
//
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.Service
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.os.IBinder
//import androidx.core.app.NotificationCompat
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationServices
//import hu.bme.aut.android.sporttracker.R
//import hu.bme.aut.android.sporttracker.data.location.repository.LocationRepository
//
//class LocationService : Service() {
//
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//
//    override fun onCreate() {
//        super.onCreate()
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        startForegroundService()
//    }
//
//    private fun startForegroundService() {
//        val channelId = "location_channel"
//        val channelName = "Location Service"
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
//        } else {
//            TODO("VERSION.SDK_INT < O")
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val notification: Notification = NotificationCompat.Builder(this, channelId)
//            .setContentTitle("Location Service")
//            .setContentText("Recording location in the background")
//            .setSmallIcon(R.drawable.baseline_hiking_24)
//            .build()
//
//        startForeground(1, notification)
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        // Start location updates
//        LocationRepository(fusedLocationClient, this).startLocationUpdates()
//        return START_STICKY
//    }
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        // Stop location updates
//        LocationRepository(fusedLocationClient, this).stopLocationUpdates()
//    }
//}