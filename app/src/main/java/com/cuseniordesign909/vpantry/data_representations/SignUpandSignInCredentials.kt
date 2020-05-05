package com.cuseniordesign909.vpantry.data_representations

class SignUpandSignInCredentials() {
    var email: String = ""
    var password: String = ""
    var given_name : String = ""
    var family_name : String = ""
    var nickname : String = ""
    var password2 : String = ""
    var old_password : String = ""
    constructor(_email : String, _password : String):this() {
        email = _email
        password = _password
    }
    constructor(_old_password:String, _password: String, _password2:String) : this(){
        old_password = _old_password
        password = _password
        password2 = _password2
    }
    constructor( _email : String,  _password : String, _given_name : String,  _family_name : String, _nickname : String) : this(){
        email = _email
        password = _password
        given_name = _given_name
        family_name = _family_name
        nickname = _nickname
    }
    constructor( _email : String,  _password : String, _given_name : String,  _family_name : String, _nickname : String, _password2 : String) : this(){
        email = _email
        password = _password
        given_name = _given_name
        family_name = _family_name
        nickname = _nickname
        password2 = _password2
    }
}