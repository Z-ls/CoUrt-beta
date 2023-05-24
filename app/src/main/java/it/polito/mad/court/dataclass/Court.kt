package it.polito.mad.court.dataclass

data class Court(
    var rating: Double = 0.0,
    var name: String = "",
    var address: String = "",
    var city: String = "",
    var country: String = "",
    var phone: String = "",
    var email: String = "",
    var website: String = "",
    var openingTime: String = "",
    var closingTime: String = "",
    var price: Double = 0.0,
    var image: String = "",
    var sport: String = "",
    var description: String = "",
    var isOutdoor: Boolean = false,
)
