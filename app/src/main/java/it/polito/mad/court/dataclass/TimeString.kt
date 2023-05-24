package it.polito.mad.court.dataclass

import java.time.LocalTime

class TimeString {
    var time: LocalTime = LocalTime.now()

    constructor(time: LocalTime) {
        this.time = time
    }

    constructor(timeString: String) {
        this.time =
            LocalTime.parse(timeString, java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
    }

    override fun toString(): String {
        return time.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
    }
}