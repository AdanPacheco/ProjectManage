package com.udemy.projectmanage.ui.view

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.udemy.projectmanage.R
import com.udemy.projectmanage.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {

    private lateinit var binding:ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        makeFullScreen()
        initListeners()

    }

    private fun initListeners() {
        binding.btnSignUpIntro.setOnClickListener{
            startActivity(Intent(this,SignUpActivity::class.java))
        }
        binding.btnSignInIntro.setOnClickListener{
            startActivity(Intent(this,SignInActivity::class.java))
        }
    }

    private fun makeFullScreen() {
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }


}