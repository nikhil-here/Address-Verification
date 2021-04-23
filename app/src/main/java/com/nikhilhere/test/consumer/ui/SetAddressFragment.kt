package com.nikhilhere.test.consumer.ui


import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.nikhilhere.test.consumer.R
import com.nikhilhere.test.consumer.data.UserLocation
import com.nikhilhere.test.consumer.databinding.FragmentSetAddressBinding
import com.nikhilhere.test.consumer.util.Permission
import com.nikhilhere.test.consumer.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class SetAddressFragment : Fragment(R.layout.fragment_set_address) ,EasyPermissions.PermissionCallbacks {

    private var _binding : FragmentSetAddressBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val adapterRecentSearch by lazy { AdapterRecentSearch() }


    private var TAG = "SetAddressFragment"

    @Inject
    lateinit var placesClient : PlacesClient
    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient
    @Inject
    lateinit var geoCoder: Geocoder


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSetAddressBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvRecentSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentSearch.adapter = adapterRecentSearch

        binding.clCurrentLocation.setOnClickListener{
            if (Permission.hasLocationPermission(requireContext())) {
                getCurrentLocation()
            } else {
                Permission.requestLocationPermission(this)
            }
        }

        binding.etPlacesSearch.doOnTextChanged { text, _, _, _ ->
            getplaces(text)
        }

        subscribeObserveres()



    }

    private fun subscribeObserveres() {
        lifecycleScope.launch {
            adapterRecentSearch.placeId.collectLatest { placeId ->
                if (placeId.isNotEmpty()){
                    Log.i(TAG, "subscribeObserveres: PlaceId Selected "+placeId)
                    onPlaceSelected(placeId)
                }
            }
        }
    }

    private fun onPlaceSelected(placeId: String) {
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.NAME
        )

        val request = FetchPlaceRequest.builder(placeId, placeFields).build()
        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            Log.i(TAG, "${response.toString()}")
            val userLocation = getUserLocationByPlace(response.place)
            val action = SetAddressFragmentDirections.actionSetAddressFragmentToConfirmAddressFragment(userLocation)
            findNavController().navigate(action)
        }.addOnFailureListener { exception ->
            Log.e(TAG, exception.message.toString())
        }
    }


    private fun getplaces(text: CharSequence?) {
        if (sharedViewModel.checkDeviceLocationSettings(requireContext())) {
            lifecycleScope.launch {
                if (text.isNullOrEmpty()) {
                    adapterRecentSearch.setData(emptyList())
                } else {
                    val token = AutocompleteSessionToken.newInstance()

                    val request = FindAutocompletePredictionsRequest.builder()
                        .setCountries("IN")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(text.toString())
                        .build()

                    placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener { response ->
                            adapterRecentSearch.setData(response.autocompletePredictions)
                            binding.rvRecentSearch.scheduleLayoutAnimation()
                        }
                        .addOnFailureListener { exception ->
                            if (exception is ApiException) {
                            }
                        }
                }
            }
        } else {
            Toast.makeText(requireContext(), "Please Enable Location Settings", Toast.LENGTH_SHORT)
                .show()
        }
    }


    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            try {
                var addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                addresses.get(0).apply {
                    val currentLocation  = UserLocation(
                        addressLines = getAddressLine(0),
                        state = adminArea,
                        district = subAdminArea,
                        locality = locality,
                        postalCode = postalCode,
                        countryCode = countryCode,
                        latitude = latitude,
                        longitude = longitude
                    )
                    Log.i(TAG, "getCurrentLocation: ${currentLocation}")
                    val action = SetAddressFragmentDirections.actionSetAddressFragmentToConfirmAddressFragment(currentLocation)
                    findNavController().navigate(action)
                }

            } catch (e: Exception) {
                Log.i(TAG, "getLocation exception ${e.message}")
            }
        }
    }

    fun getUserLocationByPlace(place : Place): UserLocation {
        val addresses = geoCoder.getFromLocation(place.latLng!!.latitude, place.latLng!!.longitude, 1)
        addresses.get(0).apply {
            val userLocation = UserLocation(
                primaryName = place.name!!,
                addressLines = getAddressLine(0),
                state = adminArea,
                district = subAdminArea,
                locality = locality,
                postalCode = postalCode,
                countryCode = countryCode,
                latitude = latitude,
                longitude = longitude
            )
            return userLocation
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)

    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        getCurrentLocation()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionDenied(this,perms[0])){
            AppSettingsDialog.Builder(requireActivity()).build().show()
        }else{
            Permission.requestLocationPermission(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}