package com.example.mapchat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mapchat.R
import com.example.mapchat.databinding.MessageBinding
import com.example.mapchat.model.Messages

class MessageAdapter(private val context: Context, private val messageList: List<Messages>) :
    RecyclerView.Adapter<MessageAdapter.MessageView>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageView {
        val layoutInflater = LayoutInflater.from(parent.context)
        val messageBinding: MessageBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.messages_display, parent, false)
        return MessageView(messageBinding)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: MessageView, position: Int) {

        val oldMessage = messageList


        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(
            MessageDiffCallBack(
                oldMessage,
                messageList
            )
        )

        val messageList = messageList[position]
        diffResult.dispatchUpdatesTo(this)

        holder.bind(messageList)
    }


    class MessageDiffCallBack(
        var oldMessageList: List<Messages>,
        var newMessageList: List<Messages>
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
}