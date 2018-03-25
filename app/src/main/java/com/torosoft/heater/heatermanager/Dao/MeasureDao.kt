package com.torosoft.heater.heatermanager.Dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.torosoft.heater.heatermanager.Entities.Measure
import io.reactivex.Flowable

@Dao
interface MeasureDao {

    @Query("SELECT * FROM measure")
    fun getAllMeasure(): Flowable<List<Measure>>

    @Insert
    fun insert(measure: Measure)
}