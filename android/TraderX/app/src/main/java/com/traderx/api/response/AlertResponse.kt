package com.traderx.api.response

import com.traderx.type.AlertType
import com.traderx.type.TransactionType

data class AlertResponse(
    val alertType: AlertType,
    val amount: Double,
    val limitValue: Double,
    val transactionType: TransactionType,
    val username: String,
    val createdAt: Long,
    val code: String
)