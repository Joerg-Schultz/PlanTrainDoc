package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.databinding.AddPlanFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.models.ReinforcementScheme
import de.tierwohlteam.android.plantraindoc_v1.others.Status
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.GoalViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AddPlanFragment : Fragment() {
    private val viewModel: GoalViewModel by activityViewModels()
    private var _binding: AddPlanFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    @Named("DistanceScheme")
    lateinit var distanceScheme : ReinforcementScheme
    @Inject
    @Named("DurationScheme")
    lateinit var durationScheme : ReinforcementScheme

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = AddPlanFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        val header = getString(R.string.plan) + " " + viewModel.selectedGoal.value?.goal
        binding.addTrainingHeader.text = header
        setupHelperRadioGroup()
        setupConstraintRadioGroup()
    }

    private fun setupConstraintRadioGroup() {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        //Repetition
        binding.rbConstraintRepetition.setOnCheckedChangeListener{ buttonView, isChecked ->
            if(isChecked){
                binding.conditionalConstraintHeader.text = getString(R.string.repetition)
                val numberPicker = NumberPicker(context)
                numberPicker.layoutParams = layoutParams
                numberPicker.wrapSelectorWheel = true
                var periodType = "count"
                numberPicker.minValue = 1
                numberPicker.maxValue = 20
                numberPicker.value = 5
                var periodLimit = 5
                numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                    periodLimit = newVal
                }
                binding.conditionalConstraint.removeAllViewsInLayout()
                binding.conditionalConstraint.addView(numberPicker)
            }
        }
        // Time
        binding.rbConstraintTime.setOnCheckedChangeListener{ buttonView, isChecked ->
            if(isChecked){
                binding.conditionalConstraintHeader.text = getString(R.string.time_based)
                val numberPicker = NumberPicker(context)
                numberPicker.layoutParams = layoutParams
                numberPicker.wrapSelectorWheel = true
                val timeSteps = (0..120 step 15).toList()
                val timeStepsString : Array<String> = timeSteps.map { it.toString() }.toTypedArray()
                numberPicker.minValue = 0
                numberPicker.maxValue = timeSteps.size - 1
                numberPicker.displayedValues = timeStepsString
                numberPicker.value = 4
                var periodLimit = 60
                numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                    periodLimit = newVal
                }
                binding.conditionalConstraint.removeAllViewsInLayout()
                binding.conditionalConstraint.addView(numberPicker)
            }
        }
        binding.rbConstraintFree.setOnCheckedChangeListener{ buttonView, isChecked ->
            if(isChecked){
                binding.conditionalConstraintHeader.text = ""
                binding.conditionalConstraint.removeAllViewsInLayout()
                val textview = TextView(context)
                textview.layoutParams = layoutParams
                binding.conditionalConstraint.addView(textview)
            }
        }
    }

    private fun setupHelperRadioGroup() {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        //Distance
        binding.rbHelperDistance.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                val numberPicker = NumberPicker(context)
                numberPicker.layoutParams = layoutParams
                numberPicker.wrapSelectorWheel = true
                numberPicker.minValue = 1
                numberPicker.displayedValues = distanceScheme.getLevels().map { it.toString() }.toTypedArray()
                numberPicker.maxValue = distanceScheme.getLevels().size - 1
                numberPicker.value = 5
                numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                    //periodCriterionValue = distanceScheme.keys.toTypedArray()[newVal - 1].toFloat()
                    //Toast.makeText(activity,scheme.keys.toTypedArray()[newVal - 1],Toast.LENGTH_SHORT).show()
                }
                binding.condHelperHeader.text = getString(R.string.number)
                binding.conditionalHelper.removeAllViewsInLayout()
                binding.conditionalHelper.addView(numberPicker)
            }
        }
        //Duration
        binding.rbHelperDuration.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val numberPicker = NumberPicker(context)
                numberPicker.layoutParams = layoutParams
                numberPicker.wrapSelectorWheel = true
                numberPicker.minValue = 1
                numberPicker.displayedValues = durationScheme.getLevels().map { it.toString() }.toTypedArray()
                numberPicker.maxValue = durationScheme.getLevels().size - 1
                numberPicker.value = 5
                numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                    //periodCriterionValue = scheme.keys.toTypedArray()[newVal - 1].toFloat()
                    //Toast.makeText(activity,scheme.keys.toTypedArray()[newVal - 1],Toast.LENGTH_SHORT).show()
                }
                binding.condHelperHeader.text = getString(R.string.seconds)
                binding.conditionalHelper.removeAllViewsInLayout()
                binding.conditionalHelper.addView(numberPicker)
            }
        }
        //Discrimination
        binding.rbHelperDiscrimination.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                val discriminationInput = EditText(activity)
                discriminationInput.setHint(R.string.discrimination_hint)
                discriminationInput.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                discriminationInput.setPadding(20, 20, 20, 20)
                binding.condHelperHeader.text = getString(R.string.values)
                binding.conditionalHelper.removeAllViewsInLayout()
                binding.conditionalHelper.addView(discriminationInput)
            }
        }
        //Cue Introduction
        binding.rbHelperCueintroduction.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                val percentages = arrayOf("0","25","50","75","100")
                val numberPicker = NumberPicker(context)
                numberPicker.layoutParams = layoutParams
                numberPicker.wrapSelectorWheel = true
                numberPicker.minValue = 1
                numberPicker.displayedValues = percentages
                numberPicker.maxValue = percentages.size
                numberPicker.value = 3
                numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                    //periodCriterionValue = percentages[newVal - 1].toFloat()
                    //Toast.makeText(activity,scheme.keys.toTypedArray()[newVal - 1],Toast.LENGTH_SHORT).show()
                }
                binding.condHelperHeader.text = getString(R.string.percentages)
                binding.conditionalHelper.removeAllViewsInLayout()
                binding.conditionalHelper.addView(numberPicker)
            }
        }
        binding.rbHelperFree.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.condHelperHeader.text = ""
                binding.conditionalHelper.removeAllViewsInLayout()
            }
        }
    }

    private fun subscribeToObservers() {
        //Did the insert work?
        viewModel.insertGoalStatus.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.ERROR -> {
                        Snackbar.make(
                            binding.root,
                            result.message ?: "An unknown error occurred",
                            Snackbar.LENGTH_LONG
                        ).setAnchorView(R.id.button_savegoal)
                            .show()
                    }
                    Status.SUCCESS -> {
                        Snackbar.make(
                            binding.root,
                            "Added Training Plan",
                            Snackbar.LENGTH_LONG
                        ).show()
                        viewModel.setSelectedGoal(null)
                        findNavController().popBackStack()
                    }
                    else -> {
                    }
                }
            }
        })
    }

}