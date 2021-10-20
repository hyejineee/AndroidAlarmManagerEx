package com.example.alarmmanagerex

data class AlarmModel(
    val hour: Int,
    val minute: Int,
    var isOff: Boolean = false
) {

    val timeText: String
    get() {
        val h = "%02d".format(if (hour > 12) hour - 12 else hour)
        val m = "%02d".format(minute)

        return "$h:$m"
    }

    val amPmText:String
    get() {
        return if(hour > 12) "PM" else "Am"
    }

    val onOffText:String
    get() {
        return if(isOff) "OFF" else "ON"
    }

    fun makeDataForDB() = "$hour:$minute"


}
