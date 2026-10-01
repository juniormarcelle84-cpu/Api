package com.example.data.local

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
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "saved_projects")
data class SavedProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val query: String,
    val matchedApiIdsCsv: String,
    val createdAt: Long,
    val notes: String = ""
)

@Entity(tableName = "watchlist_items")
data class WatchlistEntity(
    @PrimaryKey val apiId: String,
    val addedAt: Long,
    val recentOutages: Int = 0,
    val lastStatus: String = "UP",
    val alertEnabled: Boolean = true
)

@Dao
interface ProjectDao {
    @Query("SELECT * FROM saved_projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<SavedProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: SavedProjectEntity)

    @Query("DELETE FROM saved_projects WHERE id = :id")
    suspend fun deleteProject(id: String)
}

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist_items ORDER BY addedAt DESC")
    fun getAllWatchlist(): Flow<List<WatchlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlist(item: WatchlistEntity)

    @Query("DELETE FROM watchlist_items WHERE apiId = :apiId")
    suspend fun deleteWatchlist(apiId: String)

    @Query("UPDATE watchlist_items SET alertEnabled = :enabled WHERE apiId = :apiId")
    suspend fun updateAlert(apiId: String, enabled: Boolean)
}

@Database(entities = [SavedProjectEntity::class, WatchlistEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun watchlistDao(): WatchlistDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "apimatch_local.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
