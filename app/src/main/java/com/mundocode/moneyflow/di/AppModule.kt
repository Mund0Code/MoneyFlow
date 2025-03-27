package com.mundocode.moneyflow.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mundocode.moneyflow.core.SettingsDataStore
import com.mundocode.moneyflow.database.AppDatabase
import com.mundocode.moneyflow.database.ClienteDao
import com.mundocode.moneyflow.database.EventoDao
import com.mundocode.moneyflow.database.FacturaDao
import com.mundocode.moneyflow.database.ProyectoDao
import com.mundocode.moneyflow.database.TransaccionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app-database"
        ).build()
    }

    @Provides fun provideClienteDao(db: AppDatabase): ClienteDao = db.clienteDao()
    @Provides fun provideProyectoDao(db: AppDatabase): ProyectoDao = db.proyectoDao()
    @Provides fun provideTransaccionDao(db: AppDatabase): TransaccionDao = db.transaccionDao()
    @Provides fun provideEventoDao(db: AppDatabase): EventoDao = db.eventoDao()
    @Provides fun provideFacturaDao(db: AppDatabase): FacturaDao = db.facturaDao()

    @Provides
    fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore {
        return SettingsDataStore(context)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()


    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}