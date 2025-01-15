package com.example.gomath.ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gomath.data.local.GoMathDB
import com.example.gomath.data.loginFromApi
import com.example.gomath.model.LoginRequest
import com.example.gomath.model.UserSession
import com.example.gomath.model.User
import com.example.gomath.model.Users
import kotlinx.coroutines.launch
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject

class GoMathViewModel() : ViewModel() {
    private val loginError = mutableStateOf<String?>(null)

    private val _currentUser = MutableStateFlow<UserSession?>(null)

    private val _users = MutableStateFlow(Users())
    val users: StateFlow<Users> = _users.asStateFlow()

    private var codeActual: String = "";

    private lateinit var mSocket: Socket

    init {
        viewModelScope.launch {
            try {
                // mSocket = IO.socket("http://gomath.daw.inspedralbes.cat:21555")
                mSocket = IO.socket("http://10.0.2.2:3000")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("SocketIO", "Failed to connect to socket", e)
            }
            mSocket.connect()
            mSocket.on(Socket.EVENT_CONNECT) {
                Log.d("SocketIO", "Connected to socket: ${mSocket.id()}")
                mSocket.on("joined-success", onUserJoined)
                mSocket.on("update-users", onUpdateUsers)
                // mSocket.on("userLeft", onUserLeft)
                //mSocket.on("roomUserDetails", onRoomUsers)
            }
            mSocket.on(Socket.EVENT_DISCONNECT) {
                Log.d("SocketIO", "Disconnected from socket")
            }
        }
    }

    private val onUserJoined = Emitter.Listener { args ->
        val response = args[0] as JSONObject
        Log.d("SocketIO2", response.toString())
        val membersArray = response.optJSONArray("members")
        if (membersArray != null) {
            Log.d("SocketIO", membersArray.toString())
        }

        if (membersArray != null) {
            val userList = mutableListOf<User>()
            for (i in 0 until membersArray.length()) {
                val userJson = membersArray.getJSONObject(i)
                val user = User(
                    id = userJson.optString("id", ""),
                    name = userJson.optString("name", "")
                )
                userList.add(user)
            }
            _users.update { currentState ->
                currentState.copy(members = userList)
            }
            Log.d("SocketIO", "Usuarios actualizados tras unirse: ${_users.value.members}")
        } else {
            Log.w("SocketIO", "Lista de usuarios no proporcionada en el evento userJoined.")
        }
    }

    private val onUpdateUsers = Emitter.Listener { args ->
        val response = args[0] as JSONArray
        Log.d("SocketIO", response.toString())
        val userList = mutableListOf<User>()
        for (i in 0 until response.length()) {
            val userJson = response.getJSONObject(i)
            val user = User(
                id = userJson.optString("id", ""),
                name = userJson.optString("name", "")
            )
            userList.add(user)
        }
        _users.update { currentState ->
            currentState.copy(members = userList)
        }
    }

    fun getUserFromLocal(context: Context, onResult: (UserSession?) -> Unit) {
        viewModelScope.launch {
            val db = GoMathDB.getDatabase(context)
            val userSession = db.GoMathDao().getUser()
            _currentUser.value = userSession
            onResult(userSession)
        }
    }

    private fun saveUserToLocal(context: Context, user: UserSession) {
        viewModelScope.launch {
            val db = GoMathDB.getDatabase(context)
            db.GoMathDao().insertUser(user)
        }
    }

    fun login(email: String, password: String, context: Context, onSuccess: (Boolean) -> Unit) {
        val user = UserSession(email, "Professor")
        _currentUser.value = user

        if (user.role == "Professor") {
            saveUserToLocal(context, user)
            _currentUser.value = user
            onSuccess(true)
        } else {
            onSuccess(false)
        }
//        viewModelScope.launch {
//            val loginRequest = LoginRequest(email, password)
//            loginError.value = null
//
//            val result = loginFromApi(loginRequest)
//
//            if (result.isSuccess) {
//                val loginResponse = result.getOrNull()
//                if (loginResponse != null) {
//                    val user = UserSession(
//                        loginResponse.email,
//                        loginResponse.role
//                    )
//
//                    if (user.role == "Professor") {
//                        saveUserToLocal(context, user)
//                        _currentUser.value = user
//                        onSuccess(true) // Permitir acceso
//                    } else {
//                        onSuccess(false) // Denegar acceso
//                    }
//                } else {
//                    Log.e("Login", "Resposta correcta però el cos és nul o mal format. Comproveu la resposta de l'API.")
//                }
//            } else {
//                result.exceptionOrNull()?.let {
//                    loginError.value = "Error de xarxa o servidor. Si us plau, torna-ho a provar més tard."
//                }
//                onSuccess(false)
//            }
//        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            val db = GoMathDB.getDatabase(context)
            db.GoMathDao().deleteUser()
            _currentUser.value = null
            mSocket.disconnect()
        }
    }

    fun socket(code: String) {
        val data = JSONObject()
        data.put("roomCode", code)
        data.put("username", "Android")
        mSocket.emit("join-room", data)
        codeActual = code
    }

    fun kickUserFromRoom(user: User) {
        val data = JSONObject()
        data.put("roomCode", codeActual)
        data.put("id", user.id)
        mSocket.emit("kickUser", data)
        Log.d("kick", "Emitiendo kickUser: $data")
        _users.update { currentState ->
            currentState.copy(members = currentState.members.filter { it.id != user.id })
        }
        Log.d("kick", "L'Usuari: ${user.name} Ha estat eliminat")
    }

    fun resetCode(){
        codeActual = ""
    }
}