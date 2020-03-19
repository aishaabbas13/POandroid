package com.cuseniordesign909.vpantry.data_representations

class ResponseStatus {
    var success : Boolean? = true
    var message : String? = null
    var given_name : String? = null
    var group_name : String? = null
    var email : String? = null
    var password : String? = null
    var token : String? = null
    var password2 : String? = null
    var old_password : String? = null
    var id : String? = null
    var admins : ArrayList<String>? = null
    var users : ArrayList<String>? = null
    var pantries : ArrayList<Pantry>? = null
    var user_id : String? = null
    var items : ArrayList<Item>? = null
    var pantry : Pantry? = null
}