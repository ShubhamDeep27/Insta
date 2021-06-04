package com.example.insta.fragments


import android.content.Context
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * A simple [Fragment] subclass.
 */
class PostDetailFragment : Fragment() {
    private var adapter:postAdapter?=null
    private var postlist:MutableList<Post>?=null
    private var postid:String?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
    val view=inflater.inflate(R.layout.fragment_post_detail, container, false)

        val preference=context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (preference!=null)
        {
            postid=preference.getString("postid",null)
            Log.d("nones1",postid)
        }


        var recyclerView:RecyclerView
        recyclerView=view.findViewById(R.id.recycler_view_post_details)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager=LinearLayoutManager(context)
        recyclerView.layoutManager=linearLayoutManager

        postlist=ArrayList()
        adapter=context?.let{ postAdapter(it,postlist as ArrayList)}
        recyclerView.adapter=adapter
        retrivepost()


        return view
    }
    private fun retrivepost() {
        val postref= FirebaseDatabase.getInstance().reference.child("Posts").child(postid.toString())

        postref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                postlist?.clear()


                    val post = p0.getValue(Post::class.java)

                    val publishername=p0.child("publishername").getValue().toString()
                    val descritpion=p0.child("description").getValue().toString()
                    val postimage=p0.child("postimage").getValue().toString()
                    val postid=p0.child("postid").getValue().toString()
                    Log.d("yep",publishername)



                            postlist!!.add(Post(postid,postimage,publishername,descritpion))



                        adapter?.notifyDataSetChanged()



            }

        })

    }


}
