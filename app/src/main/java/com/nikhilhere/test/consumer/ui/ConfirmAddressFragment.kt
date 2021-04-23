package com.nikhilhere.test.consumer.ui

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.nikhilhere.test.consumer.R
import com.nikhilhere.test.consumer.data.UserLocation
import com.nikhilhere.test.consumer.databinding.FragmentConfirmAddressBinding
import com.nikhilhere.test.consumer.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmAddressFragment : Fragment() {

    private var _binding: FragmentConfirmAddressBinding? = null
    private val binding get() = _binding!!
    private lateinit var userLocation: UserLocation
    private val args: ConfirmAddressFragmentArgs by navArgs()
    private val sharedViewModel : SharedViewModel by activityViewModels()
    private var TAG = "ConfirmAddressFragment"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConfirmAddressBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        userLocation = args.UserLocation
        //Setting Addres

        binding.tvLocality.text = if(userLocation.primaryName.isEmpty()) userLocation.locality else userLocation.primaryName
        binding.tvAddressLine.text = userLocation.addressLines


        binding.btnSaveAndProceed.setOnClickListener{
            if (binding.tieHouseFlatBlockNo.editableText.isEmpty() || binding.tieLandmark.editableText.isEmpty()){
                return@setOnClickListener
            }else{
                userLocation.houseFlatBlockNo = binding.tieHouseFlatBlockNo.editableText.toString()
                userLocation.landMark = binding.tieLandmark.editableText.toString()
                sharedViewModel.saveUserLocation(userLocation)
                findNavController().navigate(R.id.action_confirmAddressFragment_to_homeFragment)
            }
        }
    }


    private val callback = OnMapReadyCallback { googleMap ->
        val location  = LatLng(userLocation.latitude, userLocation.longitude)
        googleMap.addMarker(MarkerOptions().position(location).title("Order will be delivered here"))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,19f))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}