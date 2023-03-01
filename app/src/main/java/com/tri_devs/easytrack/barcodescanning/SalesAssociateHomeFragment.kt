package com.tri_devs.easytrack.barcodescanning


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.tri_devs.easytrack.R
import com.tri_devs.easytrack.databinding.FragmentSalesAssociateHomeBinding



class SalesAssociateHomeFragment : Fragment() {
    lateinit var binding: FragmentSalesAssociateHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSalesAssociateHomeBinding.inflate(this.layoutInflater, container, false)

        binding.btnScan.setOnClickListener { scan() }

        binding.btnSearch.setOnClickListener { search() }

        binding.btnView.setOnClickListener { view() }

        binding.btnLogout.setOnClickListener {
            findNavController().navigate(R.id.action_logout_sales)
        }

        return binding.root
    }

    fun scan(){
        val scan = "salesScan"
        val action = SalesAssociateHomeFragmentDirections.
        actionSalesAssociateHomeFragmentToBarcodeScanningFragment(scan)
        findNavController().navigate(action)
    }

    fun search(){
        findNavController().navigate(R.id.action_salesAssociateHomeFragment_to_seachProductNameFragment)
    }

    fun view(){
        findNavController().navigate(R.id.action_salesAssociateHomeFragment_to_storeInventoryActivity)
    }

}