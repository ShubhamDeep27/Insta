package com.example.insta

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class commentAdapter(private val mContext:Context,
                     private val mComment: MutableList<CommentModelclass>):RecyclerView.Adapter<commentAdapter.ViewHolder>()

{
    private var firebaseUser:FirebaseUser?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): commentAdapter.ViewHolder {

        val view=LayoutInflater.from(mContext).inflate(R.layout.commentslayout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mComment.size
    }

    override fun onBindViewHolder(holder: commentAdapter.ViewHolder, position: Int) {

        firebaseUser=FirebaseAuth.getInstance().currentUser
        val coment=mComment[position]
          holder.comments.text=coment.getcomment()

       getuserinfo(holder.imageprofile,holder.username,coment.getpublisher())
    }

    private fun getuserinfo(
        imageprofile: CircleImageView,
        username: TextView,
        getpublisher: String
    ) {

        val userref=FirebaseDatabase.getInstance().reference.child("Users")
            .child(getpublisher)


        userref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                {
                    val user=p0.getValue(User::class.java)

                    Picasso.get().load(user?.getImage()).placeholder(R.drawable.profile).into(imageprofile)
                    username.text=user?.getUsername()
                }
            }
        })



    }


    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var imageprofile:CircleImageView
        var comments:TextView
        var username:TextView
       init {

           imageprofile=itemView.findViewById(R.id.user_profile_image_comment)
          comments=itemView.findViewById(R.id.comment_text)
          username=itemView.findViewById(R.id.user_name_comment)
       }
    }

}