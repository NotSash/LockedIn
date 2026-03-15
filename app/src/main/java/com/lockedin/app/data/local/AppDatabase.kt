package com.lockedin.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lockedin.app.data.local.converter.LockedInTypeConverters
import com.lockedin.app.data.local.dao.CategoryDao
import com.lockedin.app.data.local.dao.HistoryDao
import com.lockedin.app.data.local.dao.PasswordDao
import com.lockedin.app.data.local.entity.CategoryEntity
import com.lockedin.app.data.local.entity.HistoryEntity
import com.lockedin.app.data.local.entity.PasswordEntity
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

/**
 * SQLCipher-encrypted Room database.
 *
 * All sensitive columns are encrypted again at the field level via CryptoManager.
 */
@Database(
    entities = [
        PasswordEntity::class,
        HistoryEntity::class,
        CategoryEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(LockedInTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun passwordDao(): PasswordDao
    abstract fun historyDao(): HistoryDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        private const val DB_NAME = "lockedin_encrypted.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(
            context: Context,
            passphraseProvider: DatabasePassphrase
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context.applicationContext, passphraseProvider).also {
                    INSTANCE = it
                }
            }
        }

        private fun buildDatabase(
            context: Context,
            passphraseProvider: DatabasePassphrase
        ): AppDatabase {
            SQLiteDatabase.loadLibs(context)
            val passphrase = passphraseProvider.getOrCreatePassphrase()
            val factory = SupportFactory(passphrase)

            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DB_NAME
            )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

