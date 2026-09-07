package com.example.data.repository

import com.example.data.dao.RestaurantDao
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
import com.example.data.model.PrintStatus
import com.example.data.model.ProductEntity
import com.example.data.model.ProductionSector
import com.example.data.model.PurchaseOrderEntity
import com.example.data.model.ReservationEntity
import com.example.data.model.RestaurantTableEntity
import com.example.data.model.SupplierEntity
import com.example.data.model.TableStatus
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.math.max

class RestaurantRepository(private val dao: RestaurantDao) {

    // USERS & SESSIONS
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()

    suspend fun insertUser(user: UserEntity): Long = dao.insertUser(user)
    suspend fun updateUser(user: UserEntity) = dao.updateUser(user)
    suspend fun deleteUser(user: UserEntity) = dao.deleteUser(user)

    // TABLES
    val allTables: Flow<List<RestaurantTableEntity>> = dao.getAllTables()

    // CUSTOMERS
    val allCustomers: Flow<List<CustomerEntity>> = dao.getAllCustomers()

    // PRODUCTS & INGREDIENTS
    val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()
    val allIngredients: Flow<List<IngredientEntity>> = dao.getAllIngredients()

    // ORDERS
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()
    val allOrderItems: Flow<List<OrderItemEntity>> = dao.getAllOrderItems()

    // CASHIER
    val currentShift: Flow<CashierShiftEntity?> = dao.getCurrentShift()
    val cashierMovements: Flow<List<CashierMovementEntity>> = dao.getAllMovements()

    // FINANCIAL & PURCHASES
    val financialAccounts: Flow<List<FinancialAccountEntity>> = dao.getAllFinancialAccounts()
    val suppliers: Flow<List<SupplierEntity>> = dao.getAllSuppliers()
    val purchaseOrders: Flow<List<PurchaseOrderEntity>> = dao.getAllPurchaseOrders()

    // EMPLOYEES & RESERVATIONS
    val employees: Flow<List<EmployeeEntity>> = dao.getAllEmployees()
    val reservations: Flow<List<ReservationEntity>> = dao.getAllReservations()

    // PRINTING, NOTIFICATIONS, AUDIT
    val printJobs: Flow<List<PrintJobEntity>> = dao.getAllPrintJobs()
    val notifications: Flow<List<AppNotificationEntity>> = dao.getAllNotifications()
    val auditLogs: Flow<List<AuditLogEntity>> = dao.getAllAuditLogs()

    suspend fun getActiveOrdersForTable(tableNumber: Int): List<OrderEntity> {
        return dao.getActiveOrdersForTableList(tableNumber)
    }

    suspend fun getItemsForOrder(orderId: Long): List<OrderItemEntity> {
        return dao.getItemsForOrderList(orderId)
    }

    suspend fun searchCustomers(query: String): List<CustomerEntity> {
        return dao.searchCustomers(query)
    }

    // TABLE ACTIONS
    suspend fun openTable(
        tableNumber: Int,
        customerName: String,
        customerPhone: String,
        isNewCustomer: Boolean,
        originChannel: String,
        waiterName: String
    ) {
        val existingCustomer = if (customerPhone.isNotBlank()) {
            dao.searchCustomers(customerPhone).firstOrNull()
        } else null

        if (existingCustomer != null) {
            dao.updateCustomer(
                existingCustomer.copy(
                    visitCount = existingCustomer.visitCount + 1,
                    lastVisitMillis = System.currentTimeMillis()
                )
            )
        } else {
            dao.insertCustomer(
                CustomerEntity(
                    name = customerName,
                    phone = customerPhone,
                    originChannel = if (isNewCustomer) originChannel else "Já conhecia",
                    visitCount = 1,
                    totalSpent = 0.0
                )
            )
        }

        val table = dao.getTableByNumber(tableNumber) ?: RestaurantTableEntity(number = tableNumber)
        val updatedTable = table.copy(
            status = TableStatus.OCUPADA,
            customerName = customerName,
            customerPhone = customerPhone,
            waiterName = waiterName,
            openedAtMillis = System.currentTimeMillis()
        )
        dao.updateTable(updatedTable)

        dao.insertAuditLog(
            AuditLogEntity(
                userName = waiterName,
                action = "Abertura de Mesa",
                details = "Mesa $tableNumber aberta para $customerName (Origem: $originChannel)"
            )
        )
    }

    suspend fun transferTable(fromTableNumber: Int, toTableNumber: Int, userName: String) {
        val fromTable = dao.getTableByNumber(fromTableNumber) ?: return
        val toTable = dao.getTableByNumber(toTableNumber) ?: return

        val activeOrders = dao.getActiveOrdersForTableList(fromTableNumber)
        for (order in activeOrders) {
            dao.updateOrder(order.copy(tableNumber = toTableNumber))
        }

        dao.updateTable(
            toTable.copy(
                status = fromTable.status,
                customerName = fromTable.customerName,
                customerPhone = fromTable.customerPhone,
                waiterName = fromTable.waiterName,
                openedAtMillis = fromTable.openedAtMillis
            )
        )

        dao.updateTable(
            fromTable.copy(
                status = TableStatus.LIVRE,
                customerName = "",
                customerPhone = "",
                waiterName = "",
                openedAtMillis = 0L,
                mergedWithTableNumber = null
            )
        )

        dao.insertAuditLog(
            AuditLogEntity(
                userName = userName,
                action = "Transferência de Mesa",
                details = "Transferida da Mesa $fromTableNumber para Mesa $toTableNumber"
            )
        )
    }

    suspend fun mergeTables(mainTableNumber: Int, secondaryTableNumber: Int, userName: String) {
        val secondary = dao.getTableByNumber(secondaryTableNumber) ?: return
        dao.updateTable(
            secondary.copy(
                mergedWithTableNumber = mainTableNumber,
                status = TableStatus.OCUPADA
            )
        )
        dao.insertAuditLog(
            AuditLogEntity(
                userName = userName,
                action = "Juntar Mesas",
                details = "Mesa $secondaryTableNumber unida à Mesa $mainTableNumber"
            )
        )
    }

    suspend fun cancelTableService(tableNumber: Int, reason: String, userName: String) {
        val table = dao.getTableByNumber(tableNumber) ?: return
        val activeOrders = dao.getActiveOrdersForTableList(tableNumber)
        for (order in activeOrders) {
            dao.updateOrder(order.copy(status = OrderStatus.CANCELADO))
        }

        dao.updateTable(
            table.copy(
                status = TableStatus.LIVRE,
                customerName = "",
                customerPhone = "",
                waiterName = "",
                openedAtMillis = 0L,
                mergedWithTableNumber = null,
                notes = ""
            )
        )

        dao.insertAuditLog(
            AuditLogEntity(
                userName = userName,
                action = "Cancelamento de Atendimento",
                details = "Mesa $tableNumber cancelada. Motivo: $reason"
            )
        )
    }

    // ORDER DISPATCH & INVENTORY DEDUCTION
    data class CartItem(
        val product: ProductEntity,
        val quantity: Int,
        val notes: String = ""
    )

    suspend fun sendOrder(
        tableNumber: Int,
        customerName: String,
        customerPhone: String,
        waiterName: String,
        items: List<CartItem>
    ): Long {
        if (items.isEmpty()) return 0L

        val allCurrentOrders = dao.getAllOrders().firstOrNull() ?: emptyList()
        val nextOrderNumber = (allCurrentOrders.maxOfOrNull { it.orderNumber } ?: 100) + 1

        var subtotal = 0.0
        for (item in items) {
            subtotal += item.product.price * item.quantity
        }
        val serviceFee = subtotal * 0.10 // 10%
        val total = subtotal + serviceFee

        val newOrder = OrderEntity(
            orderNumber = nextOrderNumber,
            tableNumber = tableNumber,
            customerName = customerName,
            customerPhone = customerPhone,
            waiterName = waiterName,
            timestampMillis = System.currentTimeMillis(),
            status = OrderStatus.EM_PREPARO,
            subtotal = subtotal,
            discount = 0.0,
            serviceFee = serviceFee,
            totalAmount = total,
            isPaid = false
        )
        val orderId = dao.insertOrder(newOrder)

        // Separate items by sector & deduct inventory according to Ficha Técnica
        val sectorItems = mutableMapOf<ProductionSector, MutableList<String>>()

        for (item in items) {
            val orderItem = OrderItemEntity(
                orderId = orderId,
                productId = item.product.id,
                productName = item.product.name,
                unitPrice = item.product.price,
                quantity = item.quantity,
                notes = item.notes,
                productionSector = item.product.productionSector,
                status = OrderStatus.EM_PREPARO,
                waiterName = waiterName
            )
            dao.insertOrderItem(orderItem)

            // Add to sector summary for printer
            val desc = "${item.quantity}x ${item.product.name}" + if (item.notes.isNotBlank()) " (${item.notes})" else ""
            sectorItems.getOrPut(item.product.productionSector) { mutableListOf() }.add(desc)

            // Deduct stock for product and ingredients
            val currentProduct = dao.getProductById(item.product.id)
            if (currentProduct != null) {
                val newStock = max(0.0, currentProduct.currentStock - item.quantity)
                dao.updateProduct(currentProduct.copy(currentStock = newStock))

                if (newStock <= currentProduct.minStock) {
                    dao.insertNotification(
                        AppNotificationEntity(
                            title = "Estoque Baixo: ${currentProduct.name}",
                            message = "Apenas $newStock unidades restantes no estoque.",
                            type = "ESTOQUE_BAIXO"
                        )
                    )
                }
            }

            // Recipe ingredients deduction
            val recipe = dao.getRecipeForProduct(item.product.id)
            val allIngredients = dao.getAllIngredients().firstOrNull() ?: emptyList()
            for (recipeItem in recipe) {
                val ingredient = allIngredients.find { it.id == recipeItem.ingredientId }
                if (ingredient != null) {
                    val deduction = recipeItem.quantityNeeded * item.quantity
                    val newIngStock = max(0.0, ingredient.currentStock - deduction)
                    dao.updateIngredient(ingredient.copy(currentStock = newIngStock))

                    if (newIngStock <= ingredient.minStock) {
                        dao.insertNotification(
                            AppNotificationEntity(
                                title = "Ingrediente Crítico: ${ingredient.name}",
                                message = "Estoque atingiu $newIngStock ${ingredient.unit} (Mín: ${ingredient.minStock} ${ingredient.unit})",
                                type = "ESTOQUE_BAIXO"
                            )
                        )
                    }
                }
            }
        }

        // Generate Thermal Print Jobs per sector
        for ((sector, itemList) in sectorItems) {
            val summary = itemList.joinToString("\n• ")
            dao.insertPrintJob(
                PrintJobEntity(
                    orderNumber = nextOrderNumber,
                    sector = sector,
                    tableNumber = tableNumber,
                    waiterName = waiterName,
                    itemsSummary = "• $summary",
                    status = PrintStatus.IMPRESSO
                )
            )
        }

        // Update table status
        val table = dao.getTableByNumber(tableNumber)
        if (table != null) {
            dao.updateTable(table.copy(status = TableStatus.EM_PREPARO))
        }

        // Update employee stats
        val employees = dao.getAllEmployees().firstOrNull() ?: emptyList()
        val employee = employees.find { it.name.contains(waiterName, ignoreCase = true) }
        if (employee != null) {
            dao.updateEmployee(
                employee.copy(
                    ordersCount = employee.ordersCount + 1,
                    totalSales = employee.totalSales + total
                )
            )
        }

        dao.insertAuditLog(
            AuditLogEntity(
                userName = waiterName,
                action = "Pedido Criado",
                details = "Pedido #$nextOrderNumber na Mesa $tableNumber com ${items.size} itens (R$ ${String.format("%.2f", total)})"
            )
        )

        return orderId
    }

    // UPDATE ORDER ITEM STATUS (KITCHEN/BAR)
    suspend fun updateItemStatus(itemId: Long, newStatus: OrderStatus, operatorName: String) {
        val allItems = dao.getAllOrderItems().firstOrNull() ?: emptyList()
        val item = allItems.find { it.id == itemId } ?: return
        dao.updateOrderItem(item.copy(status = newStatus))

        // Check if order is ready
        if (newStatus == OrderStatus.PRONTO) {
            dao.insertNotification(
                AppNotificationEntity(
                    title = "Item Pronto: ${item.productName}",
                    message = "${item.quantity}x ${item.productName} pronto no setor ${item.productionSector}",
                    type = "PEDIDO_PRONTO"
                )
            )
        }

        dao.insertAuditLog(
            AuditLogEntity(
                userName = operatorName,
                action = "Status de Produção",
                details = "${item.productName} atualizado para $newStatus"
            )
        )
    }

    suspend fun updateOrderStatus(orderId: Long, newStatus: OrderStatus, operatorName: String) {
        val allOrders = dao.getAllOrders().firstOrNull() ?: emptyList()
        val order = allOrders.find { it.id == orderId } ?: return
        dao.updateOrder(order.copy(status = newStatus))

        if (newStatus == OrderStatus.ENTREGUE) {
            dao.insertAuditLog(
                AuditLogEntity(
                    userName = operatorName,
                    action = "Pedido Entregue",
                    details = "Pedido #${order.orderNumber} na Mesa ${order.tableNumber} marcado como entregue"
                )
            )
        }
    }

    // CALL WAITER (CLIENT QR CODE MODE)
    suspend fun callWaiterFromTable(tableNumber: Int) {
        dao.insertNotification(
            AppNotificationEntity(
                title = "Chamado de Atendimento",
                message = "Mesa $tableNumber solicitou a presença do garçom.",
                type = "CHAMAR_GARCOM"
            )
        )
    }

    // CLOSE TABLE & PROCESS PAYMENT
    suspend fun finalizeTable(
        tableNumber: Int,
        subtotal: Double,
        discount: Double,
        includeServiceFee: Boolean,
        serviceFee: Double,
        finalTotal: Double,
        paymentMethod: String,
        operatorName: String
    ) {
        val activeOrders = dao.getActiveOrdersForTableList(tableNumber)
        for (order in activeOrders) {
            dao.updateOrder(
                order.copy(
                    status = OrderStatus.FINALIZADO,
                    isPaid = true,
                    discount = discount,
                    serviceFee = if (includeServiceFee) serviceFee else 0.0,
                    totalAmount = finalTotal,
                    paymentMethod = paymentMethod
                )
            )
        }

        // Update Cashier Shift
        val currentShift = dao.getCurrentShiftOnce()
        if (currentShift != null && currentShift.isOpen) {
            val updatedShift = when (paymentMethod.uppercase()) {
                "DINHEIRO" -> currentShift.copy(
                    totalCash = currentShift.totalCash + finalTotal,
                    expectedCash = currentShift.expectedCash + finalTotal
                )
                "PIX" -> currentShift.copy(totalPix = currentShift.totalPix + finalTotal)
                "DÉBITO", "DEBITO" -> currentShift.copy(totalDebit = currentShift.totalDebit + finalTotal)
                "CRÉDITO", "CREDITO" -> currentShift.copy(totalCredit = currentShift.totalCredit + finalTotal)
                else -> currentShift.copy(totalPix = currentShift.totalPix + finalTotal)
            }
            dao.updateShift(updatedShift)
        }

        // Update Customer Stats
        val table = dao.getTableByNumber(tableNumber)
        if (table != null && table.customerPhone.isNotBlank()) {
            val customer = dao.searchCustomers(table.customerPhone).firstOrNull()
            if (customer != null) {
                dao.updateCustomer(
                    customer.copy(
                        totalSpent = customer.totalSpent + finalTotal,
                        lastVisitMillis = System.currentTimeMillis()
                    )
                )
            }
        }

        // Free Table
        if (table != null) {
            dao.updateTable(
                table.copy(
                    status = TableStatus.LIVRE,
                    customerName = "",
                    customerPhone = "",
                    waiterName = "",
                    openedAtMillis = 0L,
                    mergedWithTableNumber = null,
                    notes = ""
                )
            )
        }

        // Add Financial Account (Receita Realizada)
        dao.insertFinancialAccount(
            FinancialAccountEntity(
                description = "Fechamento Mesa $tableNumber ($paymentMethod)",
                type = "RECEBER",
                category = "Vendas PDV",
                amount = finalTotal,
                dueDateMillis = System.currentTimeMillis(),
                isPaid = true,
                paidDateMillis = System.currentTimeMillis()
            )
        )

        // Print Receipt Job
        dao.insertPrintJob(
            PrintJobEntity(
                orderNumber = activeOrders.firstOrNull()?.orderNumber ?: 999,
                sector = ProductionSector.COZINHA,
                tableNumber = tableNumber,
                waiterName = operatorName,
                itemsSummary = "COMPROVANTE FECHAMENTO MESA $tableNumber\nTotal: R$ ${String.format("%.2f", finalTotal)} ($paymentMethod)",
                status = PrintStatus.IMPRESSO
            )
        )

        dao.insertAuditLog(
            AuditLogEntity(
                userName = operatorName,
                action = "Fechamento de Mesa",
                details = "Mesa $tableNumber finalizada. Valor: R$ ${String.format("%.2f", finalTotal)} em $paymentMethod"
            )
        )
    }

    // CASHIER MOVEMENTS
    suspend fun addCashierMovement(type: String, amount: Double, reason: String, operatorName: String) {
        val shift = dao.getCurrentShiftOnce() ?: return
        dao.insertMovement(
            CashierMovementEntity(
                shiftId = shift.id,
                type = type,
                amount = amount,
                reason = reason,
                operatorName = operatorName
            )
        )

        val updatedShift = when (type) {
            "SANGRIA" -> shift.copy(
                totalSangria = shift.totalSangria + amount,
                expectedCash = shift.expectedCash - amount
            )
            "SUPRIMENTO" -> shift.copy(
                totalSuprimento = shift.totalSuprimento + amount,
                expectedCash = shift.expectedCash + amount
            )
            else -> shift
        }
        dao.updateShift(updatedShift)

        dao.insertAuditLog(
            AuditLogEntity(
                userName = operatorName,
                action = "Movimento de Caixa ($type)",
                details = "R$ ${String.format("%.2f", amount)} - $reason"
            )
        )
    }

    suspend fun closeCashier(closingCashCounted: Double, operatorName: String) {
        val shift = dao.getCurrentShiftOnce() ?: return
        dao.updateShift(
            shift.copy(
                isOpen = false,
                closedAtMillis = System.currentTimeMillis(),
                closingCashCounted = closingCashCounted,
                operatorName = operatorName
            )
        )

        val difference = closingCashCounted - shift.expectedCash
        dao.insertAuditLog(
            AuditLogEntity(
                userName = operatorName,
                action = "Fechamento de Caixa",
                details = "Esperado: R$ ${String.format("%.2f", shift.expectedCash)}, Contado: R$ ${String.format("%.2f", closingCashCounted)}, Diferença: R$ ${String.format("%.2f", difference)}"
            )
        )
    }

    suspend fun openCashier(openingBalance: Double, operatorName: String) {
        dao.insertShift(
            CashierShiftEntity(
                openingBalance = openingBalance,
                expectedCash = openingBalance,
                isOpen = true,
                operatorName = operatorName
            )
        )
        dao.insertAuditLog(
            AuditLogEntity(
                userName = operatorName,
                action = "Abertura de Caixa",
                details = "Caixa aberto com saldo inicial de R$ ${String.format("%.2f", openingBalance)}"
            )
        )
    }

    suspend fun addProduct(product: ProductEntity) {
        dao.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) {
        dao.updateProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        dao.deleteProduct(product)
    }

    suspend fun addIngredient(ingredient: IngredientEntity) {
        dao.insertIngredient(ingredient)
    }

    suspend fun updateIngredient(ingredient: IngredientEntity) {
        dao.updateIngredient(ingredient)
    }

    suspend fun deleteIngredient(ingredient: IngredientEntity) {
        dao.deleteIngredient(ingredient)
    }

    suspend fun addFinancialAccount(account: FinancialAccountEntity) {
        dao.insertFinancialAccount(account)
    }

    suspend fun updateFinancialAccount(account: FinancialAccountEntity) {
        dao.updateFinancialAccount(account)
    }

    suspend fun deleteFinancialAccount(account: FinancialAccountEntity) {
        dao.deleteFinancialAccount(account)
    }

    suspend fun addSupplier(supplier: SupplierEntity) {
        dao.insertSupplier(supplier)
    }

    suspend fun updateSupplier(supplier: SupplierEntity) {
        dao.updateSupplier(supplier)
    }

    suspend fun deleteSupplier(supplier: SupplierEntity) {
        dao.deleteSupplier(supplier)
    }

    suspend fun addPurchaseOrder(order: PurchaseOrderEntity) {
        dao.insertPurchaseOrder(order)
    }

    suspend fun updatePurchaseOrder(order: PurchaseOrderEntity) {
        dao.updatePurchaseOrder(order)
    }

    suspend fun deletePurchaseOrder(order: PurchaseOrderEntity) {
        dao.deletePurchaseOrder(order)
    }

    suspend fun addEmployee(employee: EmployeeEntity) {
        dao.insertEmployee(employee)
    }

    suspend fun updateEmployee(employee: EmployeeEntity) {
        dao.updateEmployee(employee)
    }

    suspend fun deleteEmployee(employee: EmployeeEntity) {
        dao.deleteEmployee(employee)
    }

    suspend fun addReservation(reservation: ReservationEntity) {
        dao.insertReservation(reservation)
    }

    suspend fun logAudit(action: String, details: String, userName: String) {
        dao.insertAuditLog(
            AuditLogEntity(
                userName = userName,
                action = action,
                details = details
            )
        )
    }

    suspend fun markNotificationAsRead(id: Long) {
        val all = dao.getAllNotifications().firstOrNull() ?: return
        val notif = all.find { it.id == id } ?: return
        dao.updateNotification(notif.copy(isRead = true))
    }

    // BULK DATA IMPORT (ADMINISTRADOR)
    fun getAllImportedRawData(): Flow<List<ImportedRawDataEntity>> = dao.getAllImportedRawData()

    suspend fun insertImportedRawData(items: List<ImportedRawDataEntity>) {
        dao.insertImportedRawData(items)
    }

    suspend fun insertProducts(products: List<ProductEntity>) {
        dao.insertProducts(products)
    }

    suspend fun insertIngredients(ingredients: List<IngredientEntity>) {
        dao.insertIngredients(ingredients)
    }

    suspend fun insertCustomers(customers: List<CustomerEntity>) {
        dao.insertCustomers(customers)
    }

    suspend fun insertSuppliers(suppliers: List<SupplierEntity>) {
        dao.insertSuppliers(suppliers)
    }

    suspend fun insertFinancialAccounts(accounts: List<FinancialAccountEntity>) {
        dao.insertFinancialAccounts(accounts)
    }

    suspend fun insertEmployees(employees: List<EmployeeEntity>) {
        dao.insertEmployees(employees)
    }
}
