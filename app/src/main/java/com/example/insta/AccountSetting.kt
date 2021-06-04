package com.example.insta

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class AccountSetting : AppCompatActivity() {
    private lateinit var firebaseuser: FirebaseUser
private var imageuri:Uri?=null
    private var url=""
    private var checker=""
    private  var storageReference:StorageReference?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)

        firebaseuser = FirebaseAuth.getInstance().currentUser!!
    storageReference=FirebaseStorage.getInstance().reference.child("Profile Image")

        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this, Signin::class.java)
            startActivity(intent)
            finish()

        }


        change_image.setOnClickListener {
             checker="clicked"
            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this)
        }

            save_profile.setOnClickListener {
                if(checker=="clicked")
                {
          uploadimageandinfo()
                }
                else
                {
                    updateuserinfo()
                }
            }


        userinfoonly()
    }

    private fun uploadimageandinfo() {


        val progressDialog=ProgressDialog(this)
        progressDialog.setTitle("Account Setting")
        progressDialog.setMessage("Please wait....")
        progressDialog.show()

        if(fullname_profile.text.toString()=="")
        {
            fullname_profile.error="Enter Fullname"
            fullname_profile.requestFocus()
            return

        }
        if(username_profile.text.toString()=="")
        {
            username_profile.error="Enter Fullname"
            username_profile.requestFocus()
            return

        }
        if(imageuri==null)
        {
            change_image.error="Select image"
            change_image.requestFocus()
            return
        }

        val fileref=storageReference!!.child(firebaseuser.uid +"jpeg")
        var uploadTask:StorageTask<*>
        uploadTask=fileref.putFile(imageuri!!)
        uploadTask.continueWithTask(Continuation  <UploadTask.TaskSnapshot,Task<Uri>>{ task ->
      if(!task.isSuccessful)
      {
          task.exception?.let {
              throw it
              progressDialog.dismiss()
          }
      }
    return@Continuation fileref.downloadUrl

        }).addOnCompleteListener(OnCompleteListener <Uri>{ task ->


            if (task.isSuccessful)
            {

                val downloadurl=task.result
                url=downloadurl.toString()
                val ref=FirebaseDatabase.getInstance().reference.child("Users")
                val usermap= HashMap<String,Any>()

                usermap["fullname"]=fullname_profile.text.toString().toLowerCase()
                usermap["username"]=username_profile.text.toString().toLowerCase()
                usermap["bio"]=bio_profile_frag.text.toString()
                usermap["image"]=url

                ref.child(firebaseuser.uid).updateChildren(usermap)
                Toast.makeText(this,"Succesfully updated", Toast.LENGTH_SHORT).show()
                val intent=Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
                progressDialog.dismiss()
            }
            else
            {
                progressDialog.dismiss()
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==Activity.RESULT_OK && data!=null)
        {

            var result=CropImage.getActivityResult(data)
            imageuri=result.uri
            profile_image.setImageURI(imageuri)

        }
    }


    private fun updateuserinfo() {

        val dref = FirebaseDatabase.getInstance().getReference("Users")

        if(fullname_profile.text.toString()=="")
        {
            fullname_profile.error="Enter Fullname"
            fullname_profile.requestFocus()
            return

        }
        if(username_profile.text.toString()=="")
        {
            username_profile.error="Enter Fullname"
            username_profile.requestFocus()
            return

        }

        val usermap= HashMap<String,Any>()

        usermap["fullname"]=fullname_profile.text.toString().toLowerCase()
        usermap["username"]=username_profile.text.toString().toLowerCase()
        usermap["bio"]=bio_profile_frag.text.toString()


        dref.child(firebaseuser.uid).updateChildren(usermap)
        Toast.makeText(this,"Succesfully updated", Toast.LENGTH_SHORT).show()
        val intent=Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()



    }

    private fun userinfoonly() {

            val dref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseuser.uid)
            dref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    if (p0.exists()) {

                        val user = p0.getValue(User::class.java)
                        Log.d("dog2", user.toString())
                       Picasso.get().load(user?.getImage()).placeholder(R.drawable.profile).into(profile_image)
                        username_profile?.setText(user?.getUsername())
                        fullname_profile?.setText(user?.getFullname())
                        bio_profile_frag?.setText(user?.getBio())
                    }
                }


            })

        }
    }


