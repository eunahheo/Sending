package com.example.first_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.first_1.R.id.button2
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        button1.setOnClickListener {
            val intent = Intent(applicationContext, SecondActivity::class.java)
            startActivity(intent)
        }
        button2.setOnClickListener {
            val intent = Intent(applicationContext, ThirdActivity::class.java)
            startActivity(intent)
        }


    }

}