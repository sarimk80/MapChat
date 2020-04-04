package com.example.mapchat.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mapchat.R
import com.example.mapchat.databinding.Friendmessage
import com.example.mapchat.event.FriendListEvent
import com.example.mapchat.model.UserMessages

class FriendMessageAdapter(
    private val context: Context,
    private val friendMessageList: List<UserMessages>
) : RecyclerView.Adapter<FriendMessageAdapter.BindMessage>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindMessage {
        val layoutInflater = LayoutInflater.from(parent.context)
        val friendmessage: Friendmessage =
            DataBindingUtil.inflate(layoutInflater, R.layout.user_fragment_messages, parent, false)
        friendmessage.event = object : FriendListEvent {
            override fun onFriendClick() {
                if (friendmessage.friends?.friendUser != null) {
                    Log.i("Adapter", "onFriendClick: ${friendmessage.friends?.friendUser!!.name}")
                }

            }

        }
        return BindMessage(friendmessage)
    }

    override fun getItemCount(): Int {
        return friendMessageList.size
    }

    override fun onBindViewHolder(holder: BindMessage, position: Int) {
        val friendList = friendMessageList[position]
        holder.bind(friendList)
    }


    class BindMessage(private val friendmessage: Friendmessage) :
        RecyclerView.ViewHolder(friendmessage.root) {

        fun bind(userMessages: UserMessages) {
            this.friendmessage.friends = userMessages
            friendmessage.executePendingBindings()
        }
    }
}