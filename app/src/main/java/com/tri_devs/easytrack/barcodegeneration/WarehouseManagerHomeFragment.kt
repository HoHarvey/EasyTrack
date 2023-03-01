package com.tri_devs.easytrack.barcodegeneration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.tri_devs.easytrack.R
import com.tri_devs.easytrack.databinding.FragmentWarehouseManagerHomeBinding


class WarehouseManagerHomeFragment : Fragment() {
    lateinit var binding: FragmentWarehouseManagerHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWarehouseManagerHomeBinding.inflate(this.layoutInflater, container, false)

        binding.btnInsertNewProductInfo.setOnClickListener {
            insertNewProduct()
        }
        binding.btnViewAllProducts.setOnClickListener {
            retrieveAllProducts()
        }

        binding.btnLogout.setOnClickListener {
            findNavController().navigate(R.id.action_logout_warehouse)
        }

        return binding.root
    }

    private fun insertNewProduct(){
        findNavController().navigate(R.id.action_warehouseManagerHomeFragment_to_insertNewProductEntryFragment)
    }

    private fun retrieveAllProducts(){
        findNavController().navigate(R.id.action_warehouseManagerHomeFragment_to_storeInventoryActivity)
    }
}