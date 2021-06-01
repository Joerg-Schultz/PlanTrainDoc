package de.tierwohlteam.android.plantraindoc_v1.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDdb
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Named("testDB")
    fun provideInMemoryPTDdb(@ApplicationContext app: Context) =
        Room.inMemoryDatabaseBuilder(app, PTDdb::class.java)
        .allowMainThreadQueries()
        .build()

    @Provides
    @Named("testTrialDao")
    fun provideTestTrialDao(@Named("testDB") db : PTDdb) = db.trialDao()
}