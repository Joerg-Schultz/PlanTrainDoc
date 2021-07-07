package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.benasher44.uuid.Uuid
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.databinding.LoginServerFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.DEFAULT_USER_EMAIL
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.DEFAULT_USER_PASSWORD
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_LOGGED_IN_EMAIL
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_LOGGED_IN_NAME
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_PASSWORD
import de.tierwohlteam.android.plantraindoc_v1.others.Status
import de.tierwohlteam.android.plantraindoc_v1.repositories.remote.BasicAuthInterceptor
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.ServerViewModel
import javax.inject.Inject

@AndroidEntryPoint
class LoginServerFragment : Fragment() {
    private val serverViewModel: ServerViewModel by activityViewModels()

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    @Inject
    lateinit var basicAuthInterceptor: BasicAuthInterceptor

    @Inject
    lateinit var userID: Uuid

    private var _binding: LoginServerFragmentBinding? = null
    private val binding get() = _binding!!

    private var curEmail : String? = null
    private var curName: String? = null
    private var curPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            sharedPrefs.edit().putBoolean("useWebServer", false).apply()
            redirectLogin()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = LoginServerFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(isLoggedIn()){
            authenticateApi(curPassword ?: "")
            redirectLogin()
        }
        subscribeToObservers()

        binding.btnLogin.setOnClickListener {
            val email = binding.tiUserEmailLogin.text.toString()
            val name = binding.tiUserNameLogin.text.toString()
            val password = binding.tiPasswordLogin.text.toString()
            curEmail = email
            curName = name
            curPassword = password
            serverViewModel.login(name = name, eMail = email, password = password)
        }
    }

    private fun isLoggedIn(): Boolean {
        curEmail = sharedPrefs.getString(KEY_LOGGED_IN_EMAIL, DEFAULT_USER_EMAIL) ?: DEFAULT_USER_EMAIL
        curPassword = sharedPrefs.getString(KEY_PASSWORD, DEFAULT_USER_PASSWORD) ?: DEFAULT_USER_PASSWORD
        return curEmail != DEFAULT_USER_EMAIL && curPassword != DEFAULT_USER_PASSWORD
    }
    private fun authenticateApi(password: String){
        basicAuthInterceptor.id = userID
        basicAuthInterceptor.password = password
    }
    private fun redirectLogin(){
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.settingsFragment, true)
            .build()
        findNavController().navigate(
            R.id.action_loginServerFragment_to_settingsFragment,
            null,
            navOptions
        )
    }
    private fun subscribeToObservers(){
        serverViewModel.loginStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when(result.status) {
                    Status.SUCCESS -> {
                        if (result.data != null) {
                            binding.progressBarLogin.visibility = View.GONE
                            Snackbar.make(
                                binding.root,
                                result.data ?: "Successfully logged in",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            with(sharedPrefs.edit()) {
                                putString(KEY_LOGGED_IN_EMAIL, curEmail)
                                putString(KEY_LOGGED_IN_NAME, curName)
                                putString(KEY_PASSWORD, curPassword)
                                apply()
                            }
                            authenticateApi(curPassword ?: "")
                            redirectLogin()
                        }
                    }
                    Status.ERROR -> {
                        binding.progressBarLogin.visibility = View.GONE
                        sharedPrefs.edit().putBoolean("useWebServer", false).apply()
                        Snackbar.make(binding.root,
                            result.message ?: "An unknown error occurred",
                            Snackbar.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {
                        binding.progressBarLogin.visibility = View.VISIBLE
                    }
                }
            }
        })
    }
}