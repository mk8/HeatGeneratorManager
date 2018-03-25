package com.torosoft.heater.heatermanager.Entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "measure")
data class Measure(
    @PrimaryKey(autoGenerate = true) var id:Long = 0,
    var storedAt: Date = Date(),
    var measureType: String = "",
    var value: Double = 0.0,
    var sourcePath: String?,
    var sourceMessage: String?
)