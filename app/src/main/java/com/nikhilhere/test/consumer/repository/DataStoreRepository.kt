package com.nikhilhere.test.consumer.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.nikhilhere.test.consumer.data.UserLocation
import com.nikhilhere.test.consumer.util.Constants.PREF_KEY_USER_LOCATION
import com.nikhilhere.test.consumer.util.Constants.PREF_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.Flow
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(PREF_NAME)

@ViewModelScoped
class DataStoreRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {

    private val dataStore: DataStore<Preferences> = context.dataStore
    private var TAG = "DataStoreRepository"


    private object MyPreferenceKeys {
        val userLocationKey = stringPreferencesKey(PREF_KEY_USER_LOCATION)
    }

    suspend fun saveUserLocation(userLocation: UserLocation) {
        val userLocationString: String = gson.toJson(userLocation)
        val result = dataStore.edit { preference ->
            preference[MyPreferenceKeys.userLocationKey] = userLocationString
        }
    }

    val readUserLocationFlow = dataStore.data.map { preference ->
        val userLocationString = preference[MyPreferenceKeys.userLocationKey] ?: null
        val userLocation =
            gson.fromJson<UserLocation>(userLocationString, UserLocation::class.java)
        userLocation
    }

}

