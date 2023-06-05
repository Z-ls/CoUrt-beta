package it.polito.mad.court

import android.content.Context
import com.google.gson.Gson
import it.polito.mad.court.dataclass.User
import java.time.LocalDate
import java.time.LocalTime

object SharedPreferencesHelper {
    private const val PREFERENCES_NAME = "user"
    private val gson = Gson().newBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
        .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
        .registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
        .registerTypeAdapter(LocalTime::class.java, LocalTimeDeserializer())
        .create()

    fun saveUserData(context: Context, user: User) {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user", gson.toJson(user))
        editor.apply()
    }

    fun getUserData(context: Context): User {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val userJSON = sharedPreferences.getString("user", null)
        return gson.fromJson(userJSON.toString(), User::class.java)
    }
}