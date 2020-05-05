package com.cuseniordesign909.vpantry.data_representations

import java.util.*

class Item(_name : String, _location : String, _type : String, _quantity : String, _expirationDate : Long, _note : String, _groupId : String){
    var name = _name
    var location = _location
    var type = _type
    var quantity = _quantity
    var expirationDate = _expirationDate
    var note = _note
    var groupId = _groupId
    var message : String? = null
    var success : Boolean? = null
    var owner : String? = null
    var _id : String? = null
}