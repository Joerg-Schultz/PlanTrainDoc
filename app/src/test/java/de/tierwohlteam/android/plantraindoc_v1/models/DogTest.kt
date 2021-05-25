package de.tierwohlteam.android.plantraindoc_v1.models

import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DogTest {
    @Test
    internal fun dogPropertiesTest(){
        val handler = User(name = "Test User",
            email = "Testuser@test.org", password = "123")
        val uuid = uuid4()
        val testDog = Dog(id = uuid, name = "Waldo", userID = handler.id)
        assertThat(testDog.id).isEqualTo(uuid)
        assertThat(testDog.userID).isEqualTo(handler.id)
    }
}