package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import javax.inject.Inject

@HiltViewModel
class AddModifyGoalViewModel @Inject constructor(
    private val repository: PTDRepository
): ViewModel() {
    //TODO Add FlowState(?) for status of insert
    // https://github.com/philipplackner/ShoppingListTestingYT/blob/TestItemDeletion/app/src/main/java/com/androiddevs/shoppinglisttestingyt/ui/ShoppingViewModel.kt
    fun saveGoal(goal: String?, description: String?, status: String?) {

    }

    val goal: Goal? = null

}