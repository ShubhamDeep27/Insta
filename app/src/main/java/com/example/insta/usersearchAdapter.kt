package com.example.insta

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity

import androidx.recyclerview.widget.RecyclerView
import com.example.insta.fragments.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.annotations.NotNull

class usersearchAdapter(private var mContext:Context,
                        private var mUser:List<User>,
                        private var isFragment: Boolean=false):RecyclerView.Adapter<usersearchAdapter.ViewHolder>() {

    private var firebaseuser: FirebaseUser? =FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): usersearchAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.userlayout, parent, false)
        return usersearchAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }


    override fun onBindViewHolder(holder: usersearchAdapter.ViewHolder, position: Int) {

        val user = mUser[position]
        Log.d("shit",user.getImage())
        holder.usernametextview.text = user.getUsername()
        holder.fullnametextview.text = user.getFullname()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(holder.imageview)

        checkfollowstatus(user.getImage(),holder.followbutton)

          holder.itemView.setOnClickListener {
              if (isFragment) {
                  val pref = mContext.getSharedPreferences("PREF", Context.MODE_PRIVATE).edit()
                  pref.putString("profileid", user.getImage())
                  Log.d("pig", user.getImage())
                  pref.apply()
                  (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                      .replace(R.id.fragment, ProfileFragment()).commit()
              }
          }
        holder.followbutton.setOnClickListener {

            if (holder.followbutton.text.toString() == "Follow") {
                firebaseuser?.uid.let { it ->
                    FirebaseDatabase.getInstance().reference.child("Follow").child(it.toString())
                        .child("Following").child(user.getImage())
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                firebaseuser?.uid.let { it ->
                                    FirebaseDatabase.getInstance().reference.child("Follow")
                                        .child(user.getImage())
                                        .child("Followers").child(it.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }
                                }
                            }
                        }
                }
                addnotification(user.getImage())
            } else {
                firebaseuser?.uid.let { it ->
                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(it.toString())
                        .child("Following").child(user.getImage())
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseuser?.uid.let { it ->
                                    FirebaseDatabase.getInstance().reference.child("Follow")
                                        .child(user.getImage())
                                        .child("Followers").child(it.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }
                                }
                            }
                        }

                }
            }

        }

    }

    private fun checkfollowstatus(uid: String, followbutton: Button) {
       val followingref= firebaseuser?.uid.let { it ->
            FirebaseDatabase.getInstance().reference.child("Follow").child(it.toString())
                .child("Following")
       }
        followingref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
              if(p0.child(uid).exists())

              {
                  followbutton.text="Following"
              }
                else
              {
                  followbutton.text="Follow"
              }
            }


        })
    }


    class ViewHolder(@NotNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        var usernametextview: TextView = itemView.findViewById(R.id.user_name_search)
        var fullnametextview: TextView = itemView.findViewById(R.id.user_full_name_search)
        var imageview: CircleImageView = itemView.findViewById(R.id.user_profile_image_search)
        var followbutton: Button = itemView.findViewById(R.id.follow_btn_search)


    }
    private fun addnotification(useriD:String){


        val notiref=FirebaseDatabase.getInstance().reference.child("Notifications")
            .child(useriD)

        val notimap=HashMap<String,Any>()
        notimap["userid"]=firebaseuser?.uid.toString()
        notimap["text"]="started following you"
        notimap["postid"]=""
        notimap["ispost"]=false


        notiref.push().setValue(notimap)





    }
}


