package com.example.dipl.presentation

import android.content.Context
import android.content.SharedPreferences
import com.example.dipl.domain.model.User
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object PrefManager {
    private const val KEY_USER: String = "USER_KEY"
    private const val PREF_NAME = "MyPrefs"
    private const val KEY_LOGGED_IN = "isLoggedIn"

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val userAdapter: JsonAdapter<User> = moshi.adapter(User::class.java)

    fun setLoggedInState(context: Context, isLoggedIn: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(KEY_LOGGED_IN, isLoggedIn).apply()
    }

    fun getLoggedInState(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false)
    }

    fun saveUser(context: Context, user: User?) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val userJson = userAdapter.toJson(user)
        editor.putString(KEY_USER, userJson)
        editor.apply()
    }

    fun updateUser(context: Context, updatedUser: User) {
        val userJson = userAdapter.toJson(updatedUser)
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER, userJson)
        editor.apply()
    }

    fun getUser(context: Context): User? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val userJson = prefs.getString(KEY_USER, null)
        return userJson?.let { userAdapter.fromJson(it) }
    }

}