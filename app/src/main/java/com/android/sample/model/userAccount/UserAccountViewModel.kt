package com.android.sample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.sample.model.userAccount.UserAccount
import com.android.sample.model.userAccount.UserAccountRepository
import com.android.sample.model.userAccount.UserAccountRepositoryFirestore
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class UserAccountViewModel(private val repository: UserAccountRepository) : ViewModel() {

    private val _userAccount = MutableStateFlow<UserAccount?>(null)
    val userAccount: StateFlow<UserAccount?>
        get() = _userAccount.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading.asStateFlow()

    fun getUserAccount(userId: String) {
        _isLoading.value = true
        repository.getUserAccount(
            userId = userId,
            onSuccess = {
                _userAccount.value = it
                _isLoading.value = false
            },
            onFailure = {
                _userAccount.value = null
                _isLoading.value = false
            })
    }

    fun createUserAccount(userAccount: UserAccount) {
        repository.createUserAccount(
            userAccount, onSuccess = { _userAccount.value = userAccount }, onFailure = {})
    }

    fun updateUserAccount(userAccount: UserAccount) {
        repository.updateUserAccount(
            userAccount, onSuccess = { getUserAccount(userAccount.userId) }, onFailure = {})
    }

    // Factory for creating instances of the ViewModel
    companion object {
        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return UserAccountViewModel(
                        UserAccountRepositoryFirestore(FirebaseFirestore.getInstance()))
                            as T
                }
            }
    }
}