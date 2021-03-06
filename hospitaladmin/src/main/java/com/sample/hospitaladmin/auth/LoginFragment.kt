package com.sample.hospitaladmin.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.sample.common.isMobile
import com.sample.common.isPassword
import com.sample.hospitaladmin.R
import com.sample.hospitaladmin.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentLoginBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentLoginBinding.bind(view)

        binding.loginButton.setOnClickListener {
            val phoneNumber = binding.loginPhoneNumberInput.text.toString().trim()
            val password = binding.loginPasswordInput.text.toString().trim()

            if (!phoneNumber.isMobile() || !password.isPassword()) {
                val bottomNavView = requireActivity().findViewById<View>(R.id.authOptionsBottomNav)
                Snackbar.make(
                    bottomNavView,
                    "PhoneNumber or Password is not valid", Snackbar.LENGTH_LONG,
                ).apply {
                    anchorView = bottomNavView
                    setAction("Dismiss") { }
                }.show()
                return@setOnClickListener
            }
            // Check
        }
    }

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }
}