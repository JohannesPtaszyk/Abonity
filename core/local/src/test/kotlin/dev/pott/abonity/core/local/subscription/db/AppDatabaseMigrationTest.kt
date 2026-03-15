package dev.pott.abonity.core.local.subscription.db

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import tech.apter.junit.jupiter.robolectric.RobolectricExtension

private const val TEST_DB_NAME = "app_database_migration_test.db"

private const val CREATE_SUBSCRIPTION_ENTITY_V1 =
    "CREATE TABLE IF NOT EXISTS subscription_entity " +
        "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
        "name TEXT NOT NULL, " +
        "description TEXT, " +
        "price REAL NOT NULL, " +
        "currency TEXT NOT NULL, " +
        "first_payment_local_date TEXT NOT NULL, " +
        "payment_type TEXT NOT NULL, " +
        "period_count INTEGER, " +
        "period TEXT)"

@ExtendWith(RobolectricExtension::class)
@Config(maxSdk = 34)
class AppDatabaseMigrationTest {

    private val context get() = RuntimeEnvironment.getApplication()

    @BeforeEach
    fun setUp() {
        context.deleteDatabase(TEST_DB_NAME)
    }

    @AfterEach
    fun tearDown() {
        context.deleteDatabase(TEST_DB_NAME)
    }

    @Test
    fun `MIGRATION_1_2 adds notification_days_before column to subscription_entity`() {
        createV1DatabaseWithData()

        openV2Database().use { db ->
            assertThat(hasColumn(db, "subscription_entity", "notification_days_before")).isTrue()
        }
    }

    @Test
    fun `MIGRATION_1_2 preserves existing rows after migration`() {
        createV1DatabaseWithData()

        openV2Database().use { db ->
            val cursor = db.query(
                "SELECT id, name, price FROM subscription_entity WHERE name = 'Periodic Sub'",
            )
            cursor.use {
                assertThat(it.moveToFirst()).isTrue()
            }
        }
    }

    @Test
    fun `MIGRATION_1_2 sets notification_days_before to null for pre-existing rows`() {
        createV1DatabaseWithData()

        openV2Database().use { db ->
            val cursor = db.query(
                "SELECT notification_days_before FROM subscription_entity WHERE name = 'Periodic Sub'",
            )
            cursor.use {
                assertThat(it.moveToFirst()).isTrue()
                assertThat(it.isNull(0)).isTrue()
            }
        }
    }

    @Test
    fun `MIGRATION_1_2 notification_days_before column accepts null values after migration`() {
        createV1DatabaseWithData()

        openV2Database().use { db ->
            db.execSQL(
                "INSERT INTO subscription_entity " +
                    "(name, price, currency, first_payment_local_date, " +
                    "payment_type, notification_days_before) " +
                    "VALUES ('New Sub', 4.99, 'USD', '2024-01-01', 'ONE_TIME', NULL)",
            )
            val cursor = db.query(
                "SELECT notification_days_before FROM subscription_entity WHERE name = 'New Sub'",
            )
            cursor.use {
                assertThat(it.moveToFirst()).isTrue()
                assertThat(it.isNull(0)).isTrue()
            }
        }
    }

    @Test
    fun `MIGRATION_1_2 notification_days_before column accepts integer values after migration`() {
        createV1DatabaseWithData()

        openV2Database().use { db ->
            db.execSQL(
                "INSERT INTO subscription_entity " +
                    "(name, price, currency, first_payment_local_date, " +
                    "payment_type, notification_days_before) " +
                    "VALUES ('Notified Sub', 9.99, 'EUR', '2024-06-01', 'ONE_TIME', 7)",
            )
            val cursor = db.query(
                "SELECT notification_days_before FROM subscription_entity WHERE name = 'Notified Sub'",
            )
            cursor.use {
                assertThat(it.moveToFirst()).isTrue()
                assertThat(it.isNull(0)).isFalse()
                assertThat(it.getInt(0)).isEqualTo(7)
            }
        }
    }

    private fun createV1DatabaseWithData() {
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(TEST_DB_NAME)
            .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL(CREATE_SUBSCRIPTION_ENTITY_V1)
                    db.execSQL(
                        "INSERT INTO subscription_entity " +
                            "(name, description, price, currency, " +
                            "first_payment_local_date, payment_type, period_count, period) " +
                            "VALUES ('Periodic Sub', 'Monthly service', 9.99, 'EUR', '2020-02-02', 'PERIODICALLY', 1, 'MONTHS')",
                    )
                    db.execSQL(
                        "INSERT INTO subscription_entity " +
                            "(name, description, price, currency, " +
                            "first_payment_local_date, payment_type) " +
                            "VALUES ('One Time Sub', 'One time purchase', 49.99, 'USD', '2021-05-15', 'ONE_TIME')",
                    )
                }

                override fun onUpgrade(
                    db: SupportSQLiteDatabase,
                    oldVersion: Int,
                    newVersion: Int,
                ) = Unit
            })
            .build()
        FrameworkSQLiteOpenHelperFactory().create(config).use { helper ->
            helper.writableDatabase.close()
        }
    }

    private fun openV2Database(): SupportSQLiteDatabase {
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(TEST_DB_NAME)
            .callback(object : SupportSQLiteOpenHelper.Callback(2) {
                override fun onCreate(db: SupportSQLiteDatabase) = Unit

                override fun onUpgrade(
                    db: SupportSQLiteDatabase,
                    oldVersion: Int,
                    newVersion: Int,
                ) {
                    if (oldVersion < 2) {
                        MIGRATION_1_2.migrate(db)
                    }
                }
            })
            .build()
        return FrameworkSQLiteOpenHelperFactory().create(config).writableDatabase
    }

    private fun hasColumn(
        db: SupportSQLiteDatabase,
        tableName: String,
        columnName: String,
    ): Boolean {
        db.query("PRAGMA table_info($tableName)").use { cursor ->
            val nameIndex = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) {
                if (cursor.getString(nameIndex) == columnName) return true
            }
        }
        return false
    }
}
