package com.example.insta

import android.content.Context
import android.content.ReceiverCallNotAllowedException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.insta.fragments.PostDetailFragment
import com.example.insta.fragments.ProfileFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class notifacationAdapter(private val mContext:Context,private val mNotification:List<Notification>)
    :RecyclerView.Adapter<notifacationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): notifacationAdapter.ViewHolder {

        val view=LayoutInflater.from(mContext).inflate(R.layout.notification_item_layout,parent,false)
        return ViewHolder(view)


    }

    override fun getItemCount(): Int {
       return mNotification.size
    }

    override fun onBindViewHolder(holder: notifacationAdapter.ViewHolder, position: Int) {






        val notification=mNotification[position]




        if (notification.gettext().equals("started following you"))
        {
            holder.text.text="started following you"

        }
        else if (notification.gettext().equals("like your post"))
        {
            holder.text.text="like your post"

        }
        else if (notification.gettext().contains("commented:"))
        {
            holder.text.text=notification.gettext().replace("commented:","commented: ")

        }
        else
        {

            holder.text.text=notification.gettext()
        }
        userinfo(holder.profileImage,holder.username,notification.getuserID())

        if (notification.getispost()) {
            holder.postImage.visibility = View.VISIBLE
            getpostimage(holder.postImage,notification.getpostID())

        }
        else
        {
            holder.postImage.visibility = View.GONE

        }

holder.itemView.setOnClickListener {
    if(notification.getispost())
    {
        val editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
        editor.putString("postid",notification.getpostID())
        editor.apply()
        Log.d("nones",notification.getpostID())
        (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment,
            PostDetailFragment()
        ).commit()

    }
    else
    {
        val editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
        editor.putString("profileid",notification.getuserID())
        editor.apply()
        Log.d("nones",notification.getuserID())
        (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment,
            ProfileFragment()
        ).commit()

    }


}



    }

    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    {
       var postImage:ImageView
        var profileImage: CircleImageView
        var text: TextView
        var username: TextView
        init {

            postImage=itemView.findViewById(R.id.notification_post_image)
            profileImage=itemView.findViewById(R.id.notification_profile_image)
            username=itemView.findViewById(R.id.username_notification)
            text=itemView.findViewById(R.id.comment_notification)
        }
    }
    private fun userinfo(imageView: ImageView,username:TextView,publisherId:String)
    {

        val userref= FirebaseDatabase.getInstance().getReference("Users").child(publisherId)
        userref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if(p0.exists())
                {

                    val user= p0.getValue(User::class.java)
                    Log.d("dog2",user?.getUID().toString())
                    Picasso.get().load(user?.getImage()).placeholder(R.drawable.profile).into(imageView)
                 username?.text=user?.getUsername()

                }
            }


        })

    }
    private fun getpostimage(imageView: ImageView,postId:String) {

        val postref = FirebaseDatabase.getInstance().reference.child("Posts").child(postId)


        postref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {

                    val post = p0.getValue(Post::class.java)

                    Picasso.get().load(post?.getPostImage()).placeholder(R.drawable.profile).into(imageView)

                }
            }


        })

    }
}