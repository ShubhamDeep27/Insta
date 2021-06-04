package com.example.insta.fragments


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.insta.R
import com.example.insta.User
import com.example.insta.usersearchAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.view.*

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var adapter: usersearchAdapter? = null
    private  var mUser1:MutableList<User>?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)


       recyclerView=view.findViewById(R.id.recycler_view_search)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager=LinearLayoutManager(context)


        mUser1= ArrayList()
        adapter=context?.let { usersearchAdapter(it, mUser1 as ArrayList<User>,true) }
        recyclerView?.adapter=adapter



        view.search_edit_text.addTextChangedListener(object :TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (view.search_edit_text.text.toString()=="")
                {


                }
                else
                {
                    recyclerView?.visibility=View.VISIBLE
                    retriveuser()
                    searchuser(s.toString().toLowerCase())
                }
            }
            override fun afterTextChanged(s: Editable?) {

            }


        })


        return view
    }

    private fun searchuser(input:String) {
        val query=FirebaseDatabase.getInstance().getReference().child("Users")
            .orderByChild("fullname")
            .startAt(input)
            .endAt(input + "\uf8ff")
        query.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {


                mUser1?.clear()
                for (snapshot in p0.children)

                {

                    val user=snapshot.getValue(User::class.java)
                    val fullname=snapshot.child("fullname").getValue().toString()
                    val username=snapshot.child("username").getValue().toString()

                    val bio=snapshot.child("bio").getValue().toString()
                    val image=snapshot.child("image").getValue().toString()
                    val uid=snapshot.child("uid").getValue().toString()
                    User(username,fullname,bio,image,uid)

                    if (user!=null && uid!=FirebaseAuth.getInstance().currentUser?.uid)
                    {

                        mUser1?.add(User(username,fullname,bio,image,uid))
                    }
                }
                adapter?.notifyDataSetChanged()
            }


        })




    }

    private  fun retriveuser()
    {
        val dref=FirebaseDatabase.getInstance().getReference().child("Users")
        dref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if (view?.search_edit_text?.text.toString()=="")
                {
                    mUser1?.clear()
                    for (snapshot in p0.children)
                    {
                        val user=snapshot.getValue(User::class.java)

                        val fullname=snapshot.child("fullname").getValue().toString()
                        val username=snapshot.child("username").getValue().toString()
                        val bio=snapshot.child("bio").getValue().toString()
                        val image=snapshot.child("image").getValue().toString()
                        val uid=snapshot.child("uid").getValue().toString()

                        User(username,fullname,bio,image,uid)
                        if (user!=null)
                        {
                            mUser1?.add(User(username,fullname,bio,image,uid))


                        }
                    }
                    adapter?.notifyDataSetChanged()
                }
            }


        })
    }

}

