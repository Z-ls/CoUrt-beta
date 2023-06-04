package it.polito.mad.court

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
import it.polito.mad.court.dataclass.Invitation
import it.polito.mad.court.dataclass.InvitationStatus
import it.polito.mad.court.dataclass.Reservation
import it.polito.mad.court.dataclass.User
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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

    private fun getReservationById(
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

    private fun getReservationsByCourtAndDate(
        court: Court,
        date: LocalDate,
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
                if ((res.court.name == court.name) && (res.date.date == date)) {
                    reservations.add(res)
                }
            }
            callback(reservations)
        }
    }

    fun addReservation(
        user: User,
        reservation: Reservation
    ) {
        val refs =
            getReference(
                "reservations",
            )
        reservation.players.add(user)
        refs.push().setValue(gson.toJson(reservation))
    }

    private fun participateReservation(
        user: User,
        reservation: Reservation
    ): Boolean {
        val refs =
            getReference(
                "reservations",
            )
        return if (reservation.players.size < reservation.maxPlayers) {
            reservation.players.add(user)
            reservation.numPlayers = reservation.players.size
            refs.child(reservation.id).setValue(gson.toJson(reservation))
            true
        } else {
            false
        }
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
        getInvitations {
            for (item in it) {
                if (item.reservation?.id == reservation.id) {
                    deleteInvitation(item)
                }
            }
        }
    }

    fun checkCourtAvailability(
        id: String,
        court: Court,
        date: LocalDate,
        time: LocalTime,
        duration: Int,
        callback: (Boolean) -> Unit
    ) {
        var available = true
        if (time.isBefore(court.openingTime) || time.isAfter(court.closingTime)) {
            callback(false)
            return
        }
        getReservationsByCourtAndDate(court, date) {
            for (itemRes in it) {
                if (itemRes.id != id) {
                    val start = itemRes.time.time
                    val end = start.plusHours(itemRes.duration.toLong())
                    val newEnd = time.plusMinutes(duration.toLong())
                    if ((time.isAfter(start) && time.isBefore(end)) ||
                        (newEnd.isAfter(start) && newEnd.isBefore(end)) ||
                        (time.isBefore(start) && newEnd.isAfter(end))
                    ) {
                        available = false
                        break
                    }
                }
            }
            callback(available)
        }
    }

    private fun getCommentByCourtNameAndUser(
        court: Court,
        user: User,
        callback: (String) -> Unit
    ) {
        val refs =
            getReference(
                "comments",
            )
        refs.child(court.name).child(emailToKey(user.email)).get().addOnSuccessListener {
            callback(it.value.toString())
        }
    }

    fun addOrUpdateComment(
        user: User,
        reservation: Reservation,
        comment: String,
    ) {
        val refs =
            getReference(
                "comments",
            )
        refs.child(reservation.court.name).child(emailToKey(user.email))
            .setValue(comment)
    }

    fun searchCourtByName(
        name: String,
        callback: (List<Court>) -> Unit
    ) {
        val refs =
            getReference(
                "courts",
            )
        refs.get().addOnSuccessListener { dataSnapshot ->
            var results = listOf<Court>()
            if (dataSnapshot.exists()) {
                results = (dataSnapshot.children.map {
                    gson.fromJson(
                        it.value.toString(), Court::class.java
                    )
                }.filter { it.name.contains(name, ignoreCase = true) }.toList())
            }
            callback(results)
        }
    }

    fun searchUserByEmail(
        email: String,
        callback: (List<User>) -> Unit
    ) {
        val refs =
            getReference(
                "users",
            )
        refs.get().addOnSuccessListener { dataSnapshot ->
            var results = listOf<User>()
            if (dataSnapshot.exists()) {
                results = (dataSnapshot.children.map {
                    gson.fromJson(
                        it.value.toString(), User::class.java
                    )
                }.filter { it.email.contains(email, ignoreCase = true) }.toList())
            }
            callback(results)
        }
    }

    fun getCourts(
        user: User,
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
                getRating(court) { rating ->
                    court.rating = rating
                }
                getCommentByCourtNameAndUser(court, user) { comment ->
                    if (comment != "") {
                        court.comment = comment
                    } else {
                        court.comment = ""
                    }
                }
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


    private fun updateCourt(
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
            if (!it.exists()) {
                callback(0f)
                return@addOnSuccessListener
            }
            var sum = 0f
            var count = 0
            for (data in it.children) {
                sum += data.value.toString().toFloat()
                count++
            }
            callback(sum / count)
        }
    }

    fun getRatingByUser(
        user: User,
        court: Court,
        callback: (Int) -> Unit,
    ) {
        val refs =
            getReference(
                "ratings",
            )
        refs.child(court.name).child(emailToKey(user.email)).get().addOnSuccessListener {
            if (!it.exists()) {
                callback(0)
                return@addOnSuccessListener
            }
            callback(it.value.toString().toInt())
        }
    }

    fun addOrUpdateRating(
        user: User,
        court: Court,
        rating: Int,
    ) {
        val refs =
            getReference(
                "ratings",
            )
        refs.child(court.name).child(emailToKey(user.email)).setValue(rating.toString())
        val refsCourt = getReference("courts")
        refsCourt.child(court.name).get().addOnSuccessListener {
            val c = gson.fromJson(it.value.toString(), Court::class.java)!!
            getRating(c) { rating ->
                c.rating = rating
                updateCourt(c)
            }
        }
    }

    private fun getInvitations(
        callback: (List<Invitation>) -> Unit
    ) {
        val refs =
            getReference(
                "invitations",
            )
        refs.get().addOnSuccessListener {
            if (!it.exists()) {
                return@addOnSuccessListener
            }
            val invitations = mutableListOf<Invitation>()
            it.children.iterator().forEach { data ->
                val invitation = gson.fromJson(data.value.toString(), Invitation::class.java)!!
                invitation.id = data.key
                invitations.add(invitation)
            }
            callback(invitations)
        }
    }

    fun getInvitationsByRole(
        email: String,
        bySender: Boolean,
        callback: (List<Invitation>) -> Unit
    ) {
        val refs =
            getReference(
                "invitations",
            )
        val dataSnapshot = when (bySender) {
            true -> refs.get()
            false -> refs.child(emailToKey(email)).get()
        }
        dataSnapshot.addOnSuccessListener {
            if (!it.exists()) {
                return@addOnSuccessListener
            }
            val invitations = mutableListOf<Invitation>()
            it.children.iterator().forEach { data ->
                val invitation = gson.fromJson(data.value.toString(), Invitation::class.java)!!
                val isFetchingBySender = !(invitation.sender?.email !== email && bySender)
                if (isFetchingBySender) {
                    invitation.id = data.key
                    if (invitation.reservation?.players?.contains(invitation.receiver) == true) {
                        invitation.status = InvitationStatus.ACCEPTED
                    }
                    invitations.add(invitation)
                }
            }
            callback(invitations)
        }
    }

    fun addInvitation(
        invitation: Invitation,
        callback: (List<Invitation>) -> Unit
    ) {
        val refs =
            getReference(
                "invitations",
            )
        refs.child(emailToKey(invitation.receiver?.email!!)).push()
            .setValue(gson.toJson(invitation)).addOnSuccessListener {
                getInvitationsByRole(invitation.receiver!!.email, bySender = true) { invitations ->
                    callback(invitations)
                }
            }
    }

    fun acceptInvitation(
        user: User,
        invitation: Invitation,
    ): Pair<Boolean, String> {
        val refs =
            getReference(
                "invitations",
            )
        if (participateReservation(user, invitation.reservation!!)) {
            refs.child(emailToKey(user.email)).child(invitation.id!!)
                .setValue(gson.toJson(invitation))
        } else return Pair(false, "The invitation is invalid or full")
        invitation.status = InvitationStatus.ACCEPTED
        refs.child(emailToKey(user.email)).child(invitation.id!!).setValue(gson.toJson(invitation))
        return true to ""
    }

    fun declineInvitation(
        user: User,
        invitation: Invitation,
    ) {
        val refs =
            getReference(
                "invitations",
            )
        invitation.status = InvitationStatus.DECLINED
        refs.child(emailToKey(user.email)).child(invitation.id!!).setValue(gson.toJson(invitation))
    }

    private fun deleteInvitation(
        invitation: Invitation,
    ) {
        val refs =
            getReference(
                "invitations",
            )
        refs.child(emailToKey(invitation.receiver?.email!!)).child(invitation.id!!).removeValue()
    }

    private fun emailToKey(email: String): String {
        return email.replace(".", "_")
    }
}
