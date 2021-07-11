package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benasher44.uuid.Uuid
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.DEFAULT_USER_EMAIL
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.DEFAULT_USER_NAME
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.DEFAULT_USER_PASSWORD
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_LOGGED_IN_EMAIL
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_LOGGED_IN_NAME
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_PASSWORD
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.LAST_SYNC_DATE
import de.tierwohlteam.android.plantraindoc_v1.others.Resource
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.datetime.toLocalDateTime
import okhttp3.internal.wait
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
    private val repository: PTDRepository,
    private val userID : Uuid,
    private val sharedPrefs: SharedPreferences
) : ViewModel() {

    //TODO change LiveData to Flow
    private val _registerStatus = MutableLiveData<Resource<String>>()
    val registerStatus : LiveData<Resource<String>> = _registerStatus

    private val _loginStatus = MutableLiveData<Resource<String>>()
    val loginStatus : LiveData<Resource<String>> = _loginStatus

    private val _syncGoalsStatus = MutableLiveData<Resource<List<Goal>>>()
    val syncGoalsStatus : LiveData<Resource<List<Goal>>> = _syncGoalsStatus

    fun register(name: String, eMail: String, password: String, repeatedPassword: String){
        _registerStatus.postValue(Resource.loading(null))
        if(eMail.isEmpty() || password.isEmpty() || name.isEmpty() || repeatedPassword.isEmpty()) {
            _registerStatus.postValue(Resource.error("Please fill out all the fields", null))
            return
        }
        if(password != repeatedPassword) {
            _registerStatus.postValue(Resource.error("The passwords do not match", null))
            return
        }
        viewModelScope.launch {
            val result = repository.register(id = userID, name = name, eMail = eMail, password = password)
            _registerStatus.postValue(result)
        }
    }

    fun login(name: String, eMail: String, password: String){
        _loginStatus.postValue(Resource.loading(null))
        if(eMail.isEmpty() || password.isEmpty() || name.isEmpty()) {
            _loginStatus.postValue(Resource.error("Please fill out all the fields", null))
            return
        }

        viewModelScope.launch {
            val result = repository.login(id = userID, name = name, eMail = eMail, password = password)
            _loginStatus.postValue(result)
        }
    }

    fun logout(){
        with (sharedPrefs.edit()) {
            putString(KEY_LOGGED_IN_EMAIL, DEFAULT_USER_EMAIL)
            putString(KEY_LOGGED_IN_NAME, DEFAULT_USER_NAME)
            putString(KEY_PASSWORD, DEFAULT_USER_PASSWORD)
            putBoolean("useWebServer", false)
            apply()
        }
        _loginStatus.postValue(Resource.success(null))
    }

    suspend fun synchronize() {
        _syncGoalsStatus.postValue(Resource.loading(null))
        val lastSyncDate = sharedPrefs.getString(LAST_SYNC_DATE, null)?.toLocalDateTime()
        var newGoalsLocal: List<Goal> = listOf()
        val localJob = viewModelScope.launch(Dispatchers.IO){
            newGoalsLocal = repository.getNewGoalsLocal(lastSyncDate)
        }
        var newGoalsRemote: List<Goal> = listOf()
       val remoteJob = viewModelScope.launch(Dispatchers.IO){
            newGoalsRemote = repository.getNewGoalsRemote(lastSyncDate)
        }
        localJob.join()
        remoteJob.join()

        val localOnlyGoals = newGoalsLocal.filter {
                local -> newGoalsRemote.none { it.id == local.id } }
        Log.d("SYNC", localOnlyGoals.toString())
        viewModelScope.launch(Dispatchers.IO) {
            repository.putGoalsRemote(localOnlyGoals)
            _syncGoalsStatus.postValue(Resource.success(localOnlyGoals))
        }

    }
}