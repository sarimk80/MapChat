package com.example.mapchat.ui


import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mapchat.R
import com.example.mapchat.adapters.MessageAdapter
import com.example.mapchat.adapters.MessageDecoration
import com.example.mapchat.databinding.FragmentChatBinding
import com.example.mapchat.model.Messages
import com.example.mapchat.view_model.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import convertToAscii
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() {

    private lateinit var fragmentChatBinding: FragmentChatBinding
    private lateinit var charViewModel: ChatViewModel
    private val mAuth: FirebaseAuth by inject()
    private lateinit var geocoder: Geocoder
    private var asciiCode: String = ""
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var messageAdapter: MessageAdapter

    private var recyclerView: RecyclerView? = null
    private val messages: Messages = Messages("sasd", "dasd", "hello", "2020-march-29")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentChatBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_chat, null, false
        )
        (activity as AppCompatActivity).setSupportActionBar(fragmentChatBinding.toolbarChat)

        recyclerView = fragmentChatBinding.root.findViewById(R.id.recycler_view) as RecyclerView

        fragmentChatBinding.toolbarChat.setNavigationOnClickListener {
            it.findNavController().navigate(R.id.action_chatFragment_to_mapFragment)
        }

        charViewModel =
            ViewModelProvider(this, defaultViewModelProviderFactory).get(ChatViewModel::class.java)

        geocoder = Geocoder(context, Locale.getDefault())

        return fragmentChatBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        asciiCode = convertToAscii(mAuth.uid, arguments?.getString("FriendId"))

        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.stackFromEnd = false
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.isSmoothScrollbarEnabled = false
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.addItemDecoration(MessageDecoration())

        charViewModel.getSingleUser(arguments?.getString("FriendId")!!)
            .observe(this, Observer { user ->

                fragmentChatBinding.user = user
                Glide.with(this).load(user.imageUrl).placeholder(R.drawable.ic_person_black_24dp)
                    .error(R.drawable.ic_person_black_24dp).into(fragmentChatBinding.imgFriend)

//                val address = geocoder.getFromLocation(user.latitude!!, user.longitude!!, 1)

                runBlocking {
                    fragmentChatBinding.city = getCity(user.latitude, user.longitude)
                }
            })


        charViewModel.getAllMessages(asciiCode)
            .observe(this,
                Observer { data ->

                    if (data.isEmpty()) {
                        Log.d("ChatFragment", data.size.toString())
                    } else {
                        messageAdapter = MessageAdapter(context!!, data)

                        recyclerView!!.adapter = messageAdapter
                        messageAdapter.notifyDataSetChanged()

                        Log.d("ChatFragment", "data change")


                    }
                })


        fragmentChatBinding.sendMessage.setOnClickListener {
            charViewModel.updateMessages(asciiCode, messages).observe(this, Observer {})
        }





        charViewModel.loading().observe(this, Observer { isLoading ->
            if (isLoading) {
                fragmentChatBinding.progressCircular.visibility = View.VISIBLE
            } else {
                fragmentChatBinding.progressCircular.visibility = View.INVISIBLE
            }
        })


        charViewModel.error().observe(this, Observer { error ->
            Log.d("ChatFragment", error)
        })


    }


    private suspend fun getCity(latitude: Double?, longitude: Double?): String {

        var city = ""

        CoroutineScope(Dispatchers.IO).launch {
            val address = geocoder.getFromLocation(latitude!!, longitude!!, 1)
            city = address[0].locality + ", " + address[0].countryName
        }.join()




        return city
    }


}
