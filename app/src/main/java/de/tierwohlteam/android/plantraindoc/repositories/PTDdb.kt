package de.tierwohlteam.android.plantraindoc.repositories

import androidx.room.*
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import de.tierwohlteam.android.plantraindoc.daos.*
import kotlinx.datetime.*
import de.tierwohlteam.android.plantraindoc.models.*

/**
 * Build the Room database for PlanTrainDoc
 * Actual start of the db in AppModule ->Hilt
 */
@Database(
    entities = [User::class, Dog::class,
        Goal::class, GoalDependencyCrossRef::class,
        Plan::class, PlanHelper::class, PlanConstraint::class,
        Session::class,
        Trial::class, TrialCriterion::class],
    version = 3, exportSchema = true
)
@TypeConverters(Converters::class)
abstract class PTDdb : RoomDatabase(){

    abstract fun userDao(): UserDao
    abstract fun dogDao(): DogDao
    abstract fun goalDao(): GoalDao
    abstract fun planDao(): PlanDao
    abstract fun sessionDao(): SessionDao
    abstract fun trialDao(): TrialDao
}

class Converters {
    @TypeConverter
    fun toUUID(uuidString: String?): Uuid? {
        return if(uuidString != null) uuidFrom(uuidString) else null
    }

    @TypeConverter
    fun fromUUID(uuid: Uuid?): String? {
        return if(uuid != null) uuid.toString() else null
    }

    @TypeConverter
    fun toString(localDateTime: LocalDateTime): String {
        return localDateTime.toString()
    }

    @TypeConverter
    fun fromString(localDateTimeString: String): LocalDateTime{
        return localDateTimeString.toLocalDateTime()
    }
}
