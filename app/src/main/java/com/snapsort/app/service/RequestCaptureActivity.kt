package com.snapsort.app.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle

class RequestCaptureActivity : Activity() {

    private val REQUEST_MEDIA_PROJECTION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(
            projectionManager.createScreenCaptureIntent(),
            REQUEST_MEDIA_PROJECTION
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == RESULT_OK && data != null) {
            val serviceIntent = Intent(this, FloatingCaptureService::class.java).apply {
                action = FloatingCaptureService.ACTION_START_PROJECTION
                putExtra(FloatingCaptureService.EXTRA_RESULT_CODE, resultCode)
                putExtra(FloatingCaptureService.EXTRA_RESULT_DATA, data)
            }
            startForegroundService(serviceIntent)
        }
        finish()
    }
}
