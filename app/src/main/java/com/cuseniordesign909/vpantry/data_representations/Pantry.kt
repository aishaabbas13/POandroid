package com.cuseniordesign909.vpantry.data_representations

import java.util.*
import kotlin.collections.ArrayList

class Pantry(){
    var _id : String? = null
    var name : String? = null
    var administrators : ArrayList<String>? = null
    var users : ArrayList<String>? = null
    var creationDate : Date? = null
    var admin : Boolean? = null
    var mode : Int? = null
    var deleteUsers :ArrayList<String>? = null
    constructor(_name : String, _admins : ArrayList<String>?, _users : ArrayList<String>?, id : String?):this(){
        _id = id
        name = _name
        administrators = _admins
        users = _users
    }
    constructor(_name : String, _admins : ArrayList<String>?, _users : ArrayList<String>?, id : String?, _deleteUsers : ArrayList<String>) : this(){
        _id = id
        name = _name
        administrators = _admins
        users = _users
        deleteUsers = _deleteUsers
    }
}