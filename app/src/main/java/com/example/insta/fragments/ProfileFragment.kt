package com.example.insta.fragments


import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.insta.*

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.io.PipedOutputStream
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
    private lateinit var profileid: String
    private lateinit var firebaseuser: FirebaseUser
    var postlist:List<Post>?=null
    var imagesAdapter:ImagesAdapter?=null
    var postlistSaved:List<Post>?=null
    var mysavedImg:List<String>?=null
    var saveimageadapter:ImagesAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)


        val pref = context?.getSharedPreferences("PREF", Context.MODE_PRIVATE)

        if (pref != null) {

            this.profileid = pref.getString("profileid", "none")!!
        Log.d("pig1",profileid)
        }
        firebaseuser = FirebaseAuth.getInstance().currentUser!!
        if (profileid == firebaseuser.uid) {
            view.edit_account_button.text = "Edit Profile"
        } else if (profileid != firebaseuser.uid) {
            checkFollowandFollowingbutton()

        }

//uploaded image
         val recyclerViewUploadpic:RecyclerView
        recyclerViewUploadpic=view.findViewById(R.id.recycler_view_upload_pic)
        recyclerViewUploadpic.setHasFixedSize(true)
        val linearLayoutManager:LinearLayoutManager
        linearLayoutManager=GridLayoutManager(context,3)
       recyclerViewUploadpic.layoutManager=linearLayoutManager

        postlist=ArrayList()

        imagesAdapter=context?.let { ImagesAdapter(it,postlist as ArrayList) }
        recyclerViewUploadpic.adapter=imagesAdapter
//save image

        val recyclerViewSavepic:RecyclerView
        recyclerViewSavepic=view.findViewById(R.id.recycler_view_save_pics)
        recyclerViewUploadpic.setHasFixedSize(true)
        val linearLayoutManagersave:LinearLayoutManager
        linearLayoutManagersave=GridLayoutManager(context,3)
        recyclerViewSavepic.layoutManager=linearLayoutManagersave

        postlistSaved=ArrayList()

        saveimageadapter=context?.let { ImagesAdapter(it,postlistSaved as ArrayList) }
        recyclerViewSavepic.adapter=saveimageadapter


        recyclerViewSavepic.visibility=View.GONE
        recyclerViewUploadpic.visibility=View.VISIBLE


      var uploadimagebutton:ImageButton
        uploadimagebutton=view.findViewById(R.id.image_grid_view_butn)
        uploadimagebutton.setOnClickListener{

            recyclerViewSavepic.visibility=View.GONE
            recyclerViewUploadpic.visibility=View.VISIBLE

        }
        var saveimagebutton:ImageButton
        saveimagebutton=view.findViewById(R.id.image_save_butn)
        saveimagebutton.setOnClickListener {

            recyclerViewSavepic.visibility = View.VISIBLE
            recyclerViewUploadpic.visibility=View.GONE
        }
        view.total_follower.setOnClickListener {
               val intent=Intent(context,ShowUser::class.java)
               intent.putExtra("id",profileid)
            Log.d("nopes3",profileid)
               intent.putExtra("title","Followers")
               startActivity(intent)
           }

        view.total_following.setOnClickListener {
            val intent=Intent(context,ShowUser::class.java)
            intent.putExtra("id",profileid)
            intent.putExtra("title","Following")
            startActivity(intent)
        }



        view.edit_account_button.setOnClickListener {
          val getbuttontext=view?.edit_account_button?.text.toString()

            when{

                getbuttontext=="Edit Profile"-> startActivity(Intent(context,AccountSetting::class.java))

                getbuttontext=="Follow"-> {

                    firebaseuser.uid.let { it ->
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(it.toString())
                            .child("Following").child(profileid).setValue(true)
                    }


                    firebaseuser.uid.let { it ->
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(profileid)
                            .child("Followers").child(it.toString()).setValue(true)
                    }
                    addnotification()
                }
                getbuttontext=="Following"-> {

                    firebaseuser.uid.let { it ->
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(it.toString())
                            .child("Following").child(profileid).removeValue()
                    }


                    firebaseuser.uid.let { it ->
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(profileid)
                            .child("Followers").child(it.toString()).removeValue()
                    }
                }
            }
        }
        getFollower()
        getFollowing()
        userinfo()
        gettotalnoofpost()
        myphotos()
        mysaves()
        return view
    }

    private fun mysaves() {

        mysavedImg=ArrayList()

        val saveref=FirebaseDatabase.getInstance().reference
            .child("Saves")
            .child(firebaseuser.uid)

        saveref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
               if (p0.exists())
               {
                   for (snapshot in p0.children)
                   {

                       (mysavedImg as ArrayList<String>).add(snapshot.key.toString()!!)
                   }
                   readsaveimagedata()


               }
            }


        })














    }

    private fun readsaveimagedata() {


        val postref=FirebaseDatabase.getInstance().reference.child("Posts")

postref.addValueEventListener(object :ValueEventListener{
    override fun onCancelled(p0: DatabaseError) {

    }

    override fun onDataChange(p0: DataSnapshot) {
        if (p0.exists())
        {

            (postlistSaved as ArrayList<Post>).clear()

            for (snapshot in p0.children)
            {

                val publishername=snapshot.child("publishername").getValue().toString()
                val descritpion=snapshot.child("description").getValue().toString()
                val postimage=snapshot.child("postimage").getValue().toString()
                val postid=snapshot.child("postid").getValue().toString()
                Log.d("nopes1",postid)

                for (key in mysavedImg!!)
                {
                    if (postid==key)
                    {
                        Log.d("nopes2", (key in mysavedImg!!).toString())
                        (postlistSaved as ArrayList<Post>).add(Post(postid,postimage,publishername,descritpion))
                    }

                }
            }
       saveimageadapter?.notifyDataSetChanged()
        }
    }
})







    }


    private fun checkFollowandFollowingbutton() {
        val followingref = firebaseuser.uid.let {it
            FirebaseDatabase.getInstance().reference.child("Follow").child(it.toString())
                .child("Following")
        }

        if (followingref != null) {
            followingref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    if (p0.child(profileid).exists()) {
                        view?.edit_account_button?.text = "Following"

                    } else {
                        view?.edit_account_button?.text = "Follow"
                    }
                }


            })
        }
    }

    private fun getFollower() {
        val followerref =
            FirebaseDatabase.getInstance().reference.child("Follow").child(profileid)
                .child("Followers")


        followerref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
              if(p0.exists())
              {
                  view?.total_follower?.text= p0.childrenCount.toString()
              }
            }

        })

    }
    private fun getFollowing() {
        val followingref =
            FirebaseDatabase.getInstance().reference.child("Follow").child(profileid)
                .child("Following")


        followingref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {

                    view?.total_following?.text= p0.childrenCount.toInt().toString()
                }
            }

        })

    }



    private fun myphotos(){

        val postref=FirebaseDatabase.getInstance().reference.child("Posts")

        postref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                ( postlist as ArrayList<Post>).clear()
              if (p0.exists())
              {


                  for (snapshot in p0.children)
                  {
                 //     val post=snapshot.getValue(Post::class.java)
                      val publishername=snapshot.child("publishername").getValue().toString()

                      if(publishername==profileid)
                      {


                          val descritpion=snapshot.child("description").getValue().toString()
                          val postimage=snapshot.child("postimage").getValue().toString()
                          val postid=snapshot.child("postid").getValue().toString()
                          Log.d("yepss",postimage)
                          (postlist as ArrayList<Post>).add(Post(postid,postimage,publishername,descritpion))
                      }
                      (postlist as ArrayList<Post>).reverse()

                      imagesAdapter?.notifyDataSetChanged()
                  }


              }
            }
        })







    }


    private fun userinfo()
    {

          val dref=FirebaseDatabase.getInstance().getReference("Users").child(profileid)
        dref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if(p0.exists())
                {

                    val user= p0.getValue(User::class.java)
                    Log.d("dog2",user?.getUID().toString())
                 Picasso.get().load(user?.getImage()).placeholder(R.drawable.profile).into(view?.or_image_profile_frag)
                    view?.profile_fragment_username?.text=user?.getUsername()
                    view?.full_profile_name?.text=user?.getFullname()
                      view?.bio_profile?.text=user?.getBio()
                 }
            }


        })

    }

    @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
    override fun onStop() {
        super.onStop()
        val pref=context?.getSharedPreferences("PREF",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileid",firebaseuser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

    }

    @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
    override fun onDestroy() {
        super.onDestroy()
        val pref=context?.getSharedPreferences("PREF",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileid",firebaseuser.uid)
        pref?.apply()
    }

    private fun gettotalnoofpost(){




        val postref=FirebaseDatabase.getInstance().reference.child("Posts")
        postref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {



                if (p0.exists())
                {

                    var postcount=0
                    for (snapshot in p0.children)
                    {

                        val publishername=snapshot.child("publishername").getValue().toString()

                        if (publishername==profileid)
                        {
                            postcount++
                        }

                      total_posts.text=""+postcount
                    }
                }
            }


        })






    }
    private fun addnotification(){


        val notiref=FirebaseDatabase.getInstance().reference.child("Notifications")
            .child(profileid)

        val notimap=HashMap<String,Any>()
        notimap["userid"]=firebaseuser?.uid.toString()
        notimap["text"]="started following you"
        notimap["postid"]=""
        notimap["ispost"]=false


        notiref.push().setValue(notimap)





    }


}
