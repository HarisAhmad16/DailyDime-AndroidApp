package com.cmpt362team21.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.cmpt362team21.R // Change this to your actual R class
import com.cmpt362team21.ui.auth.SignInActivity
import kotlin.random.Random

class StartupActivity : AppCompatActivity() {

    private lateinit var coinImageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)

        coinImageView = findViewById(R.id.coinImageView)
        coinImageView.setImageResource(R.drawable.dollar)

        performAnimations()

        val randomDelay = Random.nextLong(3000, 6000)

        Handler().postDelayed({
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }, randomDelay)
    }
    private fun performAnimations() {
        // Array of animations
        val animations = arrayOf(
            "fadeIn", "scaleX", "translate", "rotate", "bounce", "scaleY", "rotateX", "rotateY"
        )

        // Choose a random animation from the array

        // Execute the chosen animation
        when (animations.random()) {
            "fadeIn" -> performFadeInAnimation()
            "scaleX" -> performScaleXAnimation()
            "scaleY" -> performScaleYAnimation()
            "translate" -> performTranslateAnimation()
            "rotate" -> performRotateAnimation()
            "rotateX" -> performRotateXAnimation()
            "rotateY" -> performRotateYAnimation()
            "bounce" -> performBounceAnimation()
        }
    }

    private fun performFadeInAnimation() {
        val fadeIn = ObjectAnimator.ofFloat(coinImageView, "alpha", 0f, 1f)
        fadeIn.duration = 2500
        fadeIn.interpolator = AccelerateDecelerateInterpolator()
        fadeIn.repeatCount = ValueAnimator.INFINITE
        fadeIn.start()
    }

    private fun performScaleXAnimation() {
        val scale = ObjectAnimator.ofFloat(coinImageView, "scaleX", 0f, 1f)
        scale.duration = 2500
        scale.interpolator = AccelerateDecelerateInterpolator()
        scale.repeatMode = ValueAnimator.REVERSE
        scale.repeatCount = ValueAnimator.INFINITE
        scale.start()
    }

    private fun performScaleYAnimation() {
        val scale = ObjectAnimator.ofFloat(coinImageView, "scaleY", 0f, 1f)
        scale.duration = 2500
        scale.interpolator = AccelerateDecelerateInterpolator()
        scale.repeatMode = ValueAnimator.REVERSE
        scale.repeatCount = ValueAnimator.INFINITE
        scale.start()
    }

    private fun performTranslateAnimation() {
        val translate = ObjectAnimator.ofFloat(coinImageView, "translationX", -100f, 100f)
        translate.duration = 1500
        translate.interpolator = AccelerateDecelerateInterpolator()
        translate.repeatMode = ValueAnimator.REVERSE
        translate.repeatCount = ValueAnimator.INFINITE
        translate.start()
    }

    private fun performRotateAnimation() {
        val rotation = ObjectAnimator.ofFloat(coinImageView, "rotation", 0f, 360f)
        rotation.duration = 1500
        rotation.interpolator = AccelerateDecelerateInterpolator()
        rotation.repeatMode = ValueAnimator.REVERSE
        rotation.repeatCount = ValueAnimator.INFINITE
        rotation.start()

    }

    private fun performRotateXAnimation() {
        val rotation = ObjectAnimator.ofFloat(coinImageView, "rotationX", 0f, 360f)
        rotation.duration = 1500
        rotation.interpolator = AccelerateDecelerateInterpolator()
        rotation.repeatMode = ValueAnimator.REVERSE
        rotation.repeatCount = ValueAnimator.INFINITE
        rotation.start()

    }

    private fun performRotateYAnimation() {
        val rotation = ObjectAnimator.ofFloat(coinImageView, "rotationY", 0f, 360f)
        rotation.duration = 1500
        rotation.interpolator = AccelerateDecelerateInterpolator()
        rotation.repeatMode = ValueAnimator.REVERSE
        rotation.repeatCount = ValueAnimator.INFINITE
        rotation.start()

    }

    private fun performBounceAnimation() {
        val bounce = ObjectAnimator.ofFloat(coinImageView, "translationY", 0f, -300f, -100f)
        bounce.duration = 2500
        bounce.interpolator = AccelerateDecelerateInterpolator()
        bounce.repeatMode = ValueAnimator.REVERSE
        bounce.repeatCount = ValueAnimator.INFINITE
        bounce.start()
    }
}
