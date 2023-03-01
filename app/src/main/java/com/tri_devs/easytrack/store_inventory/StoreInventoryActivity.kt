package com.tri_devs.easytrack.store_inventory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tri_devs.easytrack.R
import com.tri_devs.easytrack.adapterviews.StoreInventoryAdapter
import com.tri_devs.easytrack.databinding.ActivityStoreInventoryBinding
import com.tri_devs.easytrack.entities.Product

class StoreInventoryActivity : AppCompatActivity() {

    lateinit var binding: ActivityStoreInventoryBinding

    lateinit var storeProductList : MutableList<Product>

    val db = Firebase.firestore
    val TAG = "All Products"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_store_inventory)
        binding.rvStoreInventory.layoutManager = LinearLayoutManager(this)

        storeProductList = mutableListOf<Product>()

        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val product = Product(document.get("name").toString(),
                        document.get("description").toString(),
                        document.get("quantity").toString().toInt(),
                        document.get("upcNumber").toString().toLong(),
                        document.get("price").toString(),
                        document.get("salesPrice").toString(),
                        document.get("startSalesDate").toString(),
                        document.get("endSalesDate").toString())
                    storeProductList.add(product)
                    Log.d(TAG,"inside for loop" + storeProductList.size.toString())
                }
                val adapter = StoreInventoryAdapter(storeProductList)
                binding.rvStoreInventory.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

    }
}