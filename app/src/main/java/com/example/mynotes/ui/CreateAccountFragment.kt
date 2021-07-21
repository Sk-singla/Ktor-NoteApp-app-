package com.example.mynotes.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mynotes.R
import com.example.mynotes.data.remote.model.User
import com.example.mynotes.databinding.FragmentCreateAccountBinding
import com.example.mynotes.utils.Result
import com.example.mynotes.utils.SessionManager
import com.example.mynotes.ui.viewmodels.UserViewModel
import com.example.mynotes.utils.checkEmailValid
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateAccountFragment: Fragment(R.layout.fragment_create_account) {

    private lateinit var binding: FragmentCreateAccountBinding
    private lateinit var userViewModel:UserViewModel

    @Inject
    lateinit var sessionManager:SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateAccountBinding.bind(view)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)


        binding.createAccountBtn.setOnClickListener {
            val name = binding.userNameEdtTxt.text.toString()
            val email = binding.emailEditTxt.text.toString()
            val password = binding.passwordEdtTxt.text.toString()
            val rePassword = binding.passwordReEnterEdtTxt.text.toString()

            if(name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && rePassword.isNotEmpty()){
                if(password==rePassword && checkEmailValid(email)) {
                    userViewModel.signUp(User(name, email, password))
                    subscribeToUserToken()
                } else {
                    Toast.makeText(requireContext(), "Passwords are not Same/ Email is Not Valid", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "All the fields are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun subscribeToUserToken(){
        userViewModel.userToken.observe(viewLifecycleOwner){ result->
            when(result){
                is Result.Success-> {
                    binding.createUserProgressBar.isVisible = false
                    Toast.makeText(requireContext(), "Registered Successfully", Toast.LENGTH_SHORT).show()

                    // SAVE TOKEN IN SHARED PREF
                    result.data?.let {
                        sessionManager.saveAuthToken(it)
                    }

                    findNavController().popBackStack()
                }
                is Result.Error -> {
                    binding.createUserProgressBar.isVisible = false
                    Log.d("USERERROR","Error: ${result.message}, ${result.data}")
                    Toast.makeText(requireContext(), "Error: ${result.message}, ${result.data}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading-> {
                    binding.createUserProgressBar.isVisible = true
                }
            }
        }
    }
}