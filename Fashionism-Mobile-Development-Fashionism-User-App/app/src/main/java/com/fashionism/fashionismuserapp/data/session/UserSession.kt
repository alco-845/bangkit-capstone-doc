package com.fashionism.fashionismuserapp.data.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.fashionism.fashionismuserapp.data.db.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserSession private constructor(private val dataStore: DataStore<Preferences>) {

    fun getLoginSession(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[LOGIN_SESSION] ?: false
        }
    }

    suspend fun saveLoginSession(loginSession: Boolean) {
        dataStore.edit { preferences ->
            preferences[LOGIN_SESSION] = loginSession
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN] ?: ""
        }
    }


    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    fun getName(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[NAME] ?: ""
        }
    }

    suspend fun saveName(name: String) {
        dataStore.edit { preferences ->
            preferences[NAME] = name
        }
    }

    fun getIdUser(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[ID_USER] ?: 0
        }
    }

    suspend fun saveIdUser(idUser: Int) {
        dataStore.edit { preferences ->
            preferences[ID_USER] = idUser
        }
    }

    fun getEmail(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[EMAIL] ?: ""
        }
    }

    suspend fun saveEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[EMAIL] = email
        }
    }

    suspend fun clearDataLogin() {
        dataStore.edit { preferences ->
            preferences.remove(LOGIN_SESSION)
            preferences.remove(TOKEN)
            preferences.remove(NAME)
            preferences.remove(ID_USER)
            preferences.remove(EMAIL)
        }
    }

    suspend fun setAllUserData(loginSession: Boolean, token: String, name: String, idUser: Int, email: String) {
        dataStore.edit { preferences ->
            preferences[LOGIN_SESSION] = loginSession
            preferences[TOKEN] = token
            preferences[NAME] = name
            preferences[ID_USER] = idUser
            preferences[EMAIL] = email
        }
    }

    fun getAllUserData(): Flow<UserData> {
        return dataStore.data.map { preferences ->
            UserData(
                loginSession = preferences[LOGIN_SESSION] ?: false,
                token = preferences[TOKEN] ?: "",
                name = preferences[NAME] ?: "",
                idUser = preferences[ID_USER] ?: 0,
                email = preferences[EMAIL] ?: ""
            )
        }
    }



    companion object {
        @Volatile
        private var INSTANCE: UserSession? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserSession {
            return INSTANCE ?: synchronized(this) {
                val instance = UserSession(dataStore)
                INSTANCE = instance
                instance
            }
        }

        private val LOGIN_SESSION = booleanPreferencesKey("login_session")
        private val TOKEN = stringPreferencesKey("token")
        private val NAME = stringPreferencesKey("name")
        private val ID_USER = intPreferencesKey("id_user")
        private val EMAIL = stringPreferencesKey("email")

    }
}