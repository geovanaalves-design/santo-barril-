package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.RestaurantTableEntity
import com.example.data.model.TableStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TableDao {

    @Query("SELECT * FROM restaurant_tables ORDER BY number ASC")
    fun getAllTables(): Flow<List<RestaurantTableEntity>>

    @Query("SELECT * FROM restaurant_tables WHERE number = :tableNumber")
    suspend fun getTableByNumber(tableNumber: Int): RestaurantTableEntity?

    @Query("SELECT * FROM restaurant_tables WHERE number = :tableNumber")
    fun getTableByNumberFlow(tableNumber: Int): Flow<RestaurantTableEntity?>

    @Query("SELECT * FROM restaurant_tables WHERE status = :status ORDER BY number ASC")
    fun getTablesByStatus(status: TableStatus): Flow<List<RestaurantTableEntity>>

    @Query("SELECT * FROM restaurant_tables WHERE section = :section ORDER BY number ASC")
    fun getTablesBySection(section: String): Flow<List<RestaurantTableEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTable(table: RestaurantTableEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTables(tables: List<RestaurantTableEntity>)

    @Update
    suspend fun updateTable(table: RestaurantTableEntity)

    @Delete
    suspend fun deleteTable(table: RestaurantTableEntity)

    @Query("DELETE FROM restaurant_tables WHERE number = :tableNumber")
    suspend fun deleteTableByNumber(tableNumber: Int)
}
