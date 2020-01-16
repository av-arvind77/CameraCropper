package com.artivatic.cameracontrollersdk

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.custom_camera_preview.*
import java.io.File
import java.io.FileOutputStream
import android.graphics.Matrix
import android.hardware.Camera
import android.support.v4.content.ContextCompat
import android.view.OrientationEventListener
import android.view.WindowManager


class CustomCameraPreview : AppCompatActivity() {

    private val TAG = "CustomCameraPreview"
    private var mOrientation = -1
    private var onClickOrientation = 1
    private var cropX = 0.0
    private var cropY = 0.0
    private var cropWidth = 0.0
    private var cropHeight = 0.0
    private var mCamera : Camera? = null
    private lateinit var cameraSurfaceView : CameraSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_camera_preview)

        val kycType = intent.getStringExtra("KycType")
        val previewMessage = intent.getStringExtra("PreviewMessage")

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        try {
            mCamera = Camera.open()

        val cameraPreview = mCamera?.parameters?.supportedPreviewSizes!!.first()

        val cameraParameters = mCamera?.parameters
        cameraParameters?.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
        mCamera?.parameters = cameraParameters
        mCamera?.parameters?.setPreviewSize(cameraPreview.width,cameraPreview.height)

        cameraSurfaceView = CameraSurfaceView(this,mCamera,0)
        camera_frame_layout.addView(cameraSurfaceView)
        }catch (e : java.lang.Exception){
            Toast.makeText(this,"Provide Camera Permission",Toast.LENGTH_SHORT).show()
        }

        when {
            kycType.toLowerCase().contains("full") -> {
                camera_preview_overlay.background = ContextCompat.getDrawable(this,R.drawable.full_aadhar_overlay)
                cropX = 0.2
                cropY = 0.1906
                cropWidth =0.6
                cropHeight = 0.6187
            }
            kycType.toLowerCase().contains("voter") -> {
                camera_preview_overlay.background = ContextCompat.getDrawable(this,R.drawable.voter_ovrlay)
                cropX = 0.25
                cropY = 0.1870
                cropWidth =0.5
                cropHeight = 0.4316
            }
            else -> {
                camera_preview_overlay.background = ContextCompat.getDrawable(this,R.drawable.pan_aadhar_overlay)
                cropX = 0.1
                cropY = 0.1667
                cropWidth =0.8
                cropHeight = 0.3174

            }
        }

        document_type.text = "$kycType Capture"
        message_text_view.text = previewMessage



        take_picture.setOnClickListener {
            onClickOrientation = mOrientation
            try {
                mCamera?.takePicture(null,null,pictureCallback)
            }catch (e : java.lang.Exception){
                Toast.makeText(this,"Error while taking picture!.Try Again.",Toast.LENGTH_SHORT).show()
            }

        }

        close_camera_preview.setOnClickListener {
            setResult(RESULT_CANCELED,Intent())
            finish()
        }
    }


    private val pictureCallback = Camera.PictureCallback { data, camera
        ->
        try {

            Toast.makeText(this,"Picture Taken",Toast.LENGTH_SHORT).show()
            var bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)

            val file = saveBitmap(bitmap,10)

            val exif = ExifInterface(file.toString())

            Log.d("TAG", "EXIF value >>" + exif.getAttribute(ExifInterface.TAG_ORIENTATION))
            when {
                exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("full_aadhar_overlay", ignoreCase = true
                )
                -> bitmap = rotate(bitmap, 90)
                exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("8", ignoreCase = true
                )
                -> bitmap = rotate(bitmap, 270)
                exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("3", ignoreCase = true
                )
                -> bitmap = rotate(bitmap, 180)
                exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("0", ignoreCase = true
                )
                -> bitmap = rotate(bitmap, 90)

            }

            val width = bitmap.width.toFloat()
            val height = bitmap.height.toFloat()

            val croppedBitmap = Bitmap.createBitmap(bitmap,
                (width*cropX).toInt(),
                (height*cropY).toInt(),
                (width*cropWidth).toInt(),
                (height*cropHeight).toInt())


            val rotatedfile = saveBitmap(croppedBitmap,100)
            val returnIntent = Intent()
            returnIntent.putExtra("imagePath", rotatedfile.toURI().toString())
            setResult(RESULT_OK, returnIntent)
            finish()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun saveBitmap(bitmap: Bitmap, quality: Int) : File{
        return try {
            val tempPath = cacheDir.absolutePath+"/TempImage.png"
            val tempFile = File(tempPath)

            val fileOutputStream = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG,quality,fileOutputStream)
            fileOutputStream.close()
            tempFile

        }catch (e:java.lang.Exception){
            Log.d("","")
            File("")
        }
    }


    private fun rotate(bitmap: Bitmap, degrees: Int): Bitmap {
        return try {
            val matrix = Matrix()
            matrix.setRotate(degrees.toFloat())
            val oriented =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            oriented
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            bitmap
        }
    }

}