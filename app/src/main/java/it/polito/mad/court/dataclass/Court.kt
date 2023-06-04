package it.polito.mad.court.dataclass

import java.time.LocalTime

data class Court(
    var rating: Float = 0F,
    var name: String = "",
    var address: String = "",
    var city: String = "",
    var country: String = "",
    var phone: String = "",
    var email: String = "",
    var website: String = "",
    var openingTime: LocalTime = LocalTime.of(0, 0),
    var closingTime: LocalTime = LocalTime.of(23, 59),
    var price: Double = 0.0,
    var image: String = "",
    var sport: String = "",
    var description: String = "",
    var isOutdoor: Boolean = false,
    var comment: String = "",
)
