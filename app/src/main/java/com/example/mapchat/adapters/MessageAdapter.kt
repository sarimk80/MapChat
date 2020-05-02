package com.example.mapchat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mapchat.R
import com.example.mapchat.databinding.*
import com.example.mapchat.model.Messages


class MessageAdapter(
    private val context: Context,
    private val messageList: List<Messages>,
    private val userId: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val USER_MESSAGE_BINDING: Int = 1
    private val FRIEND_MESSAGE_BINDING: Int = 2
    private val USER_GIF_BINDING: Int = 3
    private val FRIEND_GIF_BINDING: Int = 4
    private val ERROR: Int = 5

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        if (viewType == 1) {
            val messageBinding: MessageBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.messages_display, parent, false)
            return MessageView(messageBinding)
        } else if (viewType == 2) {
            val messageUserBinding: MessageUserBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.message_chat_user, parent, false)
            return MessageUserView(messageUserBinding)
        } else if (viewType == 3) {
            val gifBinding: GifBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.gif_display, parent, false)
            return GifView(gifBinding)
        } else if (viewType == 4) {
            val gifUserBinding: GifUserBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.gif_chat_user, parent, false)
            return GifUserView(gifUserBinding)
        } else {
            val loadingBinding: LoadingBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.error_layout, parent, false)
            return Error(loadingBinding)
        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (messageList[position].fromUuid == userId && messageList[position].type == "Text") {
            return FRIEND_MESSAGE_BINDING
        } else if (messageList[position].toUuid == userId && messageList[position].type == "Text") {
            return USER_MESSAGE_BINDING
        } else if (messageList[position].fromUuid == userId && messageList[position].type == "Gif") {
            return FRIEND_GIF_BINDING
        } else if (messageList[position].toUuid == userId && messageList[position].type == "Gif") {
            return USER_GIF_BINDING
        } else {
            return ERROR
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

        when {
            getItemViewType(position) == USER_MESSAGE_BINDING -> {
                val userMessageBinding = holder as MessageView
                userMessageBinding.bind(messageList)
            }
            getItemViewType(position) == FRIEND_MESSAGE_BINDING -> {
                val friendMessageBinding = holder as MessageUserView
                friendMessageBinding.bind(messageList)
            }
            getItemViewType(position) == USER_GIF_BINDING -> {
                val userGifView = holder as GifView
                userGifView.bind(messageList)
            }
            getItemViewType(position) == FRIEND_GIF_BINDING -> {
                val friendGifView = holder as GifUserView
                friendGifView.bind(messageList)
            }
            //        holder.bind(messageList)
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

    class GifView(private val gifBinding: GifBinding) : RecyclerView.ViewHolder(gifBinding.root) {
        fun bind(messages: Messages) {

            this.gifBinding.message = messages
            gifBinding.executePendingBindings()
        }

    }

    class GifUserView(private val gifUserBinding: GifUserBinding) :
        RecyclerView.ViewHolder(gifUserBinding.root) {
        fun bind(messages: Messages) {
            this.gifUserBinding.message = messages
            gifUserBinding.executePendingBindings()
        }

    }

    class Error(private val loadingBinding: LoadingBinding) :
        RecyclerView.ViewHolder(loadingBinding.root) {
        fun bind(messages: Messages) {
            this.loadingBinding.messages = messages
            loadingBinding.executePendingBindings()
        }

    }

}