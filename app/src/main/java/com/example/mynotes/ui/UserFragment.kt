package com.example.mynotes.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mynotes.R
import com.example.mynotes.data.repository.NoteRepository
import com.example.mynotes.databinding.FragmentUserBinding
import com.example.mynotes.utils.Result
import com.example.mynotes.ui.viewmodels.UserViewModel
import com.example.mynotes.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class UserFragment:Fragment(R.layout.fragment_user) {

    private lateinit var binding: FragmentUserBinding
    private lateinit var userViewModel:UserViewModel

    @Inject
    lateinit var repo:NoteRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserBinding.bind(view)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        userViewModel.getCurrentUser()

//        subscribeCurUser()
        subscribeCurUserEvent()


//        val sessionManager = SessionManager(requireContext())
//        Toast.makeText(requireContext(), "${sessionManager.fetchAuthToken()}", Toast.LENGTH_SHORT).show()
//        Log.d("token","${sessionManager.fetchAuthToken()}")


        binding.createAccountBtn.setOnClickListener {
            findNavController().navigate(R.id.action_userFragment_to_createAccountFragment)
        }
        binding.loginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_userFragment_to_loginFragment)
        }
        binding.logoutBtn.setOnClickListener {
            userViewModel.logout()
        }

        binding.userSwipeRefresh.setOnRefreshListener {
            userViewModel.getCurrentUser{
                binding.userSwipeRefresh.isRefreshing = false
            }
        }

    }

    private fun subscribeCurUserEvent() = lifecycleScope.launchWhenCreated {
        userViewModel.userEvent.collect { result->
            when (result) {
                is Result.Success -> {
                    binding.userTxt.text = "${result.data?.name}"
                    binding.userEmail.text = "${result.data?.email}"
                    showLogoutBtn()
                }
                is Result.Error -> {
                    binding.userTxt.text = "User not Found"
                    Toast.makeText(requireContext(), "${result.message}", Toast.LENGTH_SHORT).show()
                    showCreateLoginBtn()
                }
                is Result.Loading -> {
                    showProgressBar()
                }

            }

        }
    }

    private fun subscribeCurUser()  {
        userViewModel.currentUser.observe(viewLifecycleOwner) { result->

            when (result) {
                is Result.Success -> {
                    binding.userTxt.text = "${result.data?.name}"
                    binding.userEmail.text = "${result.data?.email}"
                    showLogoutBtn()
                }
                is Result.Error -> {
                    binding.userTxt.text = "User not Found"
                    Toast.makeText(requireContext(), "${result.message}", Toast.LENGTH_SHORT).show()
                    showCreateLoginBtn()
                }
                is Result.Loading -> {
                    showProgressBar()
                }

            }
        }
    }

    private fun showProgressBar(){
        binding.logoutBtn.isVisible = false
        binding.createAccountBtn.isVisible = false
        binding.loginBtn.isVisible = false
        binding.userTxt.isVisible = false
        binding.userEmail.isVisible = false


        binding.userProgressBar.isVisible = true
    }

    private fun showCreateLoginBtn(){
        binding.logoutBtn.isVisible = false
        binding.userProgressBar.isVisible = false
        binding.userEmail.isVisible = false


        binding.createAccountBtn.isVisible = true
        binding.loginBtn.isVisible = true
        binding.userTxt.isVisible = true
    }

    private fun showLogoutBtn(){
        binding.createAccountBtn.isVisible = false
        binding.loginBtn.isVisible = false
        binding.userProgressBar.isVisible = false

        binding.logoutBtn.isVisible = true
        binding.userEmail.isVisible = true
        binding.userTxt.isVisible = true

    }

    private fun isNetworkConnected(): Boolean {
        //1
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //2
        val activeNetwork = connectivityManager.activeNetwork
        //3
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        //4
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}