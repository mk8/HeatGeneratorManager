package com.torosoft.heater.heatermanager

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.torosoft.heater.heatermanager.Dao.MeasureDao
import com.torosoft.heater.heatermanager.Entities.Measure
import com.torosoft.heater.heatermanager.Converters.TimestampTypeConverter

@Database(entities = arrayOf(Measure::class), version = 1)
@TypeConverters(TimestampTypeConverter::class)
abstract class MeasureDatabase : RoomDatabase() {
    abstract fun measureDao(): MeasureDao
}
