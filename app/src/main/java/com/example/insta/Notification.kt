package com.example.insta

class Notification {

    private var userid:String=""
    private var text:String=""
    private var postid:String=""
    private var  ispost=false


    constructor()
    constructor(userid: String, text: String, postid: String, ispost: Boolean) {
        this.userid = userid
        this.text = text
        this.postid = postid
        this.ispost = ispost
    }


    fun getuserID():String{
        return userid

    }
    fun gettext():String{
        return text

    }
    fun getpostID():String{
        return postid

    }
    fun getispost():Boolean{
        return ispost

    }
    fun setuserID(userid: String){
        this.userid=userid

    }
    fun settext(text: String){
        this.text=text

    }
    fun setpostID(postid: String){
        this.postid=postid

    }
    fun setispost(ispost: Boolean){
        this.ispost=ispost

    }
}

