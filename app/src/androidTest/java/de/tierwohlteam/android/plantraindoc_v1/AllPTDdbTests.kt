package de.tierwohlteam.android.plantraindoc_v1


import de.tierwohlteam.android.plantraindoc_v1.daos.*
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepositoryTest
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDdbTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite


@ExperimentalCoroutinesApi
@RunWith(Suite::class)
@Suite.SuiteClasses(PTDdbTest::class,
    UserDaoTest::class, DogDaoTest::class, GoalDaoTest::class,
    PlanDaoTest::class, SessionDao::class, TrialDaoTest::class,
    PTDRepositoryTest::class)
class AllPTDdbTests {
}