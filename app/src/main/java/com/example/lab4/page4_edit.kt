package com.example.lab4

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
 

class page4_edit : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page4_edit)
        val cancelButton : Button = findViewById(R.id.edit_cancel_button)
        val saveButton : Button = findViewById(R.id.edit_save_button)


        saveButton.setOnClickListener {

            Toast.makeText(applicationContext, "Changes saved", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }



    }
}