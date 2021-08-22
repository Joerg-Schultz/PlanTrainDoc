package de.tierwohlteam.android.plantraindoc


import de.tierwohlteam.android.plantraindoc.daos.*
import de.tierwohlteam.android.plantraindoc.repositories.PTDRepositoryTest
import de.tierwohlteam.android.plantraindoc.repositories.PTDdbTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite


@ExperimentalCoroutinesApi
@RunWith(Suite::class)
@Suite.SuiteClasses(PTDdbTest::class,
    UserDaoTest::class, DogDaoTest::class, GoalDaoTest::class,
    PlanDaoTest::class, SessionDaoTest::class, TrialDaoTest::class,
    PTDRepositoryTest::class)
class AllPTDdbTests {
}