package de.tierwohlteam.android.plantraindoc_v1

import de.tierwohlteam.android.plantraindoc_v1.daos.UserDaoTest
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDdbTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(PTDdbTest::class, UserDaoTest::class)
class AllPTDdbTests