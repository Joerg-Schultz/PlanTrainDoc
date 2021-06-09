package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.databinding.AddPlanFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.models.PlanConstraint
import de.tierwohlteam.android.plantraindoc_v1.models.PlanHelper
import de.tierwohlteam.android.plantraindoc_v1.models.ReinforcementScheme
import de.tierwohlteam.android.plantraindoc_v1.others.Status
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.GoalViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.PlanViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AddPlanFragment : Fragment() {
    private val goalViewModel: GoalViewModel by activityViewModels()
    private val planViewModel: PlanViewModel by activityViewModels()

    private var _binding: AddPlanFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    @Named("DistanceScheme")
    lateinit var distanceScheme : ReinforcementScheme
    @Inject
    @Named("DurationScheme")
    lateinit var durationScheme : ReinforcementScheme

    private var constraintType = PlanConstraint.open
    private var constraintValue : Int? = null
    private var helperType = PlanHelper.free
    private var helperValue: String? = null

    private lateinit var discriminationInput: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = AddPlanFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        val header = getString(R.string.plan) + " " + goalViewModel.selectedGoal.value?.goal?.goal
        binding.addTrainingHeader.text = header
        setupHelperRadioGroup()
        setupConstraintRadioGroup()

        binding.buttonCancel.setOnClickListener {
            view.findNavController().popBackStack()
        }
        binding.buttonSaveplan.setOnClickListener {
            if(helperType == PlanHelper.discrimination) {
                helperValue = discriminationInput.text.toString()
            }
            lifecycleScope.launch {
                planViewModel.save(
                    goal = goalViewModel.selectedGoal.value?.goal,
                    constraintType = constraintType,
                    constraintValue = constraintValue,
                    helperType = helperType,
                    helperValue = helperValue
                )
            }
        }
    }

    private fun setupConstraintRadioGroup() {
        val constraintLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        //Repetition
        binding.rbConstraintRepetition.setOnCheckedChangeListener{ buttonView, isChecked ->
            if(isChecked){
                binding.conditionalConstraintHeader.text = getString(R.string.repetition)
                constraintType = PlanConstraint.repetition
                //Otherwise picker has to be moved
                constraintValue = PlanConstraint.defaultRepetition
                val numberPicker = NumberPicker(context)
                numberPicker.apply {
                    layoutParams = constraintLayoutParams
                    wrapSelectorWheel = true
                    minValue = 1
                    maxValue = 20
                    value = 5
                    setOnValueChangedListener { picker, oldVal, newVal ->
                        constraintValue = newVal
                    }
                }
                binding.conditionalConstraint.removeAllViewsInLayout()
                binding.conditionalConstraint.addView(numberPicker)
            }
        }
        // Time
        binding.rbConstraintTime.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked){
                binding.conditionalConstraintHeader.text = getString(R.string.time_based)
                constraintType = PlanConstraint.time
                constraintValue = PlanConstraint.defaultTime
                val timeSteps = (0..120 step 15).toList()
                val timeStepsString : Array<String> = timeSteps.map { it.toString() }.toTypedArray()
                val numberPicker = NumberPicker(context)
                numberPicker.apply {
                    layoutParams = constraintLayoutParams
                    wrapSelectorWheel = true
                    minValue = 0
                    maxValue = timeSteps.size - 1
                    displayedValues = timeStepsString
                    value = 4
                    setOnValueChangedListener { picker, oldVal, newVal ->
                        constraintValue = timeSteps[newVal]
                    }
                }
                binding.conditionalConstraint.removeAllViewsInLayout()
                binding.conditionalConstraint.addView(numberPicker)
            }
        }
        binding.rbConstraintFree.setOnCheckedChangeListener{ buttonView, isChecked ->
            if(isChecked){
                constraintType = PlanConstraint.open
                constraintValue = null
                binding.conditionalConstraintHeader.text = ""
                binding.conditionalConstraint.removeAllViewsInLayout()
                val textview = TextView(context)
                textview.layoutParams = constraintLayoutParams
                binding.conditionalConstraint.addView(textview)
            }
        }
    }

    private fun setupHelperRadioGroup() {
        val helperLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        //Distance
        binding.rbHelperDistance.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                helperType = PlanHelper.distance
                helperValue = PlanHelper.defaultDistance.toString()
                val numberPicker = NumberPicker(context)
                numberPicker.apply {
                    layoutParams = helperLayoutParams
                    wrapSelectorWheel = true
                    minValue = 1
                    displayedValues = distanceScheme.getLevels().map { it.toString() }.toTypedArray()
                    maxValue = distanceScheme.getLevels().size - 1
                    value = 5
                    setOnValueChangedListener { picker, oldVal, newVal ->
                        helperValue = distanceScheme.getLevels()[newVal-1].toString()
                    }
                }
                binding.condHelperHeader.text = getString(R.string.number)
                binding.conditionalHelper.removeAllViewsInLayout()
                binding.conditionalHelper.addView(numberPicker)
            }
        }
        //Duration
        binding.rbHelperDuration.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                helperType = PlanHelper.duration
                helperValue = PlanHelper.defaultDuration.toString()
                val numberPicker = NumberPicker(context)
                numberPicker.apply {
                    layoutParams = helperLayoutParams
                    wrapSelectorWheel = true
                    minValue = 1
                    displayedValues = durationScheme.getLevels().map { it.toString() }.toTypedArray()
                    maxValue = durationScheme.getLevels().size - 1
                    value = 5
                    setOnValueChangedListener { picker, oldVal, newVal ->
                        helperValue = durationScheme.getLevels()[newVal-1].toString()
                    }
                }
                binding.condHelperHeader.text = getString(R.string.seconds)
                binding.conditionalHelper.removeAllViewsInLayout()
                binding.conditionalHelper.addView(numberPicker)
            }
        }
        //Discrimination
        binding.rbHelperDiscrimination.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                helperType = PlanHelper.discrimination
                helperValue = null
                discriminationInput = EditText(activity)
                discriminationInput.apply {
                    setHint(R.string.discrimination_hint)
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    setPadding(20, 20, 20, 20)
                }
                binding.condHelperHeader.text = getString(R.string.values)
                binding.conditionalHelper.removeAllViewsInLayout()
                binding.conditionalHelper.addView(discriminationInput)
            }
        }
        //Cue Introduction
        binding.rbHelperCueintroduction.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                helperType = PlanHelper.cueIntroduction
                helperValue = PlanHelper.defaultCueIntroduction.toString()
                val percentages = arrayOf("0","25","50","75","100")
                val numberPicker = NumberPicker(context)
                numberPicker.apply {
                    layoutParams = helperLayoutParams
                    wrapSelectorWheel = true
                    minValue = 1
                    displayedValues = percentages
                    maxValue = percentages.size
                    value = 3
                    setOnValueChangedListener { picker, oldVal, newVal ->
                        helperValue = percentages[newVal-1].toFloat().toString()
                    }
                }
                binding.condHelperHeader.text = getString(R.string.percentages)
                binding.conditionalHelper.removeAllViewsInLayout()
                binding.conditionalHelper.addView(numberPicker)
            }
        }
        binding.rbHelperFree.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                helperType = PlanHelper.free
                helperValue = null
                binding.condHelperHeader.text = ""
                binding.conditionalHelper.removeAllViewsInLayout()
            }
        }
    }

    private fun subscribeToObservers() {
        //Did the insert work?
        planViewModel.insertPlanStatus.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.ERROR -> {
                        Snackbar.make(
                            binding.root,
                            result.message ?: "An unknown error occurred",
                            Snackbar.LENGTH_LONG
                        ).setAnchorView(R.id.button_saveplan)
                            .show()
                    }
                    Status.SUCCESS -> {
                        Snackbar.make(
                            binding.root,
                            "Added Training Plan",
                            Snackbar.LENGTH_LONG
                        ).show()
                        goalViewModel.setSelectedGoal(null)
                        findNavController().popBackStack()
                    }
                    else -> {
                    }
                }
            }
        })
    }

}