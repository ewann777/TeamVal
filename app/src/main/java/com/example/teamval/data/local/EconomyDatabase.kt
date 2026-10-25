package com.example.teamval.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert
import com.example.teamval.domain.model.CoinTransaction
import com.example.teamval.domain.model.MissionStatus
import com.example.teamval.domain.model.TransactionKind
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Base local de la economía (etapa 5): monedas, movimientos, artículos
 * adquiridos y recompensas. Room garantiza que cada compra se registre como
 * una única transacción atómica (saldo + inventario + historial).
 */
@Entity(tableName = "coin_transactions")
data class CoinTransactionEntity(
    @PrimaryKey val id: String,
    val amount: Int,
    val kind: String,
    val timestamp: Long,
    val description: String
) {
    fun toDomain(): CoinTransaction = CoinTransaction(
        id = id,
        amount = amount,
        kind = enumValues<TransactionKind>().find { it.name == kind } ?: TransactionKind.Reward,
        timestamp = timestamp,
        description = description
    )
}

@Entity(tableName = "owned_items")
data class OwnedItemEntity(
    @PrimaryKey val itemId: String,
    val acquiredAt: Long
)

@Entity(tableName = "mission_status")
data class MissionStatusEntity(
    @PrimaryKey val missionId: String,
    val progress: Int,
    val claimed: Boolean
) {
    fun toDomain(): MissionStatus = MissionStatus(missionId = missionId, progress = progress, claimed = claimed)
}

@Dao
interface EconomyDao {
    @Query("SELECT * FROM coin_transactions ORDER BY timestamp DESC")
    fun observeTransactions(): Flow<List<CoinTransactionEntity>>

    @Query("SELECT COUNT(*) FROM coin_transactions")
    suspend fun transactionCount(): Int

    @Query("SELECT COALESCE(SUM(amount), 0) FROM coin_transactions")
    suspend fun balance(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTransaction(tx: CoinTransactionEntity)

    @Query("SELECT itemId FROM owned_items")
    fun observeOwnedItems(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM owned_items WHERE itemId = :itemId")
    suspend fun ownedCount(itemId: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOwned(item: OwnedItemEntity)

    @Query("SELECT * FROM mission_status")
    fun observeMissionStatuses(): Flow<List<MissionStatusEntity>>

    @Query("SELECT * FROM mission_status WHERE missionId = :missionId")
    suspend fun missionStatus(missionId: String): MissionStatusEntity?

    @Upsert
    suspend fun saveMissionStatus(status: MissionStatusEntity)
}

@Database(
    entities = [CoinTransactionEntity::class, OwnedItemEntity::class, MissionStatusEntity::class],
    version = 1,
    exportSchema = false
)
abstract class EconomyDatabase : RoomDatabase() {
    abstract fun economyDao(): EconomyDao

    companion object {
        @Volatile
        private var instance: EconomyDatabase? = null

        fun get(context: Context): EconomyDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    EconomyDatabase::class.java,
                    "teamval_economy.db"
                ).build().also { instance = it }
            }
    }
}
