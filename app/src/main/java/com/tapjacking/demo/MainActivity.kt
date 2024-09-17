package com.tapjacking.demo
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.net.Uri
import android.widget.Button
import android.widget.EditText
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private val OVERLAY_PERMISSION_REQ_CODE = 100
    private lateinit var intent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkOverlayPermission()
        val start: Button = findViewById(R.id.btn)
        val stop: Button = findViewById(R.id.stop)
        val packageName: EditText = findViewById(R.id.package_name)
        val exportedActivity: EditText = findViewById(R.id.exported_activity)
        start.setOnClickListener {
            if (packageName.text.isEmpty() && exportedActivity.text.isEmpty()) {
                Toast.makeText(this, "Package name and exported activity are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            intent = Intent(this, OverlayService::class.java)
            intent.putExtra("targetPackageName", packageName.text.toString())
            intent.putExtra("targetExportedActivity", exportedActivity.text.toString())
            startService(intent)
        }

        stop.setOnClickListener {
            try {
                this.stopService(intent)
            } catch (e: UninitializedPropertyAccessException) {
                Toast.makeText(this, "Start overlay first :)", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "???", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE)
        } else {
            startService(Intent(this, OverlayService::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // Handle the case where the user did not grant the permission
            } else {
                startService(Intent(this, OverlayService::class.java))
            }
        }
    }
}





