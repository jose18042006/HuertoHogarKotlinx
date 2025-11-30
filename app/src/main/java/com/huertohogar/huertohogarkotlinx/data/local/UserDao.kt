package com.huertohogar.huertohogarkotlinx.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = 1")
    fun getUserById(): Flow<UserEntity?>

    @Query("DELETE FROM users WHERE id = 1")
    suspend fun deleteUser()
}