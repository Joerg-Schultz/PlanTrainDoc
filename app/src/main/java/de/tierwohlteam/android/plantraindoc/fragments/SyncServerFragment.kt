package de.tierwohlteam.android.plantraindoc.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import de.tierwohlteam.android.plantraindoc.databinding.SyncServerFragmentBinding
import de.tierwohlteam.android.plantraindoc.others.Status
import de.tierwohlteam.android.plantraindoc.viewmodels.ServerViewModel

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
                        binding.pbSyncgoals.visibility = View.INVISIBLE
                        binding.tvSyncgoalsDone.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        /* NO-OP */
                    }
                    Status.LOADING -> {
                        binding.pbSyncgoals.visibility = View.VISIBLE
                    }
                    else -> { /*NO-OP*/ }
                }
            }
        })
        serverViewModel.syncPlansStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS -> {
                        binding.pbSyncplans.visibility = View.INVISIBLE
                        binding.tvSyncplansDone.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        /* NO-OP */
                    }
                    Status.LOADING -> {
                        binding.pbSyncplans.visibility = View.VISIBLE
                    }
                    else -> { /*NO-OP*/ }
                }
            }
        })
        serverViewModel.syncTrainingStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS -> {
                        binding.pbSynctraining.visibility = View.INVISIBLE
                        binding.tvSynctrainingDone.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        /* NO-OP */
                    }
                    Status.LOADING -> {
                        binding.pbSynctraining.visibility = View.VISIBLE
                    }
                    else -> { /*NO-OP*/ }
                }
            }
        })

    }
}