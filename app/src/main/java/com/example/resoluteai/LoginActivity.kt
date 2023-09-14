package com.example.resoluteai

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var editPhone : EditText
    private lateinit var editOtp : EditText
    private lateinit var editName : EditText
    private lateinit var verifyOTPBtn : Button
    private lateinit var generateOTPBtn : Button
    private lateinit var phone: String
    private lateinit var otp: String
    private lateinit var name: String

    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        editPhone = findViewById(R.id.idEdtPhoneNumber)
        editOtp = findViewById(R.id.idEdtOtp)
        editName = findViewById(R.id.idEdtName)
        verifyOTPBtn = findViewById(R.id.idBtnVerify)
        generateOTPBtn = findViewById(R.id.idBtnGetOtp)

        generateOTPBtn.setOnClickListener {
            phone = "+91" + editPhone.text.toString().trim()
            name = editName.text.toString().trim()
            if(TextUtils.isEmpty(phone) || TextUtils.isEmpty(name)){
                Toast.makeText(this, "Fields are empty", Toast.LENGTH_LONG).show()
            }else{
                sendVerificationCode(phone);
            }
        }

        verifyOTPBtn.setOnClickListener {
            otp = editOtp.text.trim().toString()
            Log.e("GFG" , "sendVerificationCode started")

            if(otp.isNotEmpty()){
                Log.e("GFG" , "sendVerificationCode1 started")
                var credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
                signInWithPhoneAuthCredential(credential)
            }
        }


        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // This method is called when the verification is completed
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                val user = User(name, email, phone)
//                saveUserDateToFireStore(user)
                Log.e("GFG" , "onVerificationCompleted Success")
                finish()
            }

            // Called when verification is failed add log statement to see the exception
            override fun onVerificationFailed(e: FirebaseException) {
                Log.e("GFG" , "onVerificationFailed  $e")
                Toast.makeText(getApplicationContext(), "Verification Failed", Toast.LENGTH_LONG).show()
            }

            // On code is sent by the firebase this method is called
            // in here we start a new activity where user can enter the OTP
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.e("GFG","onCodeSent: $verificationId")
                storedVerificationId = verificationId
                resendToken = token

            }
        }


    }


    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Log.e("GFG" , "sendVerificationCode started")
    }


    // verifies if the code matches sent by firebase
    // if success start the new activity in our case it is main Activity
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.e("GFG" , "signInWithPhoneAuthCredential $credential")
//                    val user = User(name, email, phone)
                    val intent = Intent(this , HomeActivity::class.java).putExtra("name", name)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("GFG" , "signInWithPhoneAuthCredential failed")
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this,"Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

}