package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
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
import com.example.data.model.OrderStatus
import com.example.data.model.PrintJobEntity
import com.example.data.model.ProductEntity
import com.example.data.model.PurchaseOrderEntity
import com.example.data.model.RecipeItemEntity
import com.example.data.model.ReservationEntity
import com.example.data.model.RestaurantTableEntity
import com.example.data.model.SupplierEntity
import com.example.data.model.TableStatus
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantDao {

    // USERS
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    // TABLES
    @Query("SELECT * FROM restaurant_tables ORDER BY number ASC")
    fun getAllTables(): Flow<List<RestaurantTableEntity>>

    @Query("SELECT * FROM restaurant_tables WHERE number = :tableNumber")
    suspend fun getTableByNumber(tableNumber: Int): RestaurantTableEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTables(tables: List<RestaurantTableEntity>)

    @Update
    suspend fun updateTable(table: RestaurantTableEntity)

    // CUSTOMERS
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%'")
    suspend fun searchCustomers(query: String): List<CustomerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity): Long

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)

    // PRODUCTS
    @Query("SELECT * FROM products ORDER BY category, name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Long): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    // INGREDIENTS & RECIPES
    @Query("SELECT * FROM ingredients ORDER BY name ASC")
    fun getAllIngredients(): Flow<List<IngredientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<IngredientEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: IngredientEntity): Long

    @Update
    suspend fun updateIngredient(ingredient: IngredientEntity)

    @Delete
    suspend fun deleteIngredient(ingredient: IngredientEntity)

    @Query("SELECT * FROM recipe_items WHERE productId = :productId")
    suspend fun getRecipeForProduct(productId: Long): List<RecipeItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeItems(items: List<RecipeItemEntity>)

    // ORDERS
    @Query("SELECT * FROM orders ORDER BY timestampMillis DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE tableNumber = :tableNumber AND isPaid = 0 ORDER BY timestampMillis DESC")
    fun getActiveOrdersForTable(tableNumber: Int): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE tableNumber = :tableNumber AND isPaid = 0 ORDER BY timestampMillis DESC")
    suspend fun getActiveOrdersForTableList(tableNumber: Int): List<OrderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    // ORDER ITEMS
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getItemsForOrder(orderId: Long): Flow<List<OrderItemEntity>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getItemsForOrderList(orderId: Long): List<OrderItemEntity>

    @Query("SELECT * FROM order_items ORDER BY id DESC")
    fun getAllOrderItems(): Flow<List<OrderItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(item: OrderItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Update
    suspend fun updateOrderItem(item: OrderItemEntity)

    // CASHIER
    @Query("SELECT * FROM cashier_shifts ORDER BY openedAtMillis DESC LIMIT 1")
    fun getCurrentShift(): Flow<CashierShiftEntity?>

    @Query("SELECT * FROM cashier_shifts ORDER BY openedAtMillis DESC LIMIT 1")
    suspend fun getCurrentShiftOnce(): CashierShiftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShift(shift: CashierShiftEntity): Long

    @Update
    suspend fun updateShift(shift: CashierShiftEntity)

    @Query("SELECT * FROM cashier_movements ORDER BY timestampMillis DESC")
    fun getAllMovements(): Flow<List<CashierMovementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovement(movement: CashierMovementEntity): Long

    // FINANCIAL
    @Query("SELECT * FROM financial_accounts ORDER BY dueDateMillis ASC")
    fun getAllFinancialAccounts(): Flow<List<FinancialAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinancialAccounts(accounts: List<FinancialAccountEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinancialAccount(account: FinancialAccountEntity): Long

    @Update
    suspend fun updateFinancialAccount(account: FinancialAccountEntity)

    @Delete
    suspend fun deleteFinancialAccount(account: FinancialAccountEntity)

    // SUPPLIERS & PURCHASES
    @Query("SELECT * FROM suppliers ORDER BY name ASC")
    fun getAllSuppliers(): Flow<List<SupplierEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuppliers(suppliers: List<SupplierEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: SupplierEntity): Long

    @Update
    suspend fun updateSupplier(supplier: SupplierEntity)

    @Delete
    suspend fun deleteSupplier(supplier: SupplierEntity)

    @Query("SELECT * FROM purchase_orders ORDER BY createdAtMillis DESC")
    fun getAllPurchaseOrders(): Flow<List<PurchaseOrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchaseOrder(order: PurchaseOrderEntity): Long

    @Update
    suspend fun updatePurchaseOrder(order: PurchaseOrderEntity)

    @Delete
    suspend fun deletePurchaseOrder(order: PurchaseOrderEntity)

    // EMPLOYEES
    @Query("SELECT * FROM employees ORDER BY totalSales DESC")
    fun getAllEmployees(): Flow<List<EmployeeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployees(employees: List<EmployeeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: EmployeeEntity): Long

    @Update
    suspend fun updateEmployee(employee: EmployeeEntity)

    @Delete
    suspend fun deleteEmployee(employee: EmployeeEntity)

    // RESERVATIONS
    @Query("SELECT * FROM reservations ORDER BY dateStr, timeStr ASC")
    fun getAllReservations(): Flow<List<ReservationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: ReservationEntity): Long

    @Update
    suspend fun updateReservation(reservation: ReservationEntity)

    // PRINT JOBS
    @Query("SELECT * FROM print_jobs ORDER BY timestampMillis DESC")
    fun getAllPrintJobs(): Flow<List<PrintJobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrintJob(job: PrintJobEntity): Long

    @Update
    suspend fun updatePrintJob(job: PrintJobEntity)

    // NOTIFICATIONS
    @Query("SELECT * FROM notifications ORDER BY timestampMillis DESC")
    fun getAllNotifications(): Flow<List<AppNotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotificationEntity): Long

    @Update
    suspend fun updateNotification(notification: AppNotificationEntity)

    // AUDIT LOGS
    @Query("SELECT * FROM audit_logs ORDER BY timestampMillis DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity): Long

    // IMPORTED RAW DATA & BULK INSERTS
    @Query("SELECT * FROM imported_raw_data ORDER BY importedAt DESC")
    fun getAllImportedRawData(): Flow<List<ImportedRawDataEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImportedRawData(items: List<ImportedRawDataEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomers(customers: List<CustomerEntity>)
}
