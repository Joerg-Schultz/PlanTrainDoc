package de.tierwohlteam.android.plantraindoc.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.tierwohlteam.android.plantraindoc.repositories.PTDdb
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
    @Named("testUserDao")
    fun provideUserDao(db: PTDdb) = db.userDao()

    @Provides
    @Named("testDogDao")
    fun provideDogDao(db: PTDdb) = db.dogDao()

    @Provides
    @Named("testGoalDao")
    fun provideGoalDao(db: PTDdb) = db.goalDao()

    @Provides
    @Named("testPlanDao")
    fun providePlanDao(db: PTDdb) = db.planDao()

    @Provides
    @Named("testSessionDao")
    fun provideSessionDao(db: PTDdb) = db.sessionDao()

    @Provides
    @Named("testTrialDao")
    fun provideTestTrialDao(db : PTDdb) = db.trialDao()
}