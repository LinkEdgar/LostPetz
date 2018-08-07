package com.example.enduser.lostpetz.CustomObjectClasses

import android.os.Parcel
import android.os.Parcelable

/*
This data class represents the information the poster(shelter or user) provide for the pet to be
posted to match with.
 */

data class MatchInfo(var name: String, var url: String): Parcelable {
    //TODO expand params
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MatchInfo> {
        override fun createFromParcel(parcel: Parcel): MatchInfo {
            return MatchInfo(parcel)
        }

        override fun newArray(size: Int): Array<MatchInfo?> {
            return arrayOfNulls(size)
        }
    }
}