package com.aataganov.muvermockup

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
        return LoginResponse("sample-token")
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
}

abstract class DrivingManagerCommand
class DMAllCommand(val targetState: Boolean) : DrivingManagerCommand()
class DMAppCommand(val app: String, public val targetState: Boolean) : DrivingManagerCommand()

data class CommandExecutionResult(
    val isSuccess: Boolean
)

data class DrivingManagerState(
    val driverState: Boolean,
    val applicationStates: HashMap<String, Boolean>
)


class DrivingManagerImpl : DrivingManager {
    companion object{
        private val State = DrivingManagerState(true, HashMap())
    }
    override suspend fun executeCommand(command: DrivingManagerCommand): CommandExecutionResult {
        delay(500)
        if (command is DMAllCommand) {
            State.applicationStates.keys.forEach {key ->
                State.applicationStates[key] = command.targetState
            }
        }
        else if (command is DMAppCommand) {
            State.applicationStates[command.app] = command.targetState
        }
        delay(500)
        return CommandExecutionResult(true)
    }

    override suspend fun getState(): DrivingManagerState {
        val drivingState = State.applicationStates.any { it.value }
        return DrivingManagerState(drivingState, State.applicationStates)
    }
}