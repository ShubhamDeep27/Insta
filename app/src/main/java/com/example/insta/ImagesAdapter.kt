package com.example.insta

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.insta.fragments.PostDetailFragment
import com.squareup.picasso.Picasso

class ImagesAdapter(private val mContext:Context,private val mPost:List<Post>) :RecyclerView.Adapter<ImagesAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesAdapter.ViewHolder {
        val view=LayoutInflater.from(mContext).inflate(R.layout.images_item_layout,parent,false)
        return     ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ImagesAdapter.ViewHolder, position: Int) {

        val postlist=mPost[position]
        Log.d("yepso",postlist.getPostImage().toString())
        Picasso.get().load(postlist.getPostImage()).into(holder.postimage)

        holder.postimage.setOnClickListener {

            val editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            editor.putString("postid",postlist.getPostId())
            editor.apply()
            Log.d("nones",postlist.getPostId())
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment,PostDetailFragment()).commit()
        }
    }


    inner class ViewHolder(itemview:View):RecyclerView.ViewHolder(itemview){

        var postimage:ImageView


        init {
            postimage=itemview.findViewById(R.id.post_image)

        }




    }
}