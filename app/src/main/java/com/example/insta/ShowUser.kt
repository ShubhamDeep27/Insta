package com.example.insta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ShowUser : AppCompatActivity() {



    var id:String=""
    var title:String=""
   private var adapter:usersearchAdapter?=null

    var userlist:List<User>?=null
    var useridlist:List<String>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_user)

        val intent=intent
        title=intent.getStringExtra("title")
        id=intent.getStringExtra("id")



        val toolbar:Toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title=title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        userlist=ArrayList()
        useridlist=ArrayList()
        val recyclerView:RecyclerView
        recyclerView=findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager=LinearLayoutManager(this)
        adapter= usersearchAdapter(this,userlist as ArrayList<User>,false)
        recyclerView?.adapter=adapter






        when(title)
        {
            "Likes"->getlikes()

            "Following"->getfollowing()

            "Followers"->getfollower()
            "Views"->getviews()


        }
    }

    private fun getviews() {

    }

    private fun getfollower() {

        val followerref =
            FirebaseDatabase.getInstance().reference.child("Follow").child(id)
                .child("Followers")

        followerref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    (useridlist as ArrayList).clear()


                    for (snapshot in p0.children)
                    {
                        (useridlist as ArrayList<String>).add(snapshot.key.toString())
                        Log.d("nopes5",useridlist.toString())
                    }
                    showUsers()

                }
            }

        })

    }

    private fun getfollowing() {
        val followingref =
            FirebaseDatabase.getInstance().reference.child("Follow").child(id)
                .child("Following")


        followingref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {

                    (useridlist as ArrayList).clear()


                    for (snapshot in p0.children)
                    {
                        (useridlist as ArrayList<String>).add(snapshot.key.toString())

                    }
                    showUsers()
                }
            }

        })
    }

    private fun getlikes() {


        val likeref= FirebaseDatabase.getInstance().reference.child("Likes")
            .child(id)


        likeref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    (useridlist as ArrayList).clear()


                    for (snapshot in p0.children)
                    {
                        (useridlist as ArrayList<String>).add(snapshot.key.toString())
                        Log.d("nopes8",useridlist.toString()
                        )

                    }
                    showUsers()

                }


            }


        })


    }

    private fun showUsers() {
        val dref=FirebaseDatabase.getInstance().getReference().child("Users")
        dref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {



                (userlist as ArrayList).clear()

                for (snapshot in p0.children)
                {
                    val fullname=snapshot.child("fullname").getValue().toString()
                    val username=snapshot.child("username").getValue().toString()
                    val bio=snapshot.child("bio").getValue().toString()
                    val image=snapshot.child("image").getValue().toString()
                    val uid=snapshot.child("uid").getValue().toString()
                    for (id in useridlist!!) {
                        if (uid == id && uid!=FirebaseAuth.getInstance().currentUser?.uid ) {
                            ( userlist as ArrayList<User>).add(User(username,fullname,bio,image,uid))
                             Log.d("nopes6",useridlist.toString())
                        }
                    }
                    adapter?.notifyDataSetChanged()

                }

            }

        })


    }
}
