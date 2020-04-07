package com.example.mapchat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mapchat.R
import com.example.mapchat.databinding.MessageBinding
import com.example.mapchat.databinding.MessageUserBinding
import com.example.mapchat.model.Messages

class MessageAdapter(
    private val context: Context,
    private val messageList: List<Messages>,
    private val userId: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val USER_MESSAGE_BINDING: Int = 1
    private val FRIEND_MESSAGE_BINDING: Int = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return if (viewType == 1) {
            val messageBinding: MessageBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.messages_display, parent, false)
            MessageView(messageBinding)
        } else {
            val messageUserBinding: MessageUserBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.message_chat_user, parent, false)
            MessageUserView(messageUserBinding)
        }


    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].fromUuid == userId) {
            USER_MESSAGE_BINDING
        } else {
            FRIEND_MESSAGE_BINDING

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val oldMessage = messageList


        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(
            MessageDiffCallBack(
                oldMessage,
                messageList
            )
        )

        val messageList = messageList[position]
        diffResult.dispatchUpdatesTo(this)

        if (getItemViewType(position) == USER_MESSAGE_BINDING) {
            val userMessageBinding = holder as MessageView
            userMessageBinding.bind(messageList)
        } else {
            val friendMessageBinding = holder as MessageUserView
            friendMessageBinding.bind(messageList)
        }
//        holder.bind(messageList)
    }


    class MessageDiffCallBack(
        private var oldMessageList: List<Messages>,
        private var newMessageList: List<Messages>
    ) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return (oldMessageList[oldItemPosition].date == newMessageList[newItemPosition].date)
        }

        override fun getOldListSize(): Int {
            return oldMessageList.size
        }

        override fun getNewListSize(): Int {
            return newMessageList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldMessageList[oldItemPosition] == newMessageList[newItemPosition]
        }

    }


    class MessageView(private val messageBinding: MessageBinding) :
        RecyclerView.ViewHolder(messageBinding.root) {
        fun bind(messages: Messages) {
            this.messageBinding.message = messages
            messageBinding.executePendingBindings()
        }
    }

    class MessageUserView(private val messageUserBinding: MessageUserBinding) :
        RecyclerView.ViewHolder(messageUserBinding.root) {
        fun bind(messages: Messages) {
            this.messageUserBinding.message = messages
            messageUserBinding.executePendingBindings()
        }
    }
}