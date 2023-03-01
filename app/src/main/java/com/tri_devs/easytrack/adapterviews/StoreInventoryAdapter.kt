package com.tri_devs.easytrack.adapterviews

import com.tri_devs.easytrack.R


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tri_devs.easytrack.databinding.StoreProductRowLayoutBinding
import com.tri_devs.easytrack.entities.Product


class ProductViewHolder(private val binding:StoreProductRowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(product: Product) {
        binding.bndProduct = product
        binding.executePendingBindings()
    }
}
class StoreInventoryAdapter (private val storeProductList: List<Product>) : RecyclerView.Adapter<ProductViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)

        // Create a binding variable that is instantiated to single_battle_row_layout.xml file
        val binding = StoreProductRowLayoutBinding.inflate(layoutInflater, parent, false)

        // Return the view holder
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ProductViewHolder, position: Int) {

        val product = storeProductList[position]

        // send the results of the battle to the ViewHolder (line 9)
        viewHolder.bind(product)
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return storeProductList.size
    }
}