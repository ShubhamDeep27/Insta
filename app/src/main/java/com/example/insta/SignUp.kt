package com.example.insta

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
   signin_btn.setOnClickListener {
       val intent= Intent(this,Signin::class.java)
       startActivity(intent)
   }
        signup_btn.setOnClickListener {
            createaccount()
        }

    }

    private fun createaccount() {
        val fullname=full_name_register.text.toString().trim()
        val username=username_register.text.toString().trim()
        val email=email_id_register.text.toString().trim()
        val password=password_register.text.toString().trim()

        if (fullname.isEmpty()){
            full_name_register.error="Enter Name"
            full_name_register.requestFocus()
            return

        }
        if (username.isEmpty()) {
            username_register.error = "Enter Username"
            username_register.requestFocus()
            return
        }
        if (email.isEmpty()){
            email_id_register.error="Enter Email"
            email_id_register.requestFocus()
            return

        }
        if (password.isEmpty()) {
            password_register.error = "Enter Password"
            password_register.requestFocus()
            return
        }
        val progrssDialog = ProgressDialog(this)
        progrssDialog.setTitle("Signup")
        progrssDialog.setMessage("Please wait......")
        progrssDialog.setCanceledOnTouchOutside(false)
        progrssDialog.show()


        val mAuth:FirebaseAuth=FirebaseAuth.getInstance()
        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    saveuser(username,fullname,email,password,progrssDialog)

                }
                else
                {
                    val messsage=task.exception.toString()
                    Toast.makeText(this,"Error $messsage",Toast.LENGTH_SHORT).show()
                    progrssDialog.dismiss()

                }
            }
    }

    private fun saveuser(username: String, fullname: String, email: String, password: String,progrssDialog: ProgressDialog) {
        val currentuserid=FirebaseAuth.getInstance().currentUser?.uid.toString()
        val dref:DatabaseReference=FirebaseDatabase.getInstance().reference.child("Users")
        val usermap= HashMap<String,Any>()
        usermap["uid"]=currentuserid
        usermap["fullname"]=fullname.toLowerCase()
        usermap["username"]=username.toLowerCase()
        usermap["bio"]="I am using Instagram"
        usermap["image"]="https://firebasestorage.googleapis.com/v0/b/insta-d754e.appspot.com/o/Default%2Fprofile.png?alt=media&token=a466f9cb-b973-42be-88b0-1dae972ae383"

        dref.child(currentuserid).setValue(usermap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    progrssDialog.dismiss()
                    Toast.makeText(this,"Succesfully created",Toast.LENGTH_SHORT).show()


                    val followingref=FirebaseDatabase.getInstance().reference.child("Follow").child(currentuserid.toString())
                            .child("Following").child(currentuserid).setValue(true)
                    val intent=Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    val messsage=task.exception.toString()
                    Toast.makeText(this,"Error $messsage",Toast.LENGTH_SHORT).show()
                    progrssDialog.dismiss()

                }
            }




    }


}
