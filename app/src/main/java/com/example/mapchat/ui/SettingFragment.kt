package com.example.mapchat.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapchat.R
import com.example.mapchat.adapters.ContactsAdapter
import com.example.mapchat.databinding.FragmentSettingBinding
import com.example.mapchat.model.Contacts
import com.example.mapchat.view_model.SettingViewModel
import com.google.firebase.auth.FirebaseAuth
import com.tbruyelle.rxpermissions2.RxPermissions
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingFragment : Fragment() {


    private lateinit var settingBinding: FragmentSettingBinding
    private val settingViewModel: SettingViewModel by viewModel()
    private val mAuth: FirebaseAuth by inject()
    private val contractList = ArrayList<Contacts>()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var contactsAdapter: ContactsAdapter
    private var recyclerView: RecyclerView? = null
    private val smsManager: SmsManager = SmsManager.getDefault()

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_setting, container, false
            )

        settingBinding.toolbarSetting.setNavigationOnClickListener {
            it.findNavController().navigate(R.id.action_settingFragment_to_mapFragment)
        }
        recyclerView = settingBinding.root.findViewById(R.id.recycler_phone) as RecyclerView
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.stackFromEnd = false
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.isSmoothScrollbarEnabled = true
        recyclerView!!.layoutManager = linearLayoutManager


        // Inflate the layout for this fragment
        return settingBinding.root
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        settingViewModel.getCoroutineSingleUser(mAuth.uid!!)
            .observe(viewLifecycleOwner, Observer { user ->
                if (user != null) {
                    settingBinding.user = user
                }
            })

        val rxPermissions = RxPermissions(this)
        rxPermissions.request(
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.SEND_SMS
        ).subscribe {
            if (it) {

                val cursor = activity?.contentResolver!!.query(
                    ContactsContract.Data.CONTENT_URI,
                    arrayOf(
                        ContactsContract.Data._ID,
                        ContactsContract.Data.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.TYPE
                    ),
                    ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                            + "' AND " + ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?"
                    , arrayOf("com.whatsapp"), null
                )
                while (cursor!!.moveToNext()) {

                    contractList.add(
                        Contacts(
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)),
                            cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)),
                            "INVITE"
                        )
                    )

                }

                settingViewModel.getLink().observe(viewLifecycleOwner, Observer { link ->

                    contactsAdapter = if (link != null) {
                        Log.d("SettingFragment", link)
                        ContactsAdapter(context!!, contractList, smsManager, link)


                    } else {
                        ContactsAdapter(context!!, contractList, smsManager, "")
                    }
                    cursor.close()

                    recyclerView!!.adapter = contactsAdapter
                    contactsAdapter.notifyDataSetChanged()

                })

            } else {
                contractList.add(Contacts("Need permission to add your friends and family", "", ""))
                contactsAdapter = ContactsAdapter(context!!, contractList, smsManager, "")
            }

        }


        settingViewModel.isLoading().observe(viewLifecycleOwner, Observer {
            if (it) {
                settingBinding.progressCircular.visibility = View.VISIBLE
            } else {
                settingBinding.progressCircular.visibility = View.INVISIBLE
            }
        })

    }


}