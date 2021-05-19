package de.tierwohlteam.android.plantraindoc_v1.models
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
}
