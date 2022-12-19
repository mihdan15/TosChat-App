package com.apps.toschat

import android.app.AlertDialog
import android.content.Intent
import android.inputmethodservice.InputMethodService
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.apps.toschat.databinding.ActivityOtpactivityBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.sql.RowId
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {

    var binding : ActivityOtpactivityBinding? = null
    var verificationId:String? = null
    var auth:FirebaseAuth? = null
    var dialog: AlertDialog? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("OTPActivity", "Continued clicked")
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        dialog = AlertDialog.Builder(this).create()
        dialog!!.setMessage("Mengirimkan OTP...")
        dialog!!.setCancelable(true)
        dialog!!.show()
        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()
        val phoneNumber = intent.getStringExtra("phoneNumber")
        binding!!.phoneLabel.text = "Verifikasi $phoneNumber"
        val options = PhoneAuthOptions.newBuilder(auth!!)
            .setPhoneNumber(phoneNumber!!)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    TODO("Not yet implemented")

                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    TODO("Not yet implemented")
                }

                override fun onCodeSent(
                    verifyId: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(verifyId, forceResendingToken)
                    dialog!!.dismiss()
                    verificationId = verifyId
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    binding?.otpView?.requestFocus()
                }

            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
        binding!!.otpView.setOtpCompletionListener({ otp->
            val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
            auth!!.signInWithCredential(credential)
                .addOnCompleteListener {task->
                    if (task.isSuccessful){
                        val intent = Intent(this, SetupProfileActivity::class.java)
                        startActivity(intent)
                        finishAffinity()
                    } else {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }
                }
        })
    }
}