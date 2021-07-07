package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.databinding.RegisterServerFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.others.Status
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.ServerViewModel
import javax.inject.Inject

@AndroidEntryPoint
class RegisterServerFragment : Fragment() {
    private val serverViewModel: ServerViewModel by activityViewModels()

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    private var _binding: RegisterServerFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = RegisterServerFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        binding.btnRegister.setOnClickListener {
            val name: String = binding.tiUserNameRegister.text.toString()
            val email: String = binding.tiUserEmailRegister.text.toString()
            val password: String = binding.tiPasswordRegister.text.toString()
            val repeatedPassword: String = binding.tiPasswordConfirmRegister.text.toString()
            serverViewModel.register(name = name, eMail = email,
                password = password, repeatedPassword = repeatedPassword)
        }
    }

    private fun subscribeToObservers(){
        serverViewModel.registerStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when(result.status){
                    Status.SUCCESS -> {
                        binding.progressBarRegister.visibility = View.GONE
                        Snackbar.make(binding.root,
                            result.data ?: "Successfully registered, you can now log in",
                            Snackbar.LENGTH_SHORT).show()
                        with (sharedPrefs.edit()) {
                            putBoolean("hasAccount",true)
                            apply()
                        }
                        findNavController().navigate(R.id.action_registerServerFragment_to_loginServerFragment)
                    }
                    Status.ERROR -> {
                        binding.progressBarRegister.visibility = View.GONE
                        Snackbar.make(binding.root,
                            result.message ?: "An unknown error occurred",
                            Snackbar.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {
                        binding.progressBarRegister.visibility = View.VISIBLE
                    }
                }
            }
        })
    }
}