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
import com.example.mynotes.databinding.FragmentLoginBinding
import com.example.mynotes.utils.Result
import com.example.mynotes.utils.SessionManager
import com.example.mynotes.ui.viewmodels.UserViewModel
import com.example.mynotes.utils.checkEmailValid
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment:Fragment(R.layout.fragment_login) {

    private lateinit var binding:FragmentLoginBinding
    private lateinit var userViewModel:UserViewModel

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)


        binding.loginBtn.setOnClickListener {
            val email = binding.emailEditTxt.text.toString()
            val password = binding.passwordEdtTxt.text.toString()

            if(checkEmailValid(email) && password.isNotEmpty()){
                userViewModel.loginUser(User(email=email,password = password))
                subscribeToUserToken()
            } else {
                Toast.makeText(requireContext(), "No field should be empty/Email is Not Valid", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun subscribeToUserToken(){
        userViewModel.userToken.observe(viewLifecycleOwner){ result->
            when(result){
                is Result.Success-> {
                    binding.loginProgressBar.isVisible = false
                    Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                    result.data?.let {
                        sessionManager.saveAuthToken(it)
                    }
                    findNavController().popBackStack()
                }
                is Result.Error -> {
                    binding.loginProgressBar.isVisible = false
                    Log.d("USERERROR","Error: ${result.message}, ${result.data}")
                    Toast.makeText(requireContext(), "Error: ${result.message}, ${result.data}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading-> {
                    binding.loginProgressBar.isVisible = true
                }
            }
        }
    }
}