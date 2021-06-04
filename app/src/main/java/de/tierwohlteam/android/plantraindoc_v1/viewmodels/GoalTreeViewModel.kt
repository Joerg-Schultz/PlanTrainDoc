package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import javax.inject.Inject

@HiltViewModel
class GoalTreeViewModel @Inject constructor(
    repository: PTDRepository
) : ViewModel() {

    private val currentGoal = null //Change of this variable should drive the recreation of view
    val goals = repository.getChildGoals(parent = currentGoal)
}