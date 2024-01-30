package dev.pott.abonity.core.local.subscription.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscription_entity")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("description")
    val description: String?,
    @ColumnInfo("price")
    val price: Double,
    /**
     * Currency code - ISO 4217
     */
    @ColumnInfo("currency")
    val currency: String,
    @ColumnInfo("first_payment_local_date")
    val firstPaymentLocalDate: String,
    @ColumnInfo("payment_type")
    val paymentType: LocalPaymentType,
    /**
     * Period count is only and always set for paymentType of [LocalPaymentType.PERIODICALLY]
     */
    @ColumnInfo("period_count")
    val periodCount: Int?,
    /**
     * Period is only and always set for paymentType of [LocalPaymentType.PERIODICALLY]
     */
    @ColumnInfo("period")
    val period: LocalPaymentPeriod?,
) {
    init {
        if (paymentType == LocalPaymentType.PERIODICALLY) {
            checkNotNull(period) {
                "Period must be set for periodically payment type"
            }
            checkNotNull(periodCount) {
                "Period count must be set for periodically payment type"
            }
        }
    }
}
