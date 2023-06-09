package com.example.mongodb.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId


class Data:RealmObject{

    @PrimaryKey
    var id:ObjectId = ObjectId.invoke()
    var inputString1:String =""
    var inputString2:String =""
}
