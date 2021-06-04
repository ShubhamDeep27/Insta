package com.example.insta

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.activity_new_post.*


class NewPost : AppCompatActivity() {
    private var url=""
private var imageuri:Uri?=null
    private  var storageReferencepost: StorageReference?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        CropImage.activity()
            .setAspectRatio(3,2)
            .start(this)


        storageReferencepost= FirebaseStorage.getInstance().reference.child("Posts Images")


        save_newpost_btn.setOnClickListener {

            uploadimage()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==Activity.RESULT_OK &&data!=null)
        {
            var result=CropImage.getActivityResult(data)
            imageuri=result.uri
            image_post.setImageURI(imageuri)
        }


    }

    private fun uploadimage() {

        if(imageuri==null)
        {
            change_image.error="Select image"
            change_image.requestFocus()
            return
        }
        val progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Adding New Post")
        progressDialog.setMessage("Please wait Adding Post....")
        progressDialog.show()
        val fileref=storageReferencepost!!.child(System.currentTimeMillis().toString() +"jpeg")
        var uploadTask:StorageTask<*>
        uploadTask=fileref.putFile(imageuri!!)
        uploadTask.continueWithTask(Continuation  <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
            if (!task.isSuccessful)
            {
                task.exception?.let {
                    throw it
                    progressDialog.dismiss()
                }
            }
            return@Continuation fileref.downloadUrl
        }).addOnCompleteListener(OnCompleteListener  <Uri>{ task ->


            if (task.isSuccessful)
            {

                val downloadurl=task.result
                url=downloadurl.toString()
                val ref= FirebaseDatabase.getInstance().reference.child("Posts")

                val postid=ref.push().key
                val postmap= HashMap<String,Any>()

                postmap["postid"]=postid!!
                postmap["description"]=description_post.text.toString()
                postmap["publishername"]=FirebaseAuth.getInstance().currentUser?.uid.toString()
                postmap["postimage"]=url

                ref.child(postid).updateChildren( postmap)
                Toast.makeText(this,"Succesfully uploaded", Toast.LENGTH_SHORT).show()
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
}
