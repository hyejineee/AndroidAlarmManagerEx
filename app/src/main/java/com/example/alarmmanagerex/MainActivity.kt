package com.example.alarmmanagerex

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alarmmanagerex.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        renderView(getAlarmFromSharedPreference())

        handleOnOffButton()
        handleChangeTimeButton()
    }

    private fun renderView(model: AlarmModel) {
        binding.timeTextView.text = model.timeText
        binding.ampmTextView.text = model.amPmText
        binding.onOffButton.apply {
            text = model.onOffText
            tag = model
        }
    }

    private fun getAlarmFromSharedPreference(): AlarmModel {
        val preferences = getSharedPreferences(ALARM_DB_NAME, MODE_PRIVATE)

        val isOffValue = preferences.getBoolean(ON_OFF_KEY, false)
        val alarmValue = preferences.getString(ALARM_KEY, "09:30") ?: "09:30"
        val time = alarmValue.split(":").map { it.toInt() }

        val alarmModel = AlarmModel(hour = time[0], minute = time[1], isOffValue)

        val i = PendingIntent.getBroadcast(
            this,
            ALARM_REQUEST_CODE,
            Intent(this, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE
        )

        if ((i != null) and alarmModel.isOff) {
            i?.cancel()
        } else if ((i == null) and !isOffValue) {
            alarmModel.isOff = true
        }


        return alarmModel
    }

    private fun handleOnOffButton() {
        binding.onOffButton.setOnClickListener {

            val model = binding.onOffButton.tag as? AlarmModel ?: return@setOnClickListener
            val newModel = saveAlarmModel(model.hour, model.minute, model.isOff.not())

            renderView(newModel)

            if (model.isOff) cancelAlarm()  else createAlarm(newModel)
        }
    }

    private fun handleChangeTimeButton() {
        binding.changeTimeButton.setOnClickListener {

            val calendar = Calendar.getInstance()

            TimePickerDialog(
                this, { picker, h, m ->
                    val m = saveAlarmModel(h, m, false)
                    renderView(model = m)
                    cancelAlarm()
                    createAlarm(model = m)

                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            ).show()

        }
    }

    private fun createAlarm(model: AlarmModel) {

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, model.hour)
            set(Calendar.MINUTE, model.minute)
            if(before(Calendar.getInstance())){
                add(Calendar.DATE, 1)
            }
        }
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            ALARM_REQUEST_CODE,
            Intent(this, AlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

    }

    private fun cancelAlarm() {
        val i = PendingIntent.getBroadcast(
            this,
            ALARM_REQUEST_CODE,
            Intent(this, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE
        )
        i?.cancel()
    }


    private fun saveAlarmModel(hour: Int, minute: Int, isOff: Boolean): AlarmModel {
        val model = AlarmModel(hour, minute, isOff)

        val p = getSharedPreferences(ALARM_DB_NAME, MODE_PRIVATE)
        with(p.edit()) {
            putBoolean(ON_OFF_KEY, isOff)
            putString(ALARM_KEY, model.makeDataForDB())
            commit()
        }

        return model
    }

    companion object {
        private const val ALARM_REQUEST_CODE = 1000
        private const val ALARM_DB_NAME = "TIME"
        private const val ALARM_KEY = "ALARM"
        private const val ON_OFF_KEY = "ON_OFF"
    }


}