package com.example.insta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_comment.*


class Comment : AppCompatActivity() {

    private var postid=""
private var firebaseUser:FirebaseUser?=null
    private var publisherid=""
    private var adapter:commentAdapter?=null
    private var commentlist:MutableList<CommentModelclass>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val intent=intent
        postid=intent.getStringExtra("postid")
        publisherid=intent.getStringExtra("publisherid")

        Log.d("nine",postid.toString())
        firebaseUser=FirebaseAuth.getInstance().currentUser

           var recyclerView:RecyclerView
         recyclerView=findViewById(R.id.recyclerview_comment)

        val linearLayoutManager=LinearLayoutManager(this)
        linearLayoutManager.reverseLayout=true
        linearLayoutManager.stackFromEnd=true
        recyclerView.layoutManager=linearLayoutManager


        commentlist= ArrayList()
        adapter= commentAdapter(this, commentlist as ArrayList<CommentModelclass>)
        recyclerView.adapter=adapter




        userinfoonly()
        readcomment()
        getpostimage()


        post_comment.setOnClickListener {
            if (add_comment.text.isEmpty())
            {
                add_comment.error="Enter Comment"
                add_comment.requestFocus()
                return@setOnClickListener

            }

            addcomment()

        }
    }

    private fun addcomment() {


        val commentrefs = FirebaseDatabase.getInstance().reference.child("Comments")

        val commentmap=HashMap<String,Any>()

        commentmap["comment"]=add_comment.text.toString()
        commentmap["publisher"]=firebaseUser?.uid.toString()



        commentrefs.child(postid).child(System.currentTimeMillis().toString()).setValue(commentmap)
        addnotification()
        add_comment.text.clear()
    }

    private fun userinfoonly() {

    val dref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser?.uid.toString())
    dref.addValueEventListener(object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(p0: DataSnapshot) {

            if (p0.exists()) {

                val user = p0.getValue(User::class.java)

                Picasso.get().load(user?.getImage()).placeholder(R.drawable.profile).into(profile_image_comment)

            }
        }


    })

}
    private fun getpostimage() {

        val postref = FirebaseDatabase.getInstance().reference.child("Posts").child(postid)


        postref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {

                    val post = p0.getValue(Post::class.java)

                    Picasso.get().load(post?.getPostImage()).placeholder(R.drawable.profile).into(post_image_comment)

                }
            }


        })

    }

    private fun readcomment()
    {
        val commentref=FirebaseDatabase.getInstance().reference.child("Comments").child(postid)

        commentref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
               if (p0.exists())

               {
                   commentlist!!.clear()

                   for (snapshot in p0.children)
                   {
                       //val comments=snapshot.getValue(CommentModelclass::class.java)
                       val comment=snapshot.child("comment").getValue().toString()
                       val publisher=snapshot.child("publisher").getValue().toString()
                       Log.d("nines",comment.toString())
                       commentlist!!.add(CommentModelclass(comment,publisher))

                   }
                   adapter?.notifyDataSetChanged()




               }
            }
        })
    }
    private fun addnotification(){


        val notiref=FirebaseDatabase.getInstance().reference.child("Notifications")
            .child(publisherid!!)

        val notimap=HashMap<String,Any>()
        notimap["userid"]=firebaseUser?.uid.toString()
        notimap["text"]="commented:"+ add_comment.text.toString()
        notimap["postid"]=postid
        notimap["ispost"]=true


        notiref.push().setValue(notimap)


    }


}
