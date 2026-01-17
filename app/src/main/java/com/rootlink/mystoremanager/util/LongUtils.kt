package com.rootlink.mystoremanager.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat


@SuppressLint("SimpleDateFormat")
fun Long.toReadableDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy")
    return sdf.format(java.util.Date(this))
}

@SuppressLint("SimpleDateFormat")
fun Long.toReadableDateTime(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a")
    return sdf.format(java.util.Date(this))
}

@SuppressLint("SimpleDateFormat")
fun Long.toReadableTime(): String {
    val sdf = SimpleDateFormat("hh:mm a")
    return sdf.format(java.util.Date(this))
}
