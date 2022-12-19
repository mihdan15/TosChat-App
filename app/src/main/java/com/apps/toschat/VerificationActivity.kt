package com.apps.toschat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.apps.toschat.databinding.ActivityVerificationBinding
import com.google.firebase.auth.FirebaseAuth

class VerificationActivity : AppCompatActivity() {

    var binding : ActivityVerificationBinding? = null

    var auth : FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        auth = FirebaseAuth.getInstance()
        if (auth!!.currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        supportActionBar?.hide()
        binding!!.number.requestFocus()
        binding!!.continueBtn.setOnClickListener{
            val intent = Intent(this, OTPActivity::class.java)
            intent.putExtra("phoneNumber", binding!!.number.text.toString())
            startActivity(intent)
        }
    }
}