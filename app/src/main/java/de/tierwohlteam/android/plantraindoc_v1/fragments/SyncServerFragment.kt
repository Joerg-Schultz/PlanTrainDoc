package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import de.tierwohlteam.android.plantraindoc_v1.databinding.SyncServerFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.others.Status
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.ServerViewModel
import kotlinx.coroutines.flow.collect

class SyncServerFragment: Fragment() {

    private val serverViewModel: ServerViewModel by activityViewModels()

    private var _binding: SyncServerFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SyncServerFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        binding.btnSync.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                serverViewModel.synchronize()
            }
        }
    }

    private fun subscribeToObservers() {
        serverViewModel.syncGoalsStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        var resultText = "Hallo "
                        result.data!!.forEach{
                            Log.d("SYNC", "Goal: ${it.goal}")
                            resultText = resultText.plus("${it.goal}\n")}
                        binding.tvGoalsLocal.text = resultText
                    }
                    Status.ERROR -> {
                        /* NO-OP */
                    }
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    else -> { /*NO-OP*/ }
                }
            }
        })
    }
}