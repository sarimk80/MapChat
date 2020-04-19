package com.example.mapchat.adapters

import android.content.Context
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mapchat.R
import com.example.mapchat.databinding.ContactDisplay
import com.example.mapchat.event.ContactsEvent
import com.example.mapchat.model.Contacts
import com.google.android.material.snackbar.Snackbar

class ContactsAdapter(
    private val context: Context,
    private val contactsList: List<Contacts>,
    private val smsManager: SmsManager,
    private val link: String
) :
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
    ): bindMessages {
        val layoutInflater = LayoutInflater.from(parent.context)
        val contactDisplay: ContactDisplay =
            DataBindingUtil.inflate(layoutInflater, R.layout.contacts_display, parent, false)

        contactDisplay.event = object : ContactsEvent {
            override fun sendSms() {
                smsManager.sendTextMessage(
                    contactDisplay.contacts!!.number,
                    null,
                    link,
                    null,
                    null
                )
                Snackbar.make(contactDisplay.root, "Invite send Successfully", Snackbar.LENGTH_LONG)
                    .show()
            }

        }

        return bindMessages(contactDisplay)
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    override fun onBindViewHolder(holder: bindMessages, position: Int) {
        val myContactList = contactsList[position]
        holder.bind(myContactList)
    }

}