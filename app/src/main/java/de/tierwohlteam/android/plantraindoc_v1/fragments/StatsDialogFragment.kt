package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import de.tierwohlteam.android.plantraindoc_v1.databinding.StatsGoalValuesBinding
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.StatisticsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi

@InternalCoroutinesApi
class StatsDialogFragment : DialogFragment() {
    private val statisticsViewModel: StatisticsViewModel by activityViewModels()
    private var _binding: StatsGoalValuesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StatsGoalValuesBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
}