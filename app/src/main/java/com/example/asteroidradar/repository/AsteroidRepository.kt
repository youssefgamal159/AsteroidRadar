@file:Suppress("BlockingMethodInNonBlockingContext")

package com.example.asteroidradar.repository

import com.example.asteroidradar.api.*
import com.example.asteroidradar.database.AsteroidDatabase
import com.example.asteroidradar.models.Asteroid
import com.example.asteroidradar.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import java.util.*

class AsteroidRepository(private val database: AsteroidDatabase) {

    suspend fun refreshAsteroids(
        startDate: String = getToday(),
        endDate: String = getSeventhDay()
    ) {
        var asteroidList: ArrayList<Asteroid>
        withContext(Dispatchers.IO) {
            val asteroidResponseBody: ResponseBody = Network.service.getAsteroidsAsync(
                startDate, endDate,
                Constants.API_KEY
            )
                .await()
            asteroidList = parseAsteroidsJsonResult(JSONObject(asteroidResponseBody.string()))
            database.asteroidDao.insertAll(*asteroidList.asDomainModel())
        }
    }

    suspend fun deletePreviousDayAsteroids() {
        withContext(Dispatchers.IO) {
            database.asteroidDao.deletePreviousDayAsteroids(getToday())
        }
    }
}