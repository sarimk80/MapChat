package com.example.mapchat.ui


import android.content.Intent
import android.os.Bundle

import android.view.*

import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapchat.R
import com.example.mapchat.adapters.FriendMessageAdapter
import com.example.mapchat.adapters.MessageDecoration
import com.example.mapchat.databinding.FragmentUserBinding
import com.example.mapchat.view_model.UserViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class UserFragment : Fragment() {

    private lateinit var fragmentUserBinding: FragmentUserBinding
    private val mAuth: FirebaseAuth by inject()
    private val userViewModel: UserViewModel by viewModel()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var friendMessageAdapter: FriendMessageAdapter

    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentUserBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, null, false)

        (activity as AppCompatActivity).setSupportActionBar(fragmentUserBinding.toolbarUser)
        fragmentUserBinding.toolbarUser.title = mAuth.currentUser?.displayName

        recyclerView = fragmentUserBinding.root.findViewById(R.id.user_recyclerView) as RecyclerView

        fragmentUserBinding.toolbarUser.setNavigationOnClickListener {
            it.findNavController().navigate(R.id.action_userFragment_to_mapFragment)
        }

        fragmentUserBinding.toolbarUser.title = mAuth.currentUser?.displayName!!

        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return fragmentUserBinding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.signOut -> signOut()
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tool_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.stackFromEnd = false
        linearLayoutManager.reverseLayout = false
        linearLayoutManager.isSmoothScrollbarEnabled = true
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.addItemDecoration(MessageDecoration(0, 0, 10))

        userViewModel.getAllFriends(mAuth.uid!!).observe(viewLifecycleOwner, Observer { friends ->

            if (friends != null) {
                friendMessageAdapter = FriendMessageAdapter(context!!, friends, this)
                recyclerView!!.adapter = friendMessageAdapter

                friendMessageAdapter.notifyDataSetChanged()
            } else {
                Snackbar.make(fragmentUserBinding.root, "No messages", Snackbar.LENGTH_LONG).show()
            }
            if(friends.isEmpty()){
                Snackbar.make(fragmentUserBinding.root, "No Friends try making some", Snackbar.LENGTH_LONG).show()
            }


        })

        userViewModel.updateMessageRead(mAuth.uid!!)
            .observe(viewLifecycleOwner,
                Observer { })

        userViewModel.loading().observe(viewLifecycleOwner, Observer { isLoading ->

            if (isLoading) {
                fragmentUserBinding.userProgress.visibility = View.VISIBLE
            } else {
                fragmentUserBinding.userProgress.visibility = View.INVISIBLE
            }
        })

        userViewModel.error().observe(viewLifecycleOwner, Observer { error ->

            if (error != null) {
                Snackbar.make(fragmentUserBinding.root, error, Snackbar.LENGTH_LONG).show()
            }
        })


        super.onViewCreated(view, savedInstanceState)
    }


    private fun signOut() {
        mAuth.signOut()
        activity?.let {
            startActivity(Intent(it, MainActivity::class.java))
            it.finish()
        }
    }

    override fun onDetach() {
        super.onDetach()
        fragmentUserBinding.unbind()
    }
}
