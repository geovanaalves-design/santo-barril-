package com.example.data

import com.example.data.dao.RestaurantDao
import com.example.data.model.AppNotificationEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.CashierShiftEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.EmployeeEntity
import com.example.data.model.FinancialAccountEntity
import com.example.data.model.IngredientEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.OrderStatus
import com.example.data.model.PrintJobEntity
import com.example.data.model.PrintStatus
import com.example.data.model.ProductEntity
import com.example.data.model.ProductionSector
import com.example.data.model.PurchaseOrderEntity
import com.example.data.model.RecipeItemEntity
import com.example.data.model.ReservationEntity
import com.example.data.model.RestaurantTableEntity
import com.example.data.model.SupplierEntity
import com.example.data.model.TableStatus
import com.example.data.model.UserEntity
import com.example.data.model.UserRole

object DatabaseSeeder {

    suspend fun seedDatabase(dao: RestaurantDao) {
        // Users (Senha provisoria 000 para todos conforme solicitado)
        dao.insertUser(UserEntity(1, "admin", "Carlos Silva (Admin)", UserRole.ADMINISTRADOR, "000"))
        dao.insertUser(UserEntity(2, "marcos", "Marcos Oliveira (Gerente)", UserRole.GERENTE, "000"))
        dao.insertUser(UserEntity(3, "gabriel", "Gabriel Santos (Garçom)", UserRole.GARCOM, "000"))
        dao.insertUser(UserEntity(4, "ryan", "Ryan Alves (Garçom)", UserRole.GARCOM, "000"))
        dao.insertUser(UserEntity(5, "cozinha", "Equipe da Cozinha", UserRole.COZINHA_BAR, "000"))

        // Tables
        val tables = mutableListOf<RestaurantTableEntity>()
        for (i in 1..8) {
            tables.add(
                RestaurantTableEntity(
                    number = i,
                    name = "Mesa 0$i",
                    section = "Salão Principal",
                    status = if (i == 3) TableStatus.EM_PREPARO else if (i == 4) TableStatus.OCUPADA else TableStatus.LIVRE,
                    waiterName = if (i == 3 || i == 4) "Carlos Silva" else "",
                    customerName = if (i == 3) "Mariana Costa" else if (i == 4) "Lucas Oliveira" else "",
                    customerPhone = if (i == 3) "(17) 99823-4411" else if (i == 4) "(17) 98712-3344" else "",
                    openedAtMillis = if (i == 3 || i == 4) System.currentTimeMillis() - 45 * 60 * 1000L else 0L
                )
            )
        }
        for (i in 9..14) {
            tables.add(
                RestaurantTableEntity(
                    number = i,
                    name = "Mesa $i",
                    section = "Varanda",
                    status = if (i == 10) TableStatus.PEDIDO_ENVIADO else TableStatus.LIVRE,
                    waiterName = if (i == 10) "Carlos Silva" else "",
                    customerName = if (i == 10) "Bruno Martins" else "",
                    customerPhone = if (i == 10) "(17) 99112-9988" else "",
                    openedAtMillis = if (i == 10) System.currentTimeMillis() - 15 * 60 * 1000L else 0L
                )
            )
        }
        for (i in 15..18) {
            tables.add(
                RestaurantTableEntity(
                    number = i,
                    name = "Mesa $i",
                    section = "Deck",
                    status = TableStatus.LIVRE
                )
            )
        }
        dao.insertTables(tables)

        // Customers
        val customers = listOf(
            CustomerEntity(1, "Lucas Oliveira", "(17) 98712-3344", "Instagram", 5, 480.0),
            CustomerEntity(2, "Mariana Costa", "(17) 99823-4411", "Indicação de amigo", 3, 290.0),
            CustomerEntity(3, "Bruno Martins", "(17) 99112-9988", "Google", 1, 115.0),
            CustomerEntity(4, "Fernanda Lima", "(17) 99778-1200", "Passou em frente", 2, 175.0),
            CustomerEntity(5, "Rodrigo Ramos", "(17) 98144-5566", "TikTok", 4, 390.0),
            CustomerEntity(6, "Camila Santos", "(17) 99638-0320", "Já conhecia", 8, 820.0)
        )
        customers.forEach { dao.insertCustomer(it) }

        // Ingredients for Ficha Técnica & Stock
        val ingredients = listOf(
            IngredientEntity(1, "Carne Bovina p/ Espeto (Alcatra/Contra)", "kg", 18.5, 5.0, 38.0),
            IngredientEntity(2, "Filé de Peito de Frango", "kg", 14.0, 4.0, 18.5),
            IngredientEntity(3, "Bacon Fatiado Defumado", "kg", 6.2, 2.0, 32.0),
            IngredientEntity(4, "Palito de Madeira p/ Espeto", "un", 650.0, 150.0, 0.08),
            IngredientEntity(5, "Queijo Coalho Espeto", "un", 45.0, 15.0, 4.2),
            IngredientEntity(6, "Batata Palito Congelada", "kg", 22.0, 8.0, 12.0),
            IngredientEntity(7, "Pão Brioche Selado", "un", 35.0, 10.0, 2.4),
            IngredientEntity(8, "Blend Bovino Artesanal 180g", "un", 40.0, 12.0, 7.5),
            IngredientEntity(9, "Queijo Cheddar Cremoso", "kg", 4.8, 1.5, 29.0),
            IngredientEntity(10, "Barril Chopp Pilsen 50L", "L", 85.0, 20.0, 9.8),
            IngredientEntity(11, "Cachaça Artesanal Santo Barril", "L", 8.5, 2.0, 24.0),
            IngredientEntity(12, "Xarope de Frutas Vermelhas", "ml", 1800.0, 500.0, 0.04),
            IngredientEntity(13, "Carvão Vegetal Especial", "kg", 45.0, 15.0, 4.5)
        )
        dao.insertIngredients(ingredients)

        // Products
        val products = listOf(
            ProductEntity(1, "ESP01", "Espetinho de Alcatra Nobre", "Espetinhos", "Carne macia selecionada grelhada no braseiro com farofa e vinagrete", 16.00, 6.20, "un", true, 10.0, 65.0, ProductionSector.CHURRASQUEIRA, true),
            ProductEntity(2, "ESP02", "Espetinho de Frango c/ Bacon", "Espetinhos", "Cubos suculentos de peito de frango envolvidos em manta de bacon defumado", 15.00, 5.10, "un", true, 10.0, 50.0, ProductionSector.CHURRASQUEIRA, true),
            ProductEntity(3, "ESP03", "Espetinho de Queijo Coalho c/ Melado", "Espetinhos", "Queijo coalho tostadinho na brasa regado com melado de cana da casa", 14.00, 4.80, "un", true, 8.0, 40.0, ProductionSector.CHURRASQUEIRA, true),
            ProductEntity(4, "ESP04", "Espetinho de Picanha Especial", "Espetinhos", "Fatias selecionadas de picanha com sal grosso de parrilla", 22.00, 9.50, "un", true, 8.0, 32.0, ProductionSector.CHURRASQUEIRA, true),
            ProductEntity(5, "ESP05", "Espetinho de Coração de Frango", "Espetinhos", "Coraçõezinhos marinados em ervas finas e cerveja preta", 14.50, 4.90, "un", true, 8.0, 38.0, ProductionSector.CHURRASQUEIRA, true),
            ProductEntity(6, "POR01", "Batata Rústica Especial Santo Barril", "Porções", "Batata frita crocante temperada com páprica, coberta com cheddar e farofa de bacon", 38.00, 12.00, "porção", true, 5.0, 25.0, ProductionSector.COZINHA, true),
            ProductEntity(7, "POR02", "Dadinho de Tapioca c/ Geléia de Pimenta", "Porções", "12 dadinhos crocantes de queijo coalho e tapioca com geléia artesanal", 34.00, 10.50, "porção", true, 5.0, 18.0, ProductionSector.COZINHA, false),
            ProductEntity(8, "POR03", "Mandioca Cremosa Frita c/ Parmesão", "Porções", "Mandioca amanteigada com crosta dourada e queijo parmesão ralado", 29.00, 7.80, "porção", true, 5.0, 20.0, ProductionSector.COZINHA, false),
            ProductEntity(9, "POR04", "Isca de Tilápia c/ Molho Tártaro", "Porções", "Iscas empanadas na farinha panko, limão siciliano e molho tártaro", 48.00, 18.00, "porção", true, 4.0, 15.0, ProductionSector.COZINHA, false),
            ProductEntity(10, "COM01", "Picanha na Chapa 700g (Serve 3)", "Comidas", "Tiras de picanha na chapa de ferro com cebola caramelizada, mandioca e arroz", 115.00, 48.00, "un", true, 3.0, 10.0, ProductionSector.CHURRASQUEIRA, false),
            ProductEntity(11, "COM02", "Costela ao Bafo Santo Barril 800g", "Comidas", "Costela desmanchando na pressão lenta com mandioca na manteiga de garrafa", 89.00, 35.00, "un", true, 3.0, 8.0, ProductionSector.COZINHA, false),
            ProductEntity(12, "BUR01", "Burguer Santo Barril Artesanal", "Hambúrgueres", "Pão brioche, blend bovino 180g grelhado na brasa, cheddar, bacon e cebola crispy", 36.00, 13.50, "un", true, 6.0, 28.0, ProductionSector.COZINHA, true),
            ProductEntity(13, "CHP01", "Chopp Pilsen Artesanal Caneca 500ml", "Chopp", "Chopp geladíssimo tirado na serpentina sob pressão perfeita", 12.00, 3.80, "caneca", true, 15.0, 85.0, ProductionSector.BAR, false),
            ProductEntity(14, "CHP02", "Chopp IPA Santo Barril 500ml", "Chopp", "Chopp encorpado aromático com lúpulos nobres cítricos", 17.00, 6.00, "caneca", true, 10.0, 45.0, ProductionSector.BAR, false),
            ProductEntity(15, "DRK01", "Caipirinha Santo Barril", "Drinks", "Cachaça artesanal da casa, limão taiti, açúcar orgânico e gelo em cubos", 22.00, 5.50, "taça", true, 8.0, 50.0, ProductionSector.BAR, true),
            ProductEntity(16, "DRK02", "Gin Tônica com Frutas Vermelhas", "Drinks", "Gin premium, água tônica botânica, infusão de zimbro e xarope de frutas", 28.00, 7.50, "taça", true, 8.0, 40.0, ProductionSector.BAR, false),
            ProductEntity(17, "BEB01", "Refrigerante Lata 350ml", "Refrigerantes", "Coca-Cola normal, Zero, Guaraná Antarctica ou Sprite", 7.00, 2.90, "lata", true, 20.0, 72.0, ProductionSector.BAR, false),
            ProductEntity(18, "BEB02", "Água Mineral 500ml", "Bebidas", "Com ou sem gás", 5.00, 1.40, "garrafa", true, 20.0, 60.0, ProductionSector.BAR, false),
            ProductEntity(19, "SOB01", "Pudim na Caneca Santo Barril", "Sobremesas", "Pudim de leite condensado sedoso com calda caramelizada", 14.00, 3.90, "un", true, 5.0, 18.0, ProductionSector.COZINHA, false),
            ProductEntity(20, "CMB01", "Combo Roda de Boteco", "Combos", "3 Espetos variados + Porção Batata Rústica + 2 Chopps Pilsen 500ml", 78.00, 28.00, "combo", true, 5.0, 20.0, ProductionSector.COZINHA, false)
        )
        dao.insertProducts(products)

        // Recipe items (Fichas técnicas)
        val recipes = listOf(
            RecipeItemEntity(1, 1, 1, 0.140), // 140g carne bovina
            RecipeItemEntity(2, 1, 4, 1.0),   // 1 palito
            RecipeItemEntity(3, 2, 2, 0.120), // 120g frango
            RecipeItemEntity(4, 2, 3, 0.040), // 40g bacon
            RecipeItemEntity(5, 2, 4, 1.0),   // 1 palito
            RecipeItemEntity(6, 6, 6, 0.400), // 400g batata
            RecipeItemEntity(7, 6, 9, 0.080), // 80g cheddar
            RecipeItemEntity(8, 6, 3, 0.050), // 50g bacon
            RecipeItemEntity(9, 12, 7, 1.0),  // 1 pão
            RecipeItemEntity(10, 12, 8, 1.0), // 1 blend 180g
            RecipeItemEntity(11, 12, 9, 0.040),
            RecipeItemEntity(12, 13, 10, 0.500) // 500ml Chopp
        )
        dao.insertRecipeItems(recipes)

        // Active Orders
        val order1 = OrderEntity(
            id = 1,
            orderNumber = 101,
            tableNumber = 4,
            customerName = "Lucas Oliveira",
            customerPhone = "(17) 98712-3344",
            waiterName = "Carlos Silva",
            timestampMillis = System.currentTimeMillis() - 35 * 60 * 1000L,
            status = OrderStatus.ENTREGUE,
            subtotal = 68.0,
            discount = 0.0,
            serviceFee = 6.80,
            totalAmount = 74.80,
            isPaid = false
        )
        dao.insertOrder(order1)

        val itemsOrder1 = listOf(
            OrderItemEntity(1, 1, 1, "Espetinho de Alcatra Nobre", 16.00, 2, "Bem passado", ProductionSector.CHURRASQUEIRA, OrderStatus.ENTREGUE),
            OrderItemEntity(2, 1, 13, "Chopp Pilsen Artesanal Caneca 500ml", 12.00, 2, "Caneca bem gelada", ProductionSector.BAR, OrderStatus.ENTREGUE),
            OrderItemEntity(3, 1, 17, "Refrigerante Lata 350ml", 7.00, 1, "Coca-cola com gelo e limão", ProductionSector.BAR, OrderStatus.ENTREGUE)
        )
        dao.insertOrderItems(itemsOrder1)

        val order2 = OrderEntity(
            id = 2,
            orderNumber = 102,
            tableNumber = 3,
            customerName = "Mariana Costa",
            customerPhone = "(17) 99823-4411",
            waiterName = "Gabriel Santos",
            timestampMillis = System.currentTimeMillis() - 25 * 60 * 1000L,
            status = OrderStatus.EM_PREPARO,
            subtotal = 83.0,
            discount = 0.0,
            serviceFee = 8.30,
            totalAmount = 91.30,
            isPaid = false
        )
        dao.insertOrder(order2)

        val itemsOrder2 = listOf(
            OrderItemEntity(4, 2, 2, "Espetinho de Frango c/ Bacon", 15.00, 2, "Sem cebola", ProductionSector.CHURRASQUEIRA, OrderStatus.EM_PREPARO),
            OrderItemEntity(5, 2, 6, "Batata Rústica Especial Santo Barril", 38.00, 1, "Cheddar e bacon separados", ProductionSector.COZINHA, OrderStatus.EM_PREPARO),
            OrderItemEntity(6, 2, 15, "Caipirinha Santo Barril", 22.00, 1, "Pouco açúcar", ProductionSector.BAR, OrderStatus.PRONTO)
        )
        dao.insertOrderItems(itemsOrder2)

        val order3 = OrderEntity(
            id = 3,
            orderNumber = 103,
            tableNumber = 3,
            customerName = "Mariana Costa",
            customerPhone = "(17) 99823-4411",
            waiterName = "Ryan Alves",
            timestampMillis = System.currentTimeMillis() - 10 * 60 * 1000L,
            status = OrderStatus.EM_PREPARO,
            subtotal = 38.0,
            discount = 0.0,
            serviceFee = 3.80,
            totalAmount = 41.80,
            isPaid = false
        )
        dao.insertOrder(order3)

        val itemsOrder3 = listOf(
            OrderItemEntity(7, 3, 1, "Espetinho de Alcatra Nobre", 16.00, 1, "Ao ponto", ProductionSector.CHURRASQUEIRA, OrderStatus.EM_PREPARO),
            OrderItemEntity(8, 3, 15, "Caipirinha Santo Barril", 22.00, 1, "Tradicional de Limão", ProductionSector.BAR, OrderStatus.EM_PREPARO)
        )
        dao.insertOrderItems(itemsOrder3)

        // Cashier Shift
        val shift = CashierShiftEntity(
            id = 1,
            openedAtMillis = System.currentTimeMillis() - 4 * 3600 * 1000L,
            openingBalance = 200.0,
            expectedCash = 420.0,
            totalCash = 220.0,
            totalPix = 340.0,
            totalDebit = 210.0,
            totalCredit = 480.0,
            totalSangria = 0.0,
            totalSuprimento = 0.0,
            isOpen = true,
            operatorName = "Administrador"
        )
        dao.insertShift(shift)

        // Financial Accounts
        val accounts = listOf(
            FinancialAccountEntity(1, "Fornecedor Carnes Boi Nobre", "PAGAR", "Fornecedores", 1250.00, System.currentTimeMillis() + 86400000L * 3),
            FinancialAccountEntity(2, "Conta de Energia Elétrica CEMIG", "PAGAR", "Energia", 840.00, System.currentTimeMillis() + 86400000L * 5),
            FinancialAccountEntity(3, "Aluguel Ponto Comercial", "PAGAR", "Aluguel", 3200.00, System.currentTimeMillis() + 86400000L * 10),
            FinancialAccountEntity(4, "Distribuidora Chopp Artesanal", "PAGAR", "Fornecedores", 980.00, System.currentTimeMillis() + 86400000L * 2),
            FinancialAccountEntity(5, "Evento Corporativo Fechado (Sinal)", "RECEBER", "Vendas PDV", 1500.00, System.currentTimeMillis() + 86400000L * 1, isPaid = true, paidDateMillis = System.currentTimeMillis())
        )
        dao.insertFinancialAccounts(accounts)

        // Suppliers
        val suppliers = listOf(
            SupplierEntity(1, "Boi Nobre Distribuidora de Carnes", "12.345.678/0001-90", "(17) 3214-5500", "Roberto", "Cortes nobres, picanha, alcatra, frango", "15 dias", 1),
            SupplierEntity(2, "Cervejaria e Chopp Santo Vale", "98.765.432/0001-11", "(17) 3222-8899", "Fernanda", "Barris de chopp pilsen, ipa, copos", "7 dias", 2),
            SupplierEntity(3, "Hortifruti São José", "45.678.901/0001-22", "(17) 3233-1122", "Marcos", "Mandioca, batatas, limão, hortelã, temperos", "À vista", 1)
        )
        dao.insertSuppliers(suppliers)

        // Purchase Orders
        val purchase = PurchaseOrderEntity(
            id = 1,
            supplierName = "Cervejaria e Chopp Santo Vale",
            description = "4 Barris 50L Chopp Pilsen Artesanal",
            totalAmount = 1960.00,
            status = "Aprovado"
        )
        dao.insertPurchaseOrder(purchase)

        // Employees
        val employees = listOf(
            EmployeeEntity(1, "Carlos Silva", "Garçom", "(17) 99123-4567", 10.0, 1840.00, 16, 22),
            EmployeeEntity(2, "Renato Souza", "Garçom", "(17) 98877-6655", 10.0, 1420.00, 12, 17),
            EmployeeEntity(3, "Marcos Parrillero", "Churrasqueiro", "(17) 99765-4321", 0.0, 0.0, 0, 0),
            EmployeeEntity(4, "Juliana Oliveira", "Cozinheira Chefe", "(17) 99654-3210", 0.0, 0.0, 0, 0),
            EmployeeEntity(5, "Rafael Bartender", "Bartender", "(17) 99543-2109", 0.0, 0.0, 0, 0)
        )
        dao.insertEmployees(employees)

        // Reservations
        val reservations = listOf(
            ReservationEntity(1, "Ana Paula Ferreira", "(17) 99881-2233", "Hoje", "20:30", 6, 9, "Aniversário, mesa decorada na varanda", "Confirmada"),
            ReservationEntity(2, "Ricardo Silveira", "(17) 99772-4455", "Hoje", "21:00", 4, 11, "Prefere perto do telão", "Reservada")
        )
        reservations.forEach { dao.insertReservation(it) }

        // Thermal Print Jobs
        dao.insertPrintJob(
            PrintJobEntity(
                1,
                102,
                ProductionSector.CHURRASQUEIRA,
                3,
                "Carlos Silva",
                "2x Espetinho de Frango c/ Bacon (Sem cebola)",
                PrintStatus.IMPRESSO
            )
        )
        dao.insertPrintJob(
            PrintJobEntity(
                2,
                102,
                ProductionSector.COZINHA,
                3,
                "Carlos Silva",
                "1x Batata Rústica Especial (Cheddar separado)",
                PrintStatus.IMPRESSO
            )
        )
        dao.insertPrintJob(
            PrintJobEntity(
                3,
                102,
                ProductionSector.BAR,
                3,
                "Carlos Silva",
                "1x Caipirinha Santo Barril (Pouco açúcar)",
                PrintStatus.IMPRESSO
            )
        )

        // Notifications
        dao.insertNotification(
            AppNotificationEntity(
                1,
                "Caipirinha Pronta no Bar",
                "Mesa 03: 1x Caipirinha Santo Barril está pronta para entrega!",
                "PEDIDO_PRONTO",
                System.currentTimeMillis() - 5 * 60 * 1000L
            )
        )
        dao.insertNotification(
            AppNotificationEntity(
                2,
                "Estoque Baixo: Queijo Coalho",
                "Estoque de Queijo Coalho atingiu 15 un (Mínimo: 15 un). Reposição recomendada.",
                "ESTOQUE_BAIXO",
                System.currentTimeMillis() - 40 * 60 * 1000L
            )
        )

        // Audit Logs
        dao.insertAuditLog(
            AuditLogEntity(
                1,
                System.currentTimeMillis() - 4 * 3600 * 1000L,
                "Administrador",
                "Abertura de Caixa",
                "Caixa aberto com saldo inicial de R$ 200,00"
            )
        )
    }
}
