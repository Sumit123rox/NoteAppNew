package com.sumit.noteappnew.ui

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sumit.noteappnew.utils.SessionManager
import com.sumit.noteappnew.viewModel.AccountViewModel
import com.sumit.noteappnew.viewModel.NoteViewModel
import javax.inject.Inject

open class BaseFragment(@LayoutRes id: Int) : Fragment(id) {

    @Inject
    lateinit var sessionManager: SessionManager

    val accountViewModel by activityViewModels<AccountViewModel>()
    val noteViewModel by activityViewModels<NoteViewModel>()

    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}