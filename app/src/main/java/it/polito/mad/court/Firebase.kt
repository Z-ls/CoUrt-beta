package it.polito.mad.court

import androidx.compose.runtime.MutableState
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import it.polito.mad.court.dataclass.Court
import it.polito.mad.court.dataclass.Reservation
import it.polito.mad.court.dataclass.User
import kotlinx.coroutines.tasks.await
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.max
import kotlin.math.min

class LocalDateSerializer : JsonSerializer<LocalDate> {
    override fun serialize(
        src: LocalDate?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return JsonPrimitive(src?.format(formatter))
    }
}

class LocalDateDeserializer : JsonDeserializer<LocalDate> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return LocalDate.parse(json?.asString, formatter)
    }
}

class LocalTimeSerializer : JsonSerializer<LocalTime> {
    override fun serialize(
        src: LocalTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return JsonPrimitive(src?.format(formatter))
    }
}

class LocalTimeDeserializer : JsonDeserializer<LocalTime> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalTime {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return LocalTime.parse(json?.asString, formatter)
    }
}

@Suppress("unused")
class DbCourt {
    private val gson = Gson().newBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
        .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
        .registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
        .registerTypeAdapter(LocalTime::class.java, LocalTimeDeserializer())
        .create()
    private var db = FirebaseDatabase.getInstance()

    private fun getReference(
        name: String,
    ): DatabaseReference {
        return db.getReference(name)
    }

    fun getUsers(
        callback: (List<User>) -> Unit,
    ) {
        val refs = getReference(
            "users",
        )
        refs.get().addOnSuccessListener {
            val users = mutableListOf<User>()
            for (data in it.children) {
                val user = gson.fromJson(data.value.toString(), User::class.java)!!
                users.add(user)
            }
            callback(users)
        }
    }

    fun getUserByEmail(
        email: String,
        callback: (User) -> Unit,
    ) {
        val refs = getReference(
            "users",
        )
        refs.child(emailToKey(email)).get().addOnSuccessListener {
            val user = gson.fromJson(it.value.toString(), User::class.java)!!
            callback(user)
        }
    }

    fun addUser(
        user: User,
    ) {
        val refs = getReference(
            "users",
        )
        user.email = emailToKey(user.email)
        refs.push().setValue(gson.toJson(user))
    }

    fun updateUser(
        user: User,
    ) {
        val refs = getReference(
            "users",
        )
        refs.child(emailToKey(user.email)).setValue(gson.toJson(user))
    }

    fun deleteUser(
        user: User,
    ) {
        val refs = getReference(
            "users",
        )
        refs.child(emailToKey(user.email)).removeValue()
    }

    fun getReservations(
        callback: (List<Reservation>) -> Unit,
    ) {
        val refs =
            getReference(
                "reservations",
            )
        refs.get().addOnSuccessListener {
            val reservations = mutableListOf<Reservation>()
            for (data in it.children) {
                val res = gson.fromJson(data.value.toString(), Reservation::class.java)!!
                res.id = data.key!!
                reservations.add(res)
            }
            callback(reservations)
        }
    }

    fun getReservationById(
        id: String,
        callback: (Reservation) -> Unit,
    ) {
        val refs =
            getReference(
                "reservations",
            )
        refs.child(id).get().addOnSuccessListener {
            callback(gson.fromJson(it.value.toString(), Reservation::class.java)!!)
        }
    }

    fun addReservation(
        reservation: Reservation
    ) {
        val refs =
            getReference(
                "reservations",
            )
        refs.push().setValue(gson.toJson(reservation))
    }

    fun updateReservation(
        reservation: Reservation,
    ) {
        val refs =
            getReference(
                "reservations",
            )
        refs.child(reservation.id).setValue(gson.toJson(reservation))
    }

    fun deleteReservation(
        reservation: Reservation,
    ) {
        val refs =
            getReference(
                "reservations",
            )
        refs.child(reservation.id).removeValue()
    }

    fun checkCourtAvailability(
        courtId: String,
        date: LocalDate,
        time: LocalTime,
        duration: Int,
        callback: (Boolean) -> Unit
    ) {
        val refs =
            getReference(
                "reservations",
            )
        refs.get().addOnSuccessListener {
            var available = true
            for (data in it.children) {
                val res = gson.fromJson(data.value.toString(), Reservation::class.java)!!
                if (res.court.name == courtId) {
                    if (res.date.date == date) {
                        val start = res.time.time
                        val end = start.plusHours(res.duration.toLong())
                        val newEnd = time.plusMinutes(duration.toLong())
                        if (max(start.toSecondOfDay(), time.toSecondOfDay()) <
                            min(end.toSecondOfDay(), newEnd.toSecondOfDay())
                        ) {
                            available = false
                            break
                        }
                    }
                }
            }
            callback(available)
        }
    }

    fun searchByCourtName(
        name: String, results: MutableState<MutableList<Court>>
    ) {
        val gson = Gson()

        FirebaseDatabase.getInstance().getReference("courts").get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    results.value = dataSnapshot.children.map {
                        gson.fromJson(
                            it.value.toString(), Court::class.java
                        )
                    }.filter { it.name.contains(name, ignoreCase = true) }.toMutableList()
                }
            }
    }

    fun getCourts(
        callback: (List<Court>) -> Unit,
    ) {
        val refs =
            getReference(
                "courts",
            )
        refs.get().addOnSuccessListener {
            val courts = mutableListOf<Court>()
            for (data in it.children) {
                val court = gson.fromJson(data.value.toString(), Court::class.java)!!
                courts.add(court)
            }
            callback(courts)
        }
    }

    fun getCourtByName(name: String, callback: (Court) -> Unit) {
        val refs =
            getReference(
                "courts",
            )
        refs.child(name).get().addOnSuccessListener {
            val court = gson.fromJson(it.value.toString(), Court::class.java)!!
            callback(court)
        }
    }

    fun addCourt(
        court: Court,
    ) {
        val refs =
            getReference(
                "courts",
            )
        refs.push().setValue(gson.toJson(court))
    }


    fun updateCourt(
        court: Court,
    ) {
        val refs =
            getReference(
                "courts",
            )
        refs.child(court.name).setValue(gson.toJson(court))
    }

    fun getRating(
        court: Court,
        callback: (Float) -> Unit,
    ) {
        val refs =
            getReference(
                "ratings",
            )
        refs.child(court.name).get().addOnSuccessListener {
            var sum = 0f
            var count = 0
            for (data in it.children) {
                sum += data.value.toString().toFloat()
                count++
            }
            callback(sum / count)
        }
    }

    fun addOrUpdateRating(
        court: Court,
        rating: Int,
    ) {
        val refs =
            getReference(
                "ratings",
            )
        refs.child(court.name).setValue(rating)
    }

    private fun emailToKey(email: String): String {
        return email.replace(".", "_")
    }
}
