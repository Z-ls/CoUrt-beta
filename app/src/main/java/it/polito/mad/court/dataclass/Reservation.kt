package it.polito.mad.court.dataclass

import java.time.LocalDate
import java.time.LocalTime

data class Reservation(
    var id: String = "",
    var court: Court = Court(),
    var user: User = User(),
    var time: TimeString = TimeString(LocalTime.now()),
    var date: DateString = DateString(LocalDate.now()),
    var duration: Int = 0,
    var price: Int = 0,
    var status: Status = Status.ACTIVE,
    var minPlayers: Int = 1,
    var maxPlayers: Int = 2,
    var numPlayers: Int = 1,
    var skillLevel: Int = 0,
)



enum class Status {
    ACTIVE,
    COMPLETED,
    CANCELED;

    override fun toString(): String {
        return when (this) {
            ACTIVE -> "Active"
            COMPLETED -> "Completed"
            CANCELED -> "Canceled"
        }
    }
}

