package com.example.insta

class Post {

    private var postid:String=""
    private var postimage:String=""
    private var publishername:String=""
    private var description:String=""


    constructor()
    constructor(postid: String, postimage: String, publishername: String, description: String) {
        this.postid = postid
        this.postimage = postimage
        this.publishername = publishername
        this.description = description
    }

    fun getPostId():String{
        return postid

    }
    fun getDescription():String{
        return description

    }
    fun getPostImage():String{
        return postimage

    }
    fun getPublisher():String{
        return publishername

    }
    fun setPostId(postid: String){
        this.postid=postid

    }
    fun setDescription(description: String){
        this.description=description

    }
    fun setPostImage(postimage: String){
         this.postimage=postimage

    }
    fun setPublisher(publishername: String){
         this.publishername=publishername

    }

}