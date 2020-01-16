package com.artivatic.cameracontroller

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.artivatic.cameracontrollersdk.ChooseSourceDialog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val chooseSourceDialog = ChooseSourceDialog(this,"full","pan")
        chooseSourceDialog.showDialog()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ChooseSourceDialog.KYC_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            val imageURI = data!!.getStringExtra("imageUri").toString()

            Toast.makeText(this,imageURI,Toast.LENGTH_LONG).show()
        }
    }
}
