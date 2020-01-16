package com.artivatic.cameracontrollersdk

import android.app.AlertDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.chooser_dialog.view.*

class ChooseSourceDialog(private val activity: AppCompatActivity,private val kycType : String,private val previewMessage : String) {
    private lateinit var alertDialogBuilder: AlertDialog.Builder
    private lateinit var alertDialog: AlertDialog

    fun showDialog(){
        initializeImagePickDialog()
        alertDialog = alertDialogBuilder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    private fun initializeImagePickDialog() {
        alertDialogBuilder = AlertDialog.Builder(activity)
        val dialogView = activity.layoutInflater.inflate(R.layout.chooser_dialog,null)
        alertDialogBuilder.setView(dialogView)

        dialogView.camera_view.setOnClickListener {
            alertDialog.dismiss()
            val i = Intent(activity,CameraControllerActivity::class.java)
            i.putExtra("KycType",kycType)
            i.putExtra("PreviewMessage",previewMessage)
            i.putExtra("Source","Camera")
            activity.startActivityForResult(i, KYC_REQUEST_CODE)
        }

        dialogView.gallery_view.setOnClickListener {
            alertDialog.dismiss()
            val i = Intent(activity,CameraControllerActivity::class.java)
            i.putExtra("KycType",kycType)
            i.putExtra("PreviewMessage",previewMessage)
            i.putExtra("Source","Gallery")
            activity.startActivityForResult(i, KYC_REQUEST_CODE)
        }

        dialogView.cancel.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    companion object{
        const val KYC_REQUEST_CODE = 52525
    }
}