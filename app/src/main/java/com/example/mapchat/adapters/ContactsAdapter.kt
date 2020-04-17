package com.example.mapchat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mapchat.R
import com.example.mapchat.databinding.ContactDisplay
import com.example.mapchat.model.Contacts

class ContactsAdapter(private val context: Context, private val contactsList: List<Contacts>) :
    RecyclerView.Adapter<ContactsAdapter.bindMessages>() {
    class bindMessages(private val contactDisplay: ContactDisplay) :
        RecyclerView.ViewHolder(contactDisplay.root) {

        fun bind(contacts: Contacts) {
            this.contactDisplay.contacts = contacts
            contactDisplay.executePendingBindings()
        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactsAdapter.bindMessages {
        val layoutInflater = LayoutInflater.from(parent.context)
        val contactDisplay: ContactDisplay =
            DataBindingUtil.inflate(layoutInflater, R.layout.contacts_display, parent, false)

        return bindMessages(contactDisplay)
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    override fun onBindViewHolder(holder: ContactsAdapter.bindMessages, position: Int) {
        val myContactList = contactsList[position]
        holder.bind(myContactList)
    }

}