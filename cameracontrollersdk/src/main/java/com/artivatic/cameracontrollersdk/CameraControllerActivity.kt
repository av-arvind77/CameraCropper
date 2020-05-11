package com.artivatic.cameracontrollersdk

import  android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.artivatic.cameracontrollersdk.cropper.CropImage
import kotlinx.android.synthetic.main.activity_camera_controller.*
import java.io.File

class CameraControllerActivity : AppCompatActivity() {

    private var previewMessage = ""
    private var classificationType = ""
    private var isCameraSource : Boolean = false
    private var imageUri = ""
    private var imageSource = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_controller)
        checkPermission()

        previewMessage = intent.getStringExtra("PreviewMessage")
        classificationType = intent.getStringExtra("KycType")
        imageSource = intent.getStringExtra("Source")


        if (imageSource=="Camera"){
            val intent = Intent(this,CustomCameraPreview::class.java)
            intent.putExtra("KycType",classificationType)
            intent.putExtra("PreviewMessage",previewMessage)
            startActivityForResult(intent,111)
        }else if (imageSource=="Gallery"){
            progressBar.visibility = View.GONE
            startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),112)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            RESULT_OK -> {

                when (requestCode) {
                    CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                        val result = CropImage.getActivityResult(data)
                        val resultUri = result.uri
                        if (isCameraSource){
                            val originalUri = result.originalUri
                            val isDeleted = File(originalUri.path).delete()
                            Log.d("","")
                        }


                        if (result.bitmap==null){
                            val returnIntent = Intent()
                            returnIntent.putExtra("result", "File Not Found")
                            setResult(RESULT_OK, returnIntent)
                        }

                        imageUri = resultUri.toString()

                        val returnIntent = Intent()
                        returnIntent.putExtra("imageUri",imageUri)
                        setResult(RESULT_OK, returnIntent)
                        finish()

                    }
                    112 -> {
                        CropImage.activity(data?.data).start(this)
                    }
                    111 -> {
                        isCameraSource = true
                        val imageUri = Uri.parse(data?.getStringExtra("imagePath"))
                        CropImage.activity(imageUri).start(this)
                    }
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                val error =  CropImage.getActivityResult(data).error

                setResult(RESULT_CANCELED)
                finish()
            }
            RESULT_CANCELED -> {
                setResult(RESULT_CANCELED)
                finish()
            }

        }
    }

    private fun checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA),
                12)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}