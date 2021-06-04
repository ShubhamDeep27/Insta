package com.example.insta.fragments

import android.content.Context
import android.content.Intent
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.insta.*
import com.google.android.material.animation.AnimationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.postlayout.view.*
import java.security.PrivilegedActionException

class postAdapter(private val mContext:Context,private val mPost:List<Post>): RecyclerView.Adapter<postAdapter.ViewHolder>() {

   private var firebaseUser:FirebaseUser?=null





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(mContext).inflate(R.layout.postlayout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
      return  mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser=FirebaseAuth.getInstance().currentUser



        val post=mPost[position]


        Picasso.get().load(post.getPostImage()).into(holder.postimage)

        if (post.getDescription().equals(""))
        {

            holder.description.visibility=View.GONE
        }
        else
        {
            holder.description.visibility=View.VISIBLE
            holder.description.text=post.getDescription()
        }



        publisherinfo(holder.profileimage,holder.username,holder.publisher,post.getPublisher())

        islike(post.getPostId(),holder.likebutton)

        numberoflikes(holder.likes,post.getPostId())

        gettotalcomments(holder.comment,post.getPostId())

        checksavestatus(post.getPostId(),holder.savebutton)


        holder.likes.setOnClickListener {
            val intent=Intent(mContext,ShowUser::class.java)
            intent.putExtra("id",post.getPostId())
            intent.putExtra("title","Likes")
            Log.d("nopes9",post.getPostId())
            mContext.startActivity(intent)

        }


        holder.likebutton.setOnClickListener {

            if (holder.likebutton.tag=="Like")
            {
                FirebaseDatabase.getInstance().reference.child("Likes")
                    .child(post.getPostId()).child(firebaseUser?.uid.toString()).setValue(true)
                addnotification(post.getPublisher(),post.getPostId())
            }
            else
            {
                FirebaseDatabase.getInstance().reference.child("Likes")
                    .child(post.getPostId()).child(firebaseUser?.uid.toString()).removeValue()

                val intent= Intent(mContext,MainActivity::class.java)
                mContext.startActivity(intent)
            }
        }


        holder.commentbutton.setOnClickListener {

            val intent= Intent(mContext,Comment::class.java)
            intent.putExtra("postid",post.getPostId())
            intent.putExtra("publisherid",post.getPublisher())
            mContext.startActivity(intent)
        }
        holder.comment.setOnClickListener {

            val intent= Intent(mContext,Comment::class.java)
            intent.putExtra("postid",post.getPostId())
            intent.putExtra("publisherid",post.getPublisher())
            mContext.startActivity(intent)
        }


        holder.savebutton.setOnClickListener {
          if (holder.savebutton.tag=="Save")
          {
              FirebaseDatabase.getInstance().reference.child("Saves")
                  .child(firebaseUser?.uid.toString()).child(post.getPostId()).setValue(true)

          }

            else
          {
              FirebaseDatabase.getInstance().reference.child("Saves")
                  .child(firebaseUser?.uid.toString()).child(post.getPostId()).removeValue()
          }

        }


    }

    private fun numberoflikes(likes: TextView, postId: String) {

        val likeref= FirebaseDatabase.getInstance().reference.child("Likes")
            .child(postId)


        likeref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                  likes.text=p0.childrenCount.toString()+ " likes"
                }


            }


        })




    }

    private fun islike(postId: String, likebutton: ImageView) {


        val firebaseUser=FirebaseAuth.getInstance().currentUser


       val likeref= FirebaseDatabase.getInstance().reference.child("Likes")
           .child(postId)


        likeref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(firebaseUser!!.uid).exists()) {
                    likebutton.setImageResource(R.drawable.heart_clicked)
                    likebutton.tag = "Liked"
                } else
            {
                likebutton.setImageResource(R.drawable.heart_not_clicked)
                likebutton.tag = "Like"
            }


            }


        })

    }

    private fun publisherinfo(
        profileimage: CircleImageView,
        username: TextView,
        publisher: TextView,
        publisherID: String
    ) {
        val userref=FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)
        userref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                {
                    val user = p0.getValue(User::class.java)

                    Picasso.get().load(user?.getImage()).placeholder(R.drawable.profile).into(profileimage)
                    username.text=user?.getUsername()

                    publisher.setText(user?.getFullname())

                }
            }


        })
    }
    private fun gettotalcomments(comment: TextView, postId: String) {

        val commentref= FirebaseDatabase.getInstance().reference.child("Comments")
            .child(postId)


        commentref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    comment.text="view all "+ p0.childrenCount.toString()+ " comments"
                }


            }


        })




    }


    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        var profileimage: CircleImageView
        var postimage: ImageView
        var likebutton: ImageView
        var commentbutton: ImageView
        var savebutton: ImageView
        var username: TextView
        var likes: TextView
        var comment: TextView
        var description: TextView
        var publisher: TextView

        init {


            profileimage = itemView.findViewById(R.id.user_profile_image_post)
            postimage = itemView.findViewById(R.id.post_image_home)
            likebutton = itemView.findViewById(R.id.post_image_like_btn)
            commentbutton = itemView.findViewById(R.id.post_image_comment_btn)
            savebutton = itemView.findViewById(R.id.post_save_btn)
            username = itemView.findViewById(R.id.user_name_post)
            likes = itemView.findViewById(R.id.likes)
            comment = itemView.findViewById(R.id.comments)
            description = itemView.findViewById(R.id.description)
            publisher = itemView.findViewById(R.id.publisher)
        }


    }
    private fun checksavestatus(postId: String,imageView: ImageView){


        val saveref=FirebaseDatabase.getInstance().reference.child("Saves")
            .child(firebaseUser?.uid.toString())

        saveref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(postId).exists())

                {
                    imageView.setImageResource(R.drawable.save_large_icon)
                    imageView.tag="Saved"
                }
                else
                {
                    imageView.setImageResource(R.drawable.save_unfilled_large_icon)
                    imageView.tag="Save"
                }


            }
        })





    }
    private fun addnotification(useriD:String,postId: String){


        val notiref=FirebaseDatabase.getInstance().reference.child("Notifications")
            .child(useriD)

        val notimap=HashMap<String,Any>()
        notimap["userid"]=firebaseUser?.uid.toString()
        notimap["text"]="like your post"
        notimap["postid"]=postId
        notimap["ispost"]=true


        notiref.push().setValue(notimap)





    }

}