package com.foodilog.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.foodilog.R
import com.foodilog.databinding.FragmentCommonDialogBinding
class CommonDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentCommonDialogBinding
    private var title = "confirm"

    interface OnResultListener {
        fun onYes()
        fun onNo()
    }

    private var onResultListener: OnResultListener? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCommonDialogBinding.inflate(layoutInflater)

        dialog?.let {
            it.setCancelable(false)
        }
        arguments?.let {
            it.getString("title")?.let { notNullTitle ->
                title = notNullTitle
            }

            it.getString("yes")?.let {
                binding.btnYes.text = it
            }

            it.getString("no")?.let {
                binding.btnNo.text = it
            }

            it.getBoolean("alert")?.let { alert ->
                if(alert){
                    makeAlert()
                }
            }
        }

        binding.tvTitle.text = title
        binding.btnYes.setOnClickListener {
            if (onResultListener != null) {
                onResultListener!!.onYes()
            }
            dismiss()
        }
        binding.btnNo.setOnClickListener {
            if (onResultListener != null) {
                onResultListener!!.onNo()
            }
            dismiss()
        }

        dialog?.let { safeDialog ->
            safeDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            safeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }

        return binding.getRoot()
    }

    fun makeAlert(btnYes : String?=null) {
        binding.btnNo.visibility = View.GONE
        binding.llPopupSize.background = resources.getDrawable(R.drawable.btn_corners_20d, null)
        binding.btnYes.background = resources.getDrawable(R.drawable.btn_bottom_radius_bg, null)
        binding.btnYes.text = btnYes ?: resources.getString(R.string.confirm)
    }

    fun setOnResultListener(onResultListener: OnResultListener?) {
        this.onResultListener = onResultListener
    }
}