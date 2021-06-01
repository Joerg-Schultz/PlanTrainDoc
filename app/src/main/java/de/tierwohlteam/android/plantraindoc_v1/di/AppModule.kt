package de.tierwohlteam.android.plantraindoc_v1.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.PTD_DB_NAME
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDdb
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
//Philipp used ApplicationComponent, but was renamed
//https://github.com/google/dagger/releases/tag/dagger-2.28.2
object AppModule {

    @Provides
    @Singleton
    fun providePTDdb(
        @ApplicationContext app:Context
    ) = Room.databaseBuilder(
        app.applicationContext,
        PTDdb::class.java,
        PTD_DB_NAME
    ).build()

    @Singleton
    @Provides
    fun provideUserDao(db: PTDdb) = db.userDao()

    @Singleton
    @Provides
    fun provideDogDao(db: PTDdb) = db.dogDao()

    @Singleton
    @Provides
    fun provideGoalDao(db: PTDdb) = db.goalDao()

    @Singleton
    @Provides
    fun providePlanDao(db: PTDdb) = db.planDao()

    @Singleton
    @Provides
    fun provideSessionDao(db: PTDdb) = db.sessionDao()

    @Singleton
    @Provides
    fun provideTrialDao(db: PTDdb) = db.trialDao()
}