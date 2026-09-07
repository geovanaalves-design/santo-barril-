package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.OrderDao
import com.example.data.dao.ProductDao
import com.example.data.dao.RestaurantDao
import com.example.data.dao.TableDao
import com.example.data.model.AppNotificationEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.CashierMovementEntity
import com.example.data.model.CashierShiftEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.EmployeeEntity
import com.example.data.model.FinancialAccountEntity
import com.example.data.model.ImportedRawDataEntity
import com.example.data.model.IngredientEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.PrintJobEntity
import com.example.data.model.ProductEntity
import com.example.data.model.PurchaseOrderEntity
import com.example.data.model.RecipeItemEntity
import com.example.data.model.ReservationEntity
import com.example.data.model.RestaurantTableEntity
import com.example.data.model.SupplierEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        RestaurantTableEntity::class,
        CustomerEntity::class,
        ProductEntity::class,
        IngredientEntity::class,
        RecipeItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        CashierShiftEntity::class,
        CashierMovementEntity::class,
        FinancialAccountEntity::class,
        SupplierEntity::class,
        PurchaseOrderEntity::class,
        EmployeeEntity::class,
        ReservationEntity::class,
        PrintJobEntity::class,
        AppNotificationEntity::class,
        AuditLogEntity::class,
        ImportedRawDataEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun restaurantDao(): RestaurantDao
    abstract fun tableDao(): TableDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "santo_barril_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        DatabaseSeeder.seedDatabase(database.restaurantDao())
                    }
                }
            }
        }
    }
}
