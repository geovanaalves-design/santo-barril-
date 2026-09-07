package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    ADMINISTRADOR,
    GERENTE,
    GARCOM,
    COZINHA_BAR
}

enum class TableStatus {
    LIVRE,
    OCUPADA,
    PEDIDO_ENVIADO,
    EM_PREPARO,
    AGUARDANDO_PAGAMENTO,
    FINALIZADA
}

enum class ProductionSector {
    CHURRASQUEIRA,
    COZINHA,
    BAR
}

enum class OrderStatus {
    NOVO,
    EM_PREPARO,
    PRONTO,
    ENTREGUE,
    FINALIZADO,
    CANCELADO
}

enum class PrintStatus {
    AGUARDANDO,
    IMPRIMINDO,
    IMPRESSO,
    ERRO
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val name: String,
    val role: UserRole,
    val pin: String = "1234"
)

@Entity(tableName = "restaurant_tables")
data class RestaurantTableEntity(
    @PrimaryKey val number: Int,
    val name: String = "Mesa $number",
    val section: String = "Salão Principal", // "Salão Principal", "Varanda", "Deck"
    val status: TableStatus = TableStatus.LIVRE,
    val waiterName: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val openedAtMillis: Long = 0L,
    val mergedWithTableNumber: Int? = null,
    val notes: String = ""
)

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val originChannel: String = "Não informado", // Indicação de amigo, Instagram, etc.
    val visitCount: Int = 1,
    val totalSpent: Double = 0.0,
    val firstVisitMillis: Long = System.currentTimeMillis(),
    val lastVisitMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val category: String, // Espetinhos, Porções, Comidas, Hambúrgueres, Chopp, Cervejas, Drinks, etc.
    val description: String,
    val price: Double,
    val costPrice: Double,
    val unit: String = "un",
    val isAvailable: Boolean = true,
    val minStock: Double = 5.0,
    val currentStock: Double = 50.0,
    val productionSector: ProductionSector = ProductionSector.COZINHA,
    val hasRecipe: Boolean = false,
    val imageUri: String = ""
)

@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val unit: String, // g, ml, un, kg
    val currentStock: Double,
    val minStock: Double,
    val costPerUnit: Double
)

@Entity(tableName = "recipe_items")
data class RecipeItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val ingredientId: Long,
    val quantityNeeded: Double
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: Int,
    val tableNumber: Int,
    val customerName: String,
    val customerPhone: String = "",
    val waiterName: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val status: OrderStatus = OrderStatus.NOVO,
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val serviceFee: Double = 0.0,
    val totalAmount: Double = 0.0,
    val isPaid: Boolean = false,
    val paymentMethod: String = ""
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val productId: Long,
    val productName: String,
    val unitPrice: Double,
    val quantity: Int,
    val notes: String = "",
    val productionSector: ProductionSector,
    val status: OrderStatus = OrderStatus.NOVO,
    val waiterName: String = ""
)

@Entity(tableName = "cashier_shifts")
data class CashierShiftEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val openedAtMillis: Long = System.currentTimeMillis(),
    val closedAtMillis: Long? = null,
    val openingBalance: Double = 200.0,
    val closingCashCounted: Double? = null,
    val expectedCash: Double = 0.0,
    val totalCash: Double = 0.0,
    val totalPix: Double = 0.0,
    val totalDebit: Double = 0.0,
    val totalCredit: Double = 0.0,
    val totalSangria: Double = 0.0,
    val totalSuprimento: Double = 0.0,
    val isOpen: Boolean = true,
    val operatorName: String = "Administrador"
)

@Entity(tableName = "cashier_movements")
data class CashierMovementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val shiftId: Long,
    val type: String, // "SANGRIA", "SUPRIMENTO", "ENTRADA"
    val amount: Double,
    val reason: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val operatorName: String = "Administrador"
)

@Entity(tableName = "financial_accounts")
data class FinancialAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String,
    val type: String, // "PAGAR" ou "RECEBER"
    val category: String, // "Fornecedores", "Aluguel", "Energia", "Água", "Salários", "Vendas PDV"
    val amount: Double,
    val dueDateMillis: Long,
    val isPaid: Boolean = false,
    val paidDateMillis: Long? = null
)

@Entity(tableName = "suppliers")
data class SupplierEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val cnpj: String,
    val phone: String,
    val contactPerson: String,
    val productsSupplied: String,
    val paymentTerms: String = "15 dias",
    val deliveryDays: Int = 2
)

@Entity(tableName = "purchase_orders")
data class PurchaseOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val supplierName: String,
    val description: String,
    val totalAmount: Double,
    val status: String = "Aprovado", // "Necessidade", "Cotação", "Aprovado", "Recebido"
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val role: String, // "Garçom", "Churrasqueiro", "Cozinheiro", "Bartender", "Gerente"
    val phone: String,
    val commissionRate: Double = 10.0, // 10%
    val totalSales: Double = 0.0,
    val tablesServed: Int = 0,
    val ordersCount: Int = 0
)

@Entity(tableName = "reservations")
data class ReservationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerName: String,
    val phone: String,
    val dateStr: String,
    val timeStr: String,
    val guestsCount: Int,
    val tableNumber: Int,
    val notes: String = "",
    val status: String = "Confirmada" // "Reservada", "Confirmada", "Chegou", "Finalizada", "Cancelada"
)

@Entity(tableName = "print_jobs")
data class PrintJobEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: Int,
    val sector: ProductionSector,
    val tableNumber: Int,
    val waiterName: String,
    val itemsSummary: String,
    val status: PrintStatus = PrintStatus.IMPRESSO,
    val timestampMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class AppNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val type: String, // "PEDIDO_PRONTO", "ESTOQUE_BAIXO", "CHAMAR_GARCOM", "CAIXA", "RESERVA"
    val timestampMillis: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestampMillis: Long = System.currentTimeMillis(),
    val userName: String,
    val action: String,
    val details: String
)

@Entity(tableName = "imported_raw_data")
data class ImportedRawDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tag: String,
    val columnName: String,
    val value: String,
    val sourceFile: String,
    val importedAt: Long = System.currentTimeMillis(),
    val adminUsername: String = "Administrador"
)
