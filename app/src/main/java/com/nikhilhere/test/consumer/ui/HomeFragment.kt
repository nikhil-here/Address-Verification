package com.nikhilhere.test.consumer.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nikhilhere.test.consumer.R
import com.nikhilhere.test.consumer.databinding.FragmentConfirmAddressBinding
import com.nikhilhere.test.consumer.databinding.FragmentHomeBinding
import com.nikhilhere.test.consumer.util.Permission
import com.nikhilhere.test.consumer.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home)  {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var TAG = "HomeFragment"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sharedViewModel = sharedViewModel
        binding.lifecycleOwner =  this

        binding.btnChange.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_setAddressFragment)
        }

        sharedViewModel.userLocation.observe(viewLifecycleOwner, { userLocation ->
            Log.i(TAG, "onViewCreated: Datastore Updated ${userLocation}")
            if (userLocation == null){
                Toast.makeText(requireContext(),"Set Your Delivery Address to Continue",Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_homeFragment_to_setAddressFragment)
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}