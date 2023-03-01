package com.tri_devs.easytrack.productInfoUpdate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.tri_devs.easytrack.R
import com.tri_devs.easytrack.databinding.FragmentDepartmentManagerHomeBinding

class DepartmentManagerHomeFragment : Fragment() {
    lateinit var binding: FragmentDepartmentManagerHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDepartmentManagerHomeBinding.inflate(this.layoutInflater, container, false)

        binding.btnSearchProductScan.setOnClickListener { searchProductScan() }
        binding.btnSearchProductName.setOnClickListener { searchProductName()  }
        binding.btnViewAllProducts.setOnClickListener { viewAllProducts() }
        binding.btnLogout.setOnClickListener {
            findNavController().navigate(R.id.action_logout_department)
        }

        return binding.root
    }

    private fun searchProductScan(){
        val scan = "deptScan"
        val action = DepartmentManagerHomeFragmentDirections.
        actionDepartmentManagerHomeFragmentToBarcodeScanningFragment(scan)
        findNavController().navigate(action)
    }
    private fun searchProductName(){
        val input = "name"
        val action = DepartmentManagerHomeFragmentDirections.
        actionDepartmentManagerHomeFragmentToProductInformationDetailsFragment(input,"")
        findNavController().navigate(action)
    }
    private fun viewAllProducts() {
        findNavController().navigate(R.id.action_departmentManagerHomeFragment_to_storeInventoryActivity)
    }

}