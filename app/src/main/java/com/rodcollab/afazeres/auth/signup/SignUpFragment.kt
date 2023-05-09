package com.rodcollab.afazeres.auth.signup

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.rodcollab.afazeres.util.validators.onTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rodcollab.afazeres.R
import com.rodcollab.afazeres.util.hideKeyboard
import com.rodcollab.afazeres.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleValidationFields()

       setupShowRegistrationMessage()
    }


    private fun setupShowRegistrationMessage() {
        viewModel.showRegistrationSuccessDialog.observe(viewLifecycleOwner) { showRegistrationSuccessDialog ->
            if (showRegistrationSuccessDialog) {
                showBottomSheetDialog()
                findNavController().navigate(R.id.LoginFragment)
            }
        }
    }

    private fun handleValidationFields() {
        val emailEditText = binding.editTextEmail
        val passwordEditText = binding.editTextPassword
        val repeatPasswordEditText = binding.editTextRepeatPassword

        handleEmailField(emailEditText)

        handlePasswordField(emailEditText, passwordEditText, repeatPasswordEditText)

        handleRepeatPasswordEditText(
            emailEditText,
            passwordEditText,
            repeatPasswordEditText
        )


    }

    private fun handleRepeatPasswordEditText(
        emailEditText: EditText,
        passwordEditText: EditText,
        repeatPasswordEditText: EditText
    ) {
        repeatPasswordEditText.setOnFocusChangeListener { view, _ ->

            if (view.hasFocus()) {

                checkFields(
                    emailEditText,
                    view,
                    passwordEditText,
                    repeatPasswordEditText
                )

            }
        }
    }

    private fun checkFields(
        emailEditText: EditText,
        view: View,
        passwordEditText: EditText,
        repeatPasswordEditText: EditText
    ) {
        if (emailEditText.text.isBlank() || viewModel.emailIsValid.value == false) {

            changeFocusToAnotherView(view, emailEditText)

            sendEmailErrorMessage()

        } else if (passwordEditText.text.isBlank() || viewModel.passwordIsValid.value == false) {

            changeFocusToAnotherView(view, passwordEditText)

            viewModel.showPasswordError()
        } else if (viewModel.emailIsValid.value == false && viewModel.passwordIsValid.value == false) {

            sendEmailErrorMessage()
            changeFocusToAnotherView(view, emailEditText)

        } else {

            repeatPasswordEditText.onTextChanged { viewModel.onRepeatPasswordChanged(it) }
                .also {

                    repeatPasswordEditText.onTextChanged { viewModel.onRepeatPasswordChanged(it) }
                        .also {

                            repeatPasswordEditText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, event ->
                                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                                    if (passwordEditText.text.isNotBlank() && viewModel.repeatPasswordIsValid.value == true) {

                                        hideKeyboard(requireActivity())
                                    } else {
                                        viewModel.showRepeatPasswordError()
                                        repeatPasswordEditText.requestFocus()
                                    }
                                    return@OnEditorActionListener true
                                }
                                false
                            })
                        }

                }
        }
    }

    private fun handlePasswordField(
        emailEditText: EditText,
        passwordEditText: EditText,
        repeatPasswordEditText: EditText
    ) {

        passwordEditText.setOnFocusChangeListener { view, _ ->
            if (view.hasFocus()) {
                if (emailEditText.text.isBlank() || viewModel.emailIsValid.value == false) {
                    changeFocusToAnotherView(view, emailEditText)
                    sendEmailErrorMessage()
                } else {
                    passwordEditText.onTextChanged { viewModel.onPasswordChanged(it) }.also {

                        passwordEditText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, event ->
                            if (actionId == EditorInfo.IME_ACTION_NEXT || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                                if (passwordEditText.text.isNotBlank() && viewModel.passwordIsValid.value == true) {
                                    repeatPasswordEditText.requestFocus()
                                } else {
                                    viewModel.showPasswordError()
                                }
                                return@OnEditorActionListener true
                            }
                            false
                        })

                    }
                }
            }
        }
    }

    private fun changeFocusToAnotherView(view: View, anotherView: View) {
        view.clearFocus()
        anotherView.requestFocus()
    }

    private fun handleEmailField(emailEditText: EditText) {
        emailEditText.onTextChanged { viewModel.onEmailChanged(it) }
            .also {
                emailEditText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_NEXT || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (emailEditText.text.isNotBlank() && viewModel.emailIsValid.value == true) {
                            binding.editTextPassword.requestFocus()
                        } else {
                            sendEmailErrorMessage()
                        }
                        return@OnEditorActionListener true
                    }
                    false
                })
            }
    }

    private fun sendEmailErrorMessage() {
        viewModel.showEmailError()
    }

    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.bottom_sheet_dialog_registration_success)
        dialog.show()
    }


}