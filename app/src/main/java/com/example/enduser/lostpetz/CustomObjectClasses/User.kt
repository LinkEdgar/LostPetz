package com.example.enduser.lostpetz.CustomObjectClasses

import android.os.Parcel
import android.os.Parcelable

open class User(var userName: String?, var profileUrl: String?, var email:String?, var lastMessage:String?, var chatId: String?):Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(chatId)
        parcel.writeString(email)
        parcel.writeString(userName)
        parcel.writeString(profileUrl)
        parcel.writeString(email)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}