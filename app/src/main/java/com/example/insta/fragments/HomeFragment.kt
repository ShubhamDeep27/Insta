package com.example.insta.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.insta.Post

import com.example.insta.R
import com.example.insta.storyAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    private var adapter:postAdapter?=null
    private var postlist:MutableList<Post>?=null
    private  var followinglist:MutableList<String>?=null
    private var storyAdapter:storyAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_home, container, false)


         val recyclerView:RecyclerView
        recyclerView=view.findViewById(R.id.recycler_view_home)



        val LinearLayoutManager=LinearLayoutManager(context)
       LinearLayoutManager.reverseLayout=true
        LinearLayoutManager.stackFromEnd=true
   recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager=LinearLayoutManager



        var recyclerViewstory:RecyclerView
        recyclerViewstory=view.findViewById(R.id.recycler_view_story)



        val LinearLayoutManager2=LinearLayoutManager(context)
        LinearLayoutManager2.reverseLayout=true
        LinearLayoutManager2.stackFromEnd=true
        recyclerViewstory.setHasFixedSize(true)
        recyclerViewstory.layoutManager=LinearLayoutManager2


        postlist=ArrayList()
        adapter=context?.let{ postAdapter(it,postlist as ArrayList)}
         recyclerView.adapter=adapter


        checkfollowings()


        return view
    }

    private fun checkfollowings() {
          followinglist=ArrayList()

        val followingref=FirebaseDatabase.getInstance().reference.child("Follow").child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("Following")



        followingref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
              if (p0.exists())
              {
                  (followinglist as ArrayList<String>).clear()

                for (snapshot in p0.children)
                {
                    snapshot.key?.let {
                        (followinglist as ArrayList<String>).add(it)}
                    Log.d("yess", followinglist.toString())
                }
                  retrivepost()
              }
            }
        })
    }

    private fun retrivepost() {
        val postref=FirebaseDatabase.getInstance().reference.child("Posts")

        postref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                postlist?.clear()

                for (snapshot in p0.children) {
                    //val post = snapshot.getValue(Post::class.java)

                    val publishername=snapshot.child("publishername").getValue().toString()
                    val descritpion=snapshot.child("description").getValue().toString()
                    val postimage=snapshot.child("postimage").getValue().toString()
                    val postid=snapshot.child("postid").getValue().toString()
                    Log.d("yep",publishername)


                    for (id in (followinglist as ArrayList<String>)) {
                        if (publishername == id)
                        {
                            postlist!!.add(Post(postid,postimage,publishername,descritpion))
                        }


                        adapter?.notifyDataSetChanged()
                    }

                }
            }

        })

    }
  }









