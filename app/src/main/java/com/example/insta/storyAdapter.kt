package com.example.insta

import android.content.Context

class storyAdapter {



    private var imgurl:String=""
    private var timestart:Long=0
    private var timeend:Long=0
    private var storyid:String=""
    private var userid:String=""


 constructor()
    constructor(imgurl: String, timestart: Long, timeend: Long, storyid: String, userid: String) {
        this.imgurl = imgurl
        this.timestart = timestart
        this.timeend = timeend
        this.storyid = storyid
        this.userid = userid
    }
    fun getimgUrl():String{
        return imgurl

    }
    fun gettimeStart():Long{
        return timestart

    }
    fun gettimeEnd():Long{
        return timeend

    }
    fun getstoryId():String{
        return storyid

    }
    fun getuserId():String{
        return userid

    }
    fun setimgUrl(imgurl: String){
        this.imgurl=imgurl

    }
    fun settimeStart(timestart: Long){
        this.timestart=timestart

    }
    fun settimeend(timeend: Long){
        this.timeend=timeend

    }
    fun setstoryId(storyid: String){
        this.storyid=storyid
    }
    fun setuserId(userid: String){
        this.userid=userid
    }





}