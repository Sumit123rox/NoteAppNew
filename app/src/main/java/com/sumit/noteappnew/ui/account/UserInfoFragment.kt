package com.sumit.noteappnew.ui.account

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sumit.noteappnew.R
import com.sumit.noteappnew.databinding.FragmentUserInfoBinding
import com.sumit.noteappnew.ui.BaseFragment
import com.sumit.noteappnew.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserInfoFragment : BaseFragment(R.layout.fragment_user_info) {

    private var _binding: FragmentUserInfoBinding? = null
    private val binding: FragmentUserInfoBinding?
        get() = _binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUserInfoBinding.bind(view)

        initListeners()
        subscribeToCurrentUserState()
    }

    private fun initListeners() {
        with(binding!!) {
            createAccountBtn.setOnClickListener {
                findNavController().navigate(R.id.action_userInfoFragment_to_createAccountFragment)
            }

            loginBtn.setOnClickListener {
                findNavController().navigate(R.id.action_userInfoFragment_to_loginFragment)
            }

            logoutBtn.setOnClickListener { accountViewModel.logout() }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeToCurrentUserState() = lifecycleScope.launch {
        accountViewModel.currentUserState.collectLatest {
            when (it) {
                Resource.Loading -> {
                    showOrHideProgressbar(true)
                }

                is Resource.Success -> {
                    userLoggedIn()
                    binding?.userTxt?.text = it.result.user?.name ?: "Not logged In!"
                    binding?.userEmail?.text = it.result.user?.email ?: "No Email"
                }

                is Resource.Failure -> {
                    userNotLoggedIn()
                    binding?.userTxt?.text = "Not logged In"
                }
            }
        }
    }

    private fun showOrHideProgressbar(show: Boolean) {
        binding?.userProgressBar?.isVisible = show
    }

    override fun onStart() {
        super.onStart()
        accountViewModel.getCurrentUser()
    }

    private fun userLoggedIn() {
        binding?.userProgressBar?.isVisible = false
        binding?.createAccountBtn?.isVisible = false
        binding?.loginBtn?.isVisible = false
        binding?.logoutBtn?.isVisible = true
        binding?.userEmail?.isVisible = true
    }

    private fun userNotLoggedIn() {
        binding?.userProgressBar?.isVisible = false
        binding?.createAccountBtn?.isVisible = true
        binding?.loginBtn?.isVisible = true
        binding?.logoutBtn?.isVisible = false
        binding?.userEmail?.isVisible = false
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserInfoFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}