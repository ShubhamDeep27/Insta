package com.example.insta

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_signin.*
import java.net.InterfaceAddress

class Signin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        if(FirebaseAuth.getInstance().currentUser!=null)
        {
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        register_btn.setOnClickListener {
            val intent=Intent(this,SignUp::class.java)
            startActivity(intent)
        }
 login_btn.setOnClickListener {
     loginuser()
 }

        }

    private fun loginuser() {
        val email=email_id_login.text.toString().trim()
        val password=password_login.text.toString().trim()

        if (email.isEmpty()){
            email_id_login.error="Enter Email"
            email_id_login.requestFocus()
            return

        }
        if (password.isEmpty()) {
            password_login.error = "Enter Password"
            password_login.requestFocus()
            return
        }
        val progrssDialog = ProgressDialog(this)
        progrssDialog.setTitle("SignIn")
        progrssDialog.setMessage("Please wait......")
        progrssDialog.setCanceledOnTouchOutside(false)
        progrssDialog.show()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    val intent=Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    val messsage=task.exception.toString()
                    Toast.makeText(this,"Error $messsage", Toast.LENGTH_SHORT).show()

                    progrssDialog.dismiss()
                }

            }
    }
}


