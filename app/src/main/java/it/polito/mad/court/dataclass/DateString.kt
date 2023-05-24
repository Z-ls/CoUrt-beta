package it.polito.mad.court.dataclass

class DateString {
    var date: java.time.LocalDate = java.time.LocalDate.now()

    constructor(date: java.time.LocalDate) {
        this.date = date
    }

    constructor(dateString: String) {
        this.date =
            java.time.LocalDate.parse(
                dateString,
                java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")
            )
    }

    override fun toString(): String {
        return date.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    }
}