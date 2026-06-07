package com.example.expensetracker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.splash)

        val logo =
            findViewById<ImageView>(R.id.logoIv)

        val animation =
            AnimationUtils.loadAnimation(
                this,
                android.R.anim.fade_in
            )

        logo.startAnimation(animation)

        Handler(Looper.getMainLooper())
            .postDelayed({

                startActivity(
                    Intent(
                        this,
                        MainActivity::class.java
                    )
                )

                finish()

            }, 2500)
    }
}