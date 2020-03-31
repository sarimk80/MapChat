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
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.mapchat.event.FragmentMapEvent

import com.example.mapchat.R
import com.example.mapchat.databinding.FragmentMapBinding
import com.example.mapchat.view_model.MapViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
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
import org.koin.android.ext.android.inject


/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment() {

    private val mAuth: FirebaseAuth by inject()
    private lateinit var fragmentMapBinding: FragmentMapBinding
    private lateinit var locationManager: LocationManager
    private lateinit var mapViewModel: MapViewModel
    private lateinit var location: Location
    private lateinit var symbol: Symbol
    private val customMarker: String = "CustomMarker"


    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Mapbox.getInstance(context!!, getString(R.string.mapbox_access_token))

        fragmentMapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, null, false)


        val rxPermissions = RxPermissions(this)
        mapViewModel =
            ViewModelProvider(this, defaultViewModelProviderFactory).get(MapViewModel::class.java)

        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe {
            if (it) {
                locationManager =
                    (activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager?)!!

                if (ContextCompat.checkSelfPermission(
                        context!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    location =
                        locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)!!

                    mapViewModel.getUploadedResult(mAuth, location.latitude, location.longitude)
                        .observe(this, Observer { isUploaded ->
                            if (isUploaded) {
                                Log.d("FragmentMap", isUploaded.toString())
                            } else {
                                Log.d("FragmentMap", isUploaded.toString())
                            }
                        }
                        )
                }

            } else {
                Snackbar.make(
                    fragmentMapBinding.root,
                    "Permission is Required for this application",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }


        fragmentMapBinding.mapBox?.onCreate(savedInstanceState)


        return fragmentMapBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(this).load(mAuth.currentUser?.photoUrl).error(R.drawable.ic_person_black_24dp)
            .placeholder(R.drawable.ic_person_black_24dp)
            .apply(RequestOptions.circleCropTransform()).into(fragmentMapBinding.imgUser)


        // Error Observer
        mapViewModel.Error().observe(this, Observer { error ->
            Snackbar.make(
                fragmentMapBinding.root,
                "No internet Connection $error",
                Snackbar.LENGTH_LONG
            ).show()
        })


        //Toolbar UserImage Click Event
        fragmentMapBinding.event = object : FragmentMapEvent {
            override fun userDetail() {
                fragmentMapBinding.root.findNavController()
                    .navigate(R.id.action_mapFragment_to_userFragment)
            }

        }


        //Progressbar Observer
        mapViewModel.Loading().observe(this, Observer { isLoading ->

            if (isLoading) {
                fragmentMapBinding.progressCircular.visibility = View.VISIBLE
            } else {
                fragmentMapBinding.progressCircular.visibility = View.INVISIBLE
            }

        })


        //UserList Observer
        mapViewModel.getUserList().observe(this, Observer { user ->


            fragmentMapBinding.mapBox.getMapAsync { mapboxMap ->
                mapboxMap.setStyle(Style.Builder().fromUri(getString(R.string.mapboc_access_style))) { style ->

                    user.forEach { users ->
                        val customTarget = object : CustomTarget<Bitmap>() {
                            override fun onLoadCleared(placeholder: Drawable?) {

                            }

                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                style.addImage(customMarker, resource)
                            }

                        }
                        Glide.with(this).asBitmap().load(users.imageUrl)
                            .error(R.drawable.ic_person_black_24dp)
                            .placeholder(R.drawable.ic_person_black_24dp)
                            .into(customTarget)

                        val symbolManager =
                            SymbolManager(fragmentMapBinding.mapBox, mapboxMap, style)
                        symbolManager.iconAllowOverlap = true
                        symbolManager.iconTranslate = arrayOf(-4f, -5f)
                        symbolManager.textAllowOverlap = false

                        val symbolOptions: SymbolOptions = SymbolOptions()
                            .withLatLng(
                                LatLng(
                                    users.latitude!!.toDouble(),
                                    users.longitude!!.toDouble()
                                )
                            )
                            .withIconImage(customMarker)
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
                            } else {
                                Log.d("MapFragment", "not mapfragment")
                            }


                        }


                    }


                    animateMap(mapboxMap)


                    fragmentMapBinding.fab.setOnClickListener {
                        animateMap(mapboxMap)
                    }
                }
            }


        })


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

    override fun onLowMemory() {
        super.onLowMemory()
        fragmentMapBinding.mapBox.onLowMemory()
    }


    private fun animateMap(mapboxMap: MapboxMap) {
        val position = CameraPosition.Builder()
            .target(LatLng(location.latitude, location.longitude))
            .zoom(10.0)
            .tilt(20.0)
            .bearing(10.0)
            .build()

        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000)
    }


}


@BindingAdapter("bind:imageUrl")
fun loadImage(view: ImageView, imageUrl: String?) {

    Glide.with(view.context).load(imageUrl)
        .error(R.drawable.ic_person_black_24dp)
        .placeholder(R.drawable.ic_person_black_24dp)
        .into(view)


}