package com.tri_devs.easytrack.barcodescanning

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tri_devs.easytrack.databinding.FragmentSearchProductNameBinding
import com.tri_devs.easytrack.entities.Product

class SearchProductNameFragment : Fragment() {
    lateinit var binding: FragmentSearchProductNameBinding
    val name = ""
    val db = Firebase.firestore
    val productsRef = db.collection("products")
    val TAG = "ABC"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchProductNameBinding.inflate(this.layoutInflater, container, false)

        binding.txtRetailPrice.setText("")
        binding.txtSalePrice.setText("")
        binding.txtDescription.setText("")
        binding.txtQuantity.setText("")
        binding.txtupcNum.setText("")
        binding.txtStartSales.setText("")
        binding.txtEndSales.setText("")


        binding.btnSearchName.setOnClickListener {
            searchProductByName()
        }

        return binding.root
    }

    private fun searchProductByName() {
        val prodName = binding.edtProductName.text
        if (prodName.toString().isBlank()) {
            Toast.makeText(activity, "Please enter a product name", Toast.LENGTH_SHORT).show()
        } else {
            val query = productsRef.whereEqualTo("name", prodName.toString())
            query.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, document.toString())

                    if (document != null) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        val productInfo = Product(
                            document.get("name").toString(),
                            document.get("description").toString(),
                            document.get("quantity").toString().toInt(),
                            document.get("upcNumber").toString().toLong(),
                            document.get("price").toString(),
                            document.get("salesPrice").toString(),
                            document.get("startSalesDate").toString(),
                            document.get("endSalesDate").toString()
                        )
                        binding.bndProduct = productInfo

                    } else {
                        val productInfo = Product(
                          "",
                            "",
                            0,
                            0,
                           "",
                            "",
                           "",
                            ""
                        )
                        binding.bndProduct = productInfo

                        Toast.makeText(activity, "Invalid Product Name", Toast.LENGTH_SHORT).show()
//                        binding.edtProductName.text.clear()
//                        binding.txtRetailPrice.setText("")
//                        binding.txtSalePrice.setText("")
//                        binding.txtDescription.setText("")
//                        binding.txtQuantity.setText("")
//                        binding.txtupcNum.setText("")
//                        binding.txtStartSales.setText("")
//                        binding.txtEndSales.setText("")
                        Log.d(TAG, name)
                    }
                }
            }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)


                }
        }
    }


}