package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.benasher44.uuid.Uuid
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.others.Event
import de.tierwohlteam.android.plantraindoc_v1.others.Resource
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import javax.inject.Inject

@HiltViewModel
class AddModifyGoalViewModel @Inject constructor(
    private val repository: PTDRepository,
    private val userID: Uuid
): ViewModel() {
    val goal: Goal? = null
    private val parentGoal: Goal? = null //get from repository later

    private val _insertGoalStatus = MutableLiveData<Event<Resource<Goal>>>(Event(Resource.empty()))
    val insertGoalStatus: LiveData<Event<Resource<Goal>>> = _insertGoalStatus

    //TODO Add FlowState(?) for status of insert
    // https://github.com/philipplackner/ShoppingListTestingYT/blob/TestItemDeletion/app/src/main/java/com/androiddevs/shoppinglisttestingyt/ui/ShoppingViewModel.kt
    fun saveGoal(goalText: String, description: String, status: String?) {
        if(goalText.isEmpty()) {
            _insertGoalStatus.postValue(Event(Resource.error("The fields must not be empty", null)))
            return
        }

        //get UserId from shared prefs
        val newGoal = Goal(goal = goalText, description = description, parents = parentGoal?.id,
        userID = userID)
        if(status != null) newGoal.status = status
        repository.insertGoal(newGoal)
        _insertGoalStatus.postValue(Event(Resource.success(newGoal)))
    }


}