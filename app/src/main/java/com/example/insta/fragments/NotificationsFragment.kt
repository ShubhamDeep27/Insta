package com.example.insta.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.insta.Notification

import com.example.insta.R
import com.example.insta.notifacationAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_show_user.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class NotificationsFragment : Fragment() {

    private var notificationlist:List<Notification>?=null
    private var adapter:notifacationAdapter?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_notifications, container, false)


                var recyclerView:RecyclerView
        recyclerView=view.findViewById(R.id.recycler_view_notifications)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager=LinearLayoutManager(context)

                notificationlist=ArrayList()
        adapter= notifacationAdapter(context!!,notificationlist as ArrayList<Notification>)
        recyclerView.adapter=adapter



        readnotifications()

            return view
    }

    private fun readnotifications() {

        val notiref=FirebaseDatabase.getInstance().reference.child("Notifications")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())


        notiref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
              if (p0.exists())
              {
                  (notificationlist as ArrayList).clear()


                  for (snapshot in p0.children)
                  {

                      val userid=snapshot.child("userid").getValue().toString()
                      val text=snapshot.child("text").getValue().toString()

                      val postid=snapshot.child("postid").getValue().toString()
                      val ispost=snapshot.child("ispost").getValue()
                      (notificationlist as ArrayList).add(Notification(userid,text,postid, ispost as Boolean))
                  }
                  Collections.reverse(notificationlist)
                  adapter?.notifyDataSetChanged()
              }
            }
        })








    }


}
