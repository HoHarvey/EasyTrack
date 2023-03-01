package com.tri_devs.easytrack

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.tri_devs.easytrack.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    var username: String = ""
    var password: String = ""
    lateinit var binding : FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // setup binding
        binding =FragmentLoginBinding.inflate(this.layoutInflater, container, false)

        binding.btnLogin.setOnClickListener {
            login()
        }
        return binding.root
    }

    fun login(){
        username = binding.edtUser.text.toString()
        password = binding.edtPass.text.toString()
        if(username.isEmpty()||password.isEmpty() ){
            Toast.makeText(activity, "Please fill in both text input fields", Toast.LENGTH_SHORT).show()
        }

        else{
            if(username == "Ware100" && password == "ware100"){
                findNavController().navigate(R.id.action_loginFragment_to_warehouseManagerHomeFragment)
            }
            else if(username == "Sales100" && password == "sales100"){
                findNavController().navigate(R.id.action_loginFragment_to_salesAssociateHomeFragment)
            }
            else if(username == "Dept100" && password == "dept100"){
                findNavController().navigate(R.id.action_loginFragment_to_departmentManagerHomeFragment)
            }

            else{
                Toast.makeText(activity, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }

        }
        binding.edtUser.text.clear()
        binding.edtPass.text.clear()
    }

}