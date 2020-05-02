package com.example.mapchat.helper

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mapchat.R
import java.text.SimpleDateFormat


@SuppressLint("SimpleDateFormat")
@BindingAdapter("app:text")
fun formatDate(view: TextView, date: String?) {

    if (date != null) {
        val timeStamp = date.toLong()
        val formatter = SimpleDateFormat("MM/dd/yy hh:mm a")
        view.text = formatter.format(timeStamp).toString()
    } else {
        view.text = "Jan-05-5020"
    }

}

@BindingAdapter("app:imageUrl")
fun loadImage(view: ImageView, imageUrl: String?) {

    Glide.with(view.context).load(imageUrl)
        .apply(RequestOptions.circleCropTransform())
        .error(R.drawable.ic_person_black_24dp)
        .placeholder(R.drawable.ic_person_black_24dp)
        .into(view)


}

@BindingAdapter("app:gif")
fun loadGif(view: ImageView, imageUrl: String?) {
    Glide.with(view.context).asGif().load(imageUrl)
        .placeholder(R.drawable.ic_round_emoji_emotions_24).into(view)
}