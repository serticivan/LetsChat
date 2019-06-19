package com.example.serticivan.letschat.models

class User(val uid: String, val username: String, val profileImageUrl: String) {
    constructor() : this("","","")
}