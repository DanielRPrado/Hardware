package com.example.hardware

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions


class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var lecturaqr = findViewById<TextView>(R.id.lecturaqr)
        var lecturagps = findViewById<TextView>(R.id.lecturagps)
        var qr = findViewById<Button>(R.id.leerqr)
        var gps = findViewById<Button>(R.id.leergps)


        val barcodeLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
            ScanContract()
        ) { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    this,
                    "Scanned: " + result.contents,
                    Toast.LENGTH_LONG
                ).show()

            }
        }


        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES);
        options.setPrompt("Scan a barcode");
        options.setCameraId(0);  // Use a specific camera of the device
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);

        qr.setOnClickListener {
            barcodeLauncher.launch(options)
        }

        //////////////////////////////////////GPS///////////
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                } else -> {
                // No location access granted.
            }
            }
        }

// ...

// Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION))

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        gps.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
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
            }
            else
            {
                fusedLocationClient.lastLocation
                    .addOnCompleteListener {
                            task ->

                        if (task.isSuccessful)
                        {
                            lecturagps.text = "Latitud: " + task.result.latitude.toString() + "Longitud: " + task.result.longitude.toString()
                        }
                        else
                        {
                            Toast.makeText(this, "Fallo " + task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
            }

        }
    }
}