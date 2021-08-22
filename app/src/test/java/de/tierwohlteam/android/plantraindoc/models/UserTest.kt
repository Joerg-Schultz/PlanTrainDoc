package de.tierwohlteam.android.plantraindoc.models
import com.benasher44.uuid.uuid4
import kotlin.test.Test
import kotlin.test.*

class UserTest {
    @Test
    internal fun userPropertiesTest() {
        val userName = "Test User"
        val uuid = uuid4()
        val testUser = User(
            id = uuid, name = userName,
            email = "Testuser@test.org", password = "123"
        )
        assertEquals(userName, testUser.name, "Name")
        assertEquals(uuid, testUser.id, "UUID as id")
        assertEquals("standard", testUser.role, "Role")
    }

    @Test
    internal fun emptyUserTest() {
        // Empty User
        assertFailsWith<IllegalArgumentException> {
            User(
                id = uuid4(), name = "",
                email = "Testuser@test.org", password = "123"
            )
        }
    }

    @Test
    internal fun invalidEMailUserTest() { // Invalid EMail
        assertFailsWith<IllegalArgumentException> {
            User(
                id = uuid4(), name = "Test User",
                email = "Testusertest.org", password = "123"
            )
        }
    }

    @Test
    fun userWithDogs() {
        val userName = "Test User"
        val uuid = uuid4()
        val testUser = User(
            id = uuid, name = userName,
            email = "Testuser@test.org", password = "123"
        )
        val dog1 = Dog(userID = testUser.id, name = "Luna")
        val dog2 = Dog(userID = testUser.id, name = "Rex")
        val userWithDogs = UserWithDogs(user = testUser, dogs = listOf(dog1, dog2))
        assertEquals(dog1, userWithDogs.dogs[0])
        assertEquals(dog2, userWithDogs.dogs[1])
    }
}
