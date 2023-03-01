package com.tri_devs.easytrack.productInfoUpdate

import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tri_devs.easytrack.databinding.FragmentProductInformationDetailsBinding

class ProductInformationDetailsFragment : Fragment() {
    lateinit var binding: FragmentProductInformationDetailsBinding
    val args: ProductInformationDetailsFragmentArgs by navArgs()
    val db = Firebase.firestore
    val productsRef = db.collection("products")
    val TAG = "Prod Info Details"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductInformationDetailsBinding.inflate(this.layoutInflater, container, false)

        val input = args.input
        val upcNumber = args.upcNumber

        if(input == "name"){
            binding.etProductName.text.clear()
            binding.etProductName.inputType = InputType.TYPE_CLASS_TEXT
            binding.tvUPC.text = ""
        }else{
            binding.etProductName.inputType = InputType.TYPE_CLASS_TEXT

            //db part
            val query = productsRef.whereEqualTo("upcNumber",upcNumber)
            query.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document != null){
                        Log.d(TAG, "${document.id} => ${document.data}")
                        binding.etProductName.setText(document.get("name").toString())
                        binding.etPrice.setText(document.get("price").toString())
                        binding.etSalesPrice.setText(document.get("salesPrice").toString())
                        binding.etDescription.setText(document.get("description").toString())
                        binding.etQuantity.setText(document.get("quantity").toString())
                        binding.tvUPC.text = document.get("upcNumber").toString()
                        binding.etBeginSalesDate.setText(document.get("startSalesDate").toString())
                        binding.etEndSalesDate.setText(document.get("endSalesDate").toString())
                        when {
                            document.get("sales").toString().toInt() == 1 -> {
                                binding.rgSalesHolidays.check(binding.rb1.id)
                            }
                            document.get("sales").toString().toInt() == 2 -> {
                                binding.rgSalesHolidays.check(binding.rb2.id)
                            }
                            document.get("sales").toString().toInt() == 3 -> {
                                binding.rgSalesHolidays.check(binding.rb3.id)
                            }
                            else ->{
                                binding.rgSalesHolidays.clearCheck()
                            }
                        }

                    }else{
                        Toast.makeText(activity, "Invalid UPC Number", Toast.LENGTH_SHORT).show()
                    }
                }
            }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }

        binding.btnSearchByName.setOnClickListener { searchProductByName() }
        binding.btnSubmit.setOnClickListener {  submitUpdate() }


        return binding.root
    }

    private fun searchProductByName() {
        val prodName = binding.etProductName.text
        binding.etPrice.setText("")
        binding.etSalesPrice.setText("")
        binding.etDescription.setText("")
        binding.etQuantity.setText("")
        binding.tvUPC.text = ""
        binding.etBeginSalesDate.setText("")
        binding.etEndSalesDate.setText("")
        if (prodName.toString().isBlank()){
            Toast.makeText(activity, "Please enter a product name", Toast.LENGTH_SHORT).show()
        }
        else{
            val query = productsRef.whereEqualTo("name",prodName.toString())
            query.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document != null){
                        Log.d(TAG, "${document.id} => ${document.data}")
                        binding.etProductName.setText(document.get("name").toString())
                        binding.etPrice.setText(document.get("price").toString())
                        binding.etSalesPrice.setText(document.get("salesPrice").toString())
                        binding.etDescription.setText(document.get("description").toString())
                        binding.etQuantity.setText(document.get("quantity").toString())
                        binding.tvUPC.text = document.get("upcNumber").toString()
                        binding.etBeginSalesDate.setText(document.get("startSalesDate").toString())
                        binding.etEndSalesDate.setText(document.get("endSalesDate").toString())
                        when {
                            document.get("sales").toString().toInt() == 1 -> {
                                binding.rgSalesHolidays.check(binding.rb1.id)
                            }
                            document.get("sales").toString().toInt() == 2 -> {
                                binding.rgSalesHolidays.check(binding.rb2.id)
                            }
                            document.get("sales").toString().toInt() == 3 -> {
                                binding.rgSalesHolidays.check(binding.rb3.id)
                            }
                            else ->{
                                binding.rgSalesHolidays.clearCheck()
                            }
                        }

                    }
                    else{
                        Toast.makeText(activity, "Invalid Product Name", Toast.LENGTH_SHORT).show()
                    }
                }
            }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

    private fun submitUpdate() {
        val upcNumber = binding.tvUPC.text
        if (upcNumber.isNullOrBlank()){
            Toast.makeText(activity, "Needs UPC Number to update", Toast.LENGTH_SHORT).show()
        }
        else if(binding.etProductName.text.toString().isBlank() ||
            binding.etPrice.text.toString().isBlank() ||
                binding.etSalesPrice.text.toString().isBlank() ||
                binding.etDescription.text.toString().isBlank() ||
                binding.etQuantity.text.toString().isBlank()){
            Toast.makeText(activity, "1 or more required fields are blank", Toast.LENGTH_SHORT).show()
        }
        else{
            confirmationDialog()
        }
    }


    private fun confirmationDialog(){
        val dialogBuilder = AlertDialog.Builder(requireActivity())

        // set message of alert dialog
        dialogBuilder.setMessage("Are you sure you want to update?")
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("Confirm") { dialog, _ ->
                val upcNumber = binding.tvUPC.text
                val salesNum = when{
                    binding.rb1.isChecked -> {
                        1
                    }
                    binding.rb2.isChecked ->{
                        2
                    }
                    binding.rb2.isChecked  ->{
                        3
                    }
                    else -> 0
                }

                productsRef.document(upcNumber as String).update(
                    "name",binding.etProductName.text.toString(),
                    "price",binding.etPrice.text.toString(),
                    "salesPrice",binding.etSalesPrice.text.toString(),
                    "description",binding.etDescription.text.toString(),
                    "quantity",binding.etQuantity.text.toString().toInt(),
                    "upcNumber",binding.tvUPC.text,
                    "startSalesDate",binding.etBeginSalesDate.text.toString(),
                    "endSalesDate",binding.etEndSalesDate.text.toString(),
                    "sales", salesNum
                )
                dialog.dismiss()
                Toast.makeText(activity, "Update Successful", Toast.LENGTH_SHORT).show()
            }
            // negative button text and action
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Confirm Update")
        // show alert dialog
        alert.show()
    }

}