package com.sumit.noteappnew.ui.account

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sumit.noteappnew.R
import com.sumit.noteappnew.databinding.FragmentCreateAccountBinding
import com.sumit.noteappnew.ui.BaseFragment
import com.sumit.noteappnew.utils.Constants.EMAIL
import com.sumit.noteappnew.utils.Constants.JWT_TOKEN_KEY
import com.sumit.noteappnew.utils.Constants.USERNAME
import com.sumit.noteappnew.utils.Resource
import com.sumit.noteappnew.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateAccountFragment : BaseFragment(R.layout.fragment_create_account) {

    private var _binding: FragmentCreateAccountBinding? = null
    private val binding: FragmentCreateAccountBinding?
        get() = _binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateAccountBinding.bind(view)
        initListeners()
        subscribeToRegisterState()
    }

    private fun initListeners() {
        with(binding!!) {
            createAccountBtn.setOnClickListener {
                val name = userNameEdtTxt.text.toString().trim()
                val email = emailEditTxt.text.toString().trim()
                val password = passwordEdtTxt.text.toString().trim()
                val confirmPassword = passwordReEnterEdtTxt.text.toString().trim()

                accountViewModel.createUser(name, email, password, confirmPassword)
            }
        }
    }

    private fun subscribeToRegisterState() = lifecycleScope.launch {
        accountViewModel.registerState.collectLatest {
            when (it) {
                Resource.Loading -> {
                    showOrHideProgressbar(true)
                }

                is Resource.Success -> {
                    showOrHideProgressbar(false)
                    val result = it.result

                    if (result.success) {
                        sessionManager.saveString(JWT_TOKEN_KEY, result.message)
                        sessionManager.saveString(USERNAME, result.user?.name ?: "")
                        sessionManager.saveString(EMAIL, result.user?.email ?: "")
                        mContext.toast { "Account created successfully" }
                        findNavController().popBackStack()
                    } else {
                        showOrHideProgressbar(false)
                        mContext.toast { result.message }
                    }
                }

                is Resource.Failure -> {
                    showOrHideProgressbar(false)
                    mContext.toast { it.errorMessage }
                }
            }
        }
    }

    private fun showOrHideProgressbar(show: Boolean) {
        binding?.createUserProgressBar?.isVisible = show
    }

    companion object {
        @JvmStatic
        fun newInstance() = CreateAccountFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}