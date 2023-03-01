package com.tri_devs.easytrack.barcodescanning

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.tri_devs.easytrack.R
import com.tri_devs.easytrack.databinding.FragmentBarcodeScanningBinding
import com.tri_devs.easytrack.databinding.FragmentProductInfoSearchBinding
import com.tri_devs.easytrack.entities.Product

class ProductInfoSearchFragment : Fragment() {

    private lateinit var product: Product

    private lateinit var binding: FragmentProductInfoSearchBinding

    val args: ProductInfoSearchFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductInfoSearchBinding.inflate(this.layoutInflater, container, false)

        product = args.ProductInfo

        binding.bndProduct = product

        return binding.root
    }


}
