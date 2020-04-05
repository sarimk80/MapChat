package com.example.mapchat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.mapchat.R
import com.example.mapchat.databinding.Friendmessage
import com.example.mapchat.event.FriendListEvent
import com.example.mapchat.helper.getCity
import com.example.mapchat.model.UserMessages

class FriendMessageAdapter(
    private val context: Context,
    private val friendMessageList: List<UserMessages>,
    private val parentFragment: Fragment
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
                    parentFragment.findNavController()
                        .navigate(
                            R.id.action_userFragment_to_chatFragment,
                            bundleOf("FriendId" to friendmessage.friends?.friendUser!!.uuid)
                        )
                }

            }

        }
        return BindMessage(friendmessage, context)
    }

    override fun getItemCount(): Int {
        return friendMessageList.size
    }

    override fun onBindViewHolder(holder: BindMessage, position: Int) {
        val friendList = friendMessageList[position]
        holder.bind(friendList)
    }


    class BindMessage(private val friendmessage: Friendmessage, private val context: Context) :
        RecyclerView.ViewHolder(friendmessage.root) {

        fun bind(userMessages: UserMessages) {
            this.friendmessage.friends = userMessages
            this.friendmessage.city = getCity(
                userMessages.friendUser?.latitude,
                userMessages.friendUser?.longitude,
                context
            )
            friendmessage.executePendingBindings()
        }
    }
}