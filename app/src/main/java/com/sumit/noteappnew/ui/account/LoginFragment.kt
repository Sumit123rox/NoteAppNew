package com.sumit.noteappnew.ui.account

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sumit.noteappnew.R
import com.sumit.noteappnew.databinding.FragmentLoginBinding
import com.sumit.noteappnew.ui.BaseFragment
import com.sumit.noteappnew.utils.Constants.EMAIL
import com.sumit.noteappnew.utils.Constants.JWT_TOKEN_KEY
import com.sumit.noteappnew.utils.Constants.USERNAME
import com.sumit.noteappnew.utils.Resource
import com.sumit.noteappnew.utils.loge
import com.sumit.noteappnew.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding?
        get() = _binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
        initListeners()
        subscribeToLoginState()
    }

    private fun initListeners() {
        with(binding!!) {
            loginBtn.setOnClickListener {
                val name = nameEditTxt.text.toString().trim()
                val email = emailEditTxt.text.toString().trim()
                val password = passwordEdtTxt.text.toString().trim()

                loge { "Login button clicked" }

                accountViewModel.loginUser(name, email, password)
            }
        }
    }

    private fun subscribeToLoginState() = lifecycleScope.launch {
        accountViewModel.loginState.collectLatest {
            when (it) {
                Resource.Loading -> {
                    showOrHideProgressbar(true)
                }

                is Resource.Success -> {
                    val result = it.result

                    if (result.success) {
                        loge { "Resource Success" }
                        showOrHideProgressbar(false)
                        sessionManager.saveString(JWT_TOKEN_KEY, result.message)
                        sessionManager.saveString(EMAIL, result.user?.email ?: "")
                        sessionManager.saveString(USERNAME, result.user?.name ?: "")
                        mContext.toast { "Login successfully" }
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
        binding?.loginProgressBar?.isVisible = show
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}