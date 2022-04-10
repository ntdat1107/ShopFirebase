package com.example.shopfb.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.shopfb.databinding.DialogProgressBinding

open class BaseFragment : Fragment() {
    private lateinit var mProgressDialog: Dialog

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(requireActivity())
        val progressBinding: DialogProgressBinding =
            DialogProgressBinding.inflate(layoutInflater)
        mProgressDialog.setContentView(progressBinding.root)
        progressBinding.tvProgressText.text = text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}