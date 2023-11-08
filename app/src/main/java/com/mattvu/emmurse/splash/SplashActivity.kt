package com.mattvu.emmurse.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mattvu.emmurse.R
import com.mattvu.emmurse.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        val textView = findViewById<TextView>(R.id.splashpageTextView)
        val logoAnimationView = findViewById<ImageView>(R.id.logoView)


        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_logo)
        val textFadeAnimation = AnimationUtils.loadAnimation(this, R.anim.text_fade_splash)



        rotateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                textView.visibility = View.VISIBLE
                textFadeAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                        finish()
                    }
                    override fun onAnimationRepeat(animation: Animation?) {}
                })
                textView.startAnimation(textFadeAnimation)
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
        logoAnimationView.startAnimation(rotateAnimation)
    }
}


