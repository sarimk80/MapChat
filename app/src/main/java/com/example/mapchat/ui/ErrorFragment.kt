package com.example.mapchat.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.mapchat.R
import com.example.mapchat.databinding.FragmentErrorBinding
import com.example.mapchat.event.ErrorEvent
import com.tbruyelle.rxpermissions2.RxPermissions


class ErrorFragment : Fragment() {

    private lateinit var fragmentErrorBinding: FragmentErrorBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentErrorBinding =
            DataBindingUtil.inflate(inflater,
                R.layout.fragment_error, container, false)

        return fragmentErrorBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rxPermissions = RxPermissions(this)
        super.onViewCreated(view, savedInstanceState)
        fragmentErrorBinding.event = object : ErrorEvent {
            override fun close() {
                findNavController().navigate(R.id.action_errorFragment_to_mapFragment)
            }

            @SuppressLint("CheckResult")
            override fun permission() {
                rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe {
                    if (it) {
                        findNavController().navigate(R.id.action_errorFragment_to_mapFragment)
                    }
                }

            }

        }
    }

}