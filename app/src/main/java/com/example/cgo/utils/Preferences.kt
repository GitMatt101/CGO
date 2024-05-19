package com.example.cgo.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)

    fun saveData(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getData(key: String, defaultValue: String) : String = sharedPreferences.getString(key, defaultValue) ?: defaultValue

    fun containsKey(key: String): Boolean = sharedPreferences.contains(key)

    fun clearPreferences() = sharedPreferences.edit().clear().apply()
}