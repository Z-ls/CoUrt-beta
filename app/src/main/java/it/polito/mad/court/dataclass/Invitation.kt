package it.polito.mad.court.dataclass

data class Invitation(
    var id: String? = null,
    var reservation: Reservation? = null,
    var sender: User? = null,
    var receiver: User? = null,
    var dateSent: DateString? = null,
    var timeSent: TimeString? = null,
    var hourExpiration: Int = 1,
    var status: InvitationStatus = InvitationStatus.PENDING
)

enum class InvitationStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    CANCELED;

    override fun toString(): String {
        return when (this) {
            PENDING -> "Pending"
            ACCEPTED -> "Accepted"
            DECLINED -> "Declined"
            CANCELED -> "Canceled"
        }
    }
}
