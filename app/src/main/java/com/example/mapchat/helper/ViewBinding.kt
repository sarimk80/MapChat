package com.example.mapchat.helper

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.mapchat.R
import java.text.SimpleDateFormat


//@SuppressLint("SimpleDateFormat")
//@BindingAdapter("app:text")
//fun formateDate(view: TextView, date: String?) {
//
//    if (date != null) {
//        val formatter = SimpleDateFormat("MM/dd/yy HH:mm aa")
//        view.text = formatter.parse(date)!!.toString()
//    } else {
//        view.text = "Jan-05-5020"
//    }
//
//}

@BindingAdapter("app:imageUrl")
fun loadImage(view: ImageView, imageUrl: String?) {

    Glide.with(view.context).load(imageUrl)
        .error(R.drawable.ic_person_black_24dp)
        .placeholder(R.drawable.ic_person_black_24dp)
        .into(view)


}