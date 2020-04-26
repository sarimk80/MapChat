package com.example.mapchat.ui


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.mapchat.event.FragmentMapEvent

import com.example.mapchat.R
import com.example.mapchat.databinding.FragmentMapBinding
import com.example.mapchat.model.Users
import com.example.mapchat.view_model.MapViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.Property
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment() {

    private val mAuth: FirebaseAuth by inject()
    private lateinit var fragmentMapBinding: FragmentMapBinding
    private lateinit var locationManager: LocationManager
    private val mapViewModel: MapViewModel by viewModel()
    private lateinit var location: Location
    private lateinit var symbol: Symbol
    //private val customMarker: String = "CustomMarker"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Mapbox.getInstance(context!!, getString(R.string.mapbox_access_token))

        fragmentMapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, null, false)




        fragmentMapBinding.mapBox.onCreate(savedInstanceState)


        return fragmentMapBinding.root
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseMessaging.getInstance().subscribeToTopic(mAuth.uid!!).addOnCompleteListener {
            if (it.isComplete) {
                //Log.d("MapFragment", "onViewCreated: ${it.isSuccessful}")
            }
        }

        activity?.intent?.extras?.let {
            for (keys in it.keySet()) {

                val value = activity?.intent?.extras?.get(keys)
                // Log.d("MapFragment", "Key $keys value: $value")
//                fragmentMapBinding.root.findNavController().navigate(
//                    R.id.action_mapFragment_to_chatFragment,
//                    bundleOf("FriendId" to value.toString())
//                )

            }
        }

        mapViewModel.messageRead(mAuth.uid!!).observe(viewLifecycleOwner, Observer { isRead ->
            when (isRead) {
                null -> {
                    fragmentMapBinding.badge.visibility = View.INVISIBLE
                    Log.d("MapFragment", "$isRead")
                }
                true -> {
                    fragmentMapBinding.badge.visibility = View.INVISIBLE
                    Log.d("MapFragment", "$isRead")
                }
                false -> {
                    fragmentMapBinding.badge.visibility = View.VISIBLE
                    Log.d("MapFragment", "$isRead")
                }
            }
        })

        Glide.with(this).load(mAuth.currentUser?.photoUrl)
            .error(R.drawable.ic_person_black_24dp)
            .placeholder(R.drawable.ic_person_black_24dp)
            .apply(RequestOptions.circleCropTransform()).into(fragmentMapBinding.imgUser)


        // Error Observer
        mapViewModel.error().observe(viewLifecycleOwner, Observer { error ->
            Snackbar.make(
                fragmentMapBinding.root,
                "No internet Connection $error",
                Snackbar.LENGTH_LONG
            ).show()
        })


        //Toolbar UserImage Click Event
        fragmentMapBinding.event = object : FragmentMapEvent {
            override fun userDetail() {
                findNavController().navigate(R.id.action_mapFragment_to_accountFragment)
            }

            override fun settingFab() {
                findNavController().navigate(R.id.action_mapFragment_to_settingFragment)
            }

            override fun msgFab() {
                fragmentMapBinding.root.findNavController()
                    .navigate(R.id.action_mapFragment_to_userFragment)
            }


        }


        //Progressbar Observer
        mapViewModel.loading().observe(viewLifecycleOwner, Observer { isLoading ->

            if (isLoading) {
                fragmentMapBinding.progressCircular.visibility = View.VISIBLE
            } else {
                fragmentMapBinding.progressCircular.visibility = View.INVISIBLE
            }

        })

        val rxPermissions = RxPermissions(this)
        locationManager =
            (activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager?)!!

        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe {
            if (it) {


                if (ContextCompat.checkSelfPermission(
                        context!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    location =
                        locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)!!

                    mapViewModel.getUploadedResult(mAuth, location.latitude, location.longitude)
                        .observe(viewLifecycleOwner, Observer {
//                            if (isUploaded) {
//                                Log.d("FragmentMap", mAuth.currentUser!!.phoneNumber!!)
//                            } else {
//                                Log.d("FragmentMap", isUploaded.toString())
//                            }
                        }
                        )


                    fragmentMapBinding.mapBox.getMapAsync { mapboxMap ->


                        mapboxMap.setStyle(Style.Builder().fromUri(getString(R.string.mapboc_access_style)))
                        { style ->

                            viewLifecycleOwner.lifecycleScope.launch {
                                listOfUsers().collect { users ->
                                    Log.d("MapFragment", users.name!!)
                                    Glide.with(fragmentMapBinding.root).asBitmap()
                                        .load(users.imageUrl)
                                        .apply(RequestOptions.circleCropTransform())
                                        .error(R.drawable.ic_person_black_24dp)
                                        .placeholder(R.drawable.ic_person_black_24dp)
                                        .into(object : CustomTarget<Bitmap>() {
                                            override fun onLoadCleared(placeholder: Drawable?) {
                                                style.addImage(users.uuid, placeholder!!)
                                            }

                                            override fun onResourceReady(
                                                resource: Bitmap,
                                                transition: Transition<in Bitmap>?
                                            ) {
                                                style.addImage(users.uuid, resource)
                                                //Log.d("mapFragment", customMarker)
                                            }

                                        })


                                    val symbolManager =
                                        SymbolManager(fragmentMapBinding.mapBox, mapboxMap, style)
                                    symbolManager.iconAllowOverlap = true
                                    symbolManager.iconTranslate = arrayOf(-1f, -1f)
                                    symbolManager.textAllowOverlap = false

                                    val symbolOptions: SymbolOptions = SymbolOptions()
                                        .withLatLng(
                                            LatLng(
                                                users.latitude!!.toDouble(),
                                                users.longitude!!.toDouble()
                                            )
                                        )
                                        .withIconImage(users.uuid)
                                        .withIconSize(0.6f)
                                        .withTextField(users.uuid)
                                        .withTextAnchor(Property.TEXT_ANCHOR_BOTTOM)
                                        .withTextSize(0f)
                                        .withTextOffset(arrayOf(0f, 0f))
                                        .withTextColor("#2f3542")
                                        .withTextJustify(Property.TEXT_JUSTIFY_CENTER)

                                    symbol = symbolManager.create(symbolOptions)

                                    symbolManager.addClickListener { Symbols ->

                                        if (findNavController().currentDestination?.id == R.id.mapFragment) {
                                            fragmentMapBinding.root.findNavController().navigate(
                                                R.id.action_mapFragment_to_chatFragment,
                                                bundleOf("FriendId" to Symbols.textField)
                                            )
                                        }

                                    }

                                }
                            }





                            animateMap(mapboxMap)


                            fragmentMapBinding.fab.setOnClickListener {
                                animateMap(mapboxMap)
                            }
                        }
                    }


//                    //UserList Observer
//                    mapViewModel.getUserList().observe(viewLifecycleOwner, Observer { user ->
//                        Log.d("MapFragment", "${user.size}")
//
//
//                    })
                }

            } else {

                findNavController().navigate(R.id.action_mapFragment_to_errorFragment)
            }
        }


    }

    override fun onStart() {
        super.onStart()
        fragmentMapBinding.mapBox.onStart()
    }

    override fun onPause() {
        super.onPause()
        fragmentMapBinding.mapBox.onPause()
    }

    override fun onResume() {
        super.onResume()
        fragmentMapBinding.mapBox.onResume()
    }

    override fun onStop() {
        super.onStop()
        fragmentMapBinding.mapBox.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentMapBinding.mapBox.onDestroy()


    }

    override fun onDetach() {
        super.onDetach()
        fragmentMapBinding.unbind()

    }

    override fun onLowMemory() {
        super.onLowMemory()
        fragmentMapBinding.mapBox.onLowMemory()
    }


    private fun animateMap(mapboxMap: MapboxMap) {
        val position = CameraPosition.Builder()
            .target(LatLng(location.latitude, location.longitude))
            .zoom(10.0)
            .tilt(25.0)
            .bearing(15.0)
            .build()

        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000)
    }

    private fun listOfUsers(): Flow<Users> = callbackFlow {

        mapViewModel.getUserList().observe(viewLifecycleOwner, Observer { user ->
            user.forEach { singleUser -> offer(singleUser) }
        })
        awaitClose { close() }
    }

}
