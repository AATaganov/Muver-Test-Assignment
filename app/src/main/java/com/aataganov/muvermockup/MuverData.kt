package com.aataganov.muvermockup

import android.text.format.DateUtils
import kotlinx.coroutines.delay

interface BackendApi {
    suspend fun login(phone: String, code: String, state: Int): LoginResponse
    suspend fun loadProfile(token: String): Profile
}

data class LoginResponse(
    val accessToken: String
)

data class Profile(
    val id: Long,
    val phone: String,
    val isEnabled: Boolean
)

class BackendApiImpl : BackendApi {
    override suspend fun login(phone: String, code: String, state: Int): LoginResponse {
        delay(500)
        return LoginResponse(phone)
    }

    override suspend fun loadProfile(token: String): Profile{
        delay(500)

        // для примера блокировки пользователя при окончании триала
        val isEnabled = token.length % 2 == 0

        if (token.startsWith("a")) {
            return Profile(1, "a", isEnabled)
        }

        return Profile(2, "b", isEnabled)
    }
}

interface DrivingManager {
    suspend fun executeCommand(command: DrivingManagerCommand): CommandExecutionResult
    suspend fun getState(): DrivingManagerState
    fun updateApplications(applications: List<String>)
}

abstract class DrivingManagerCommand
class DMAllCommand(val targetState: Boolean) : DrivingManagerCommand()
class DMAppCommand(val app: String, val targetState: Boolean) : DrivingManagerCommand()

data class CommandExecutionResult(
    val isSuccess: Boolean
)

data class DrivingManagerState(
    val driverState: Boolean,
    val applicationStates: HashMap<String, Boolean>
)


class DrivingManagerImpl : DrivingManager {


    var applicationStates: HashMap<String, Boolean> = hashMapOf()

    override fun updateApplications(applications: List<String>){
        val newHash: HashMap<String, Boolean> = hashMapOf()
        applications.forEach {
            newHash[it] = applicationStates[it] ?: false
        }
        applicationStates = newHash
    }

    override suspend fun executeCommand(command: DrivingManagerCommand): CommandExecutionResult {
        delay(500)

        //imitate failure
        if(simulateFail()){
            return CommandExecutionResult(false)
        }

        if (command is DMAllCommand) {
            applicationStates.keys.forEach {key ->
                applicationStates[key] = command.targetState
            }
        }
        else if (command is DMAppCommand) {
            applicationStates[command.app] = command.targetState
        }
        delay(500)
        return CommandExecutionResult(true)
    }

    private fun simulateFail(): Boolean{
        val time = System.currentTimeMillis()
        return (time % DateUtils.SECOND_IN_MILLIS < 100)
    }

    override suspend fun getState(): DrivingManagerState {
        val drivingState = applicationStates.any { it.value }
        return DrivingManagerState(drivingState, applicationStates)
    }
}