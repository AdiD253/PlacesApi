package pl.adriandefus.placesapi

import androidx.test.runner.AndroidJUnit4
import io.realm.Realm
import io.realm.RealmConfiguration
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pl.adriandefus.placesapi.domain.model.PlaceInformation
import java.util.*

@RunWith(AndroidJUnit4::class)
class RealmDbTest {

    private lateinit var realm: Realm

    private val placeInformationMock = PlaceInformation(
        id = UUID.randomUUID().toString(),
        placeId = UUID.randomUUID().toString(),
        description = UUID.randomUUID().toString(),
        isFavourite = false
    )

    @Before
    fun setUp() {
        val config = RealmConfiguration.Builder().inMemory().name("test-db").build()
        realm = Realm.getInstance(config)
    }

    @Test
    fun shouldPlaceInformationTableBeEmptyIfNothingAdded() {
        realm.executeTransaction {
            //given
            val expectedPlaceInformationSize = 0

            //when
            val favouritePlaceInformationSize = realm.where(PlaceInformation::class.java).findAll().size

            //then
            assertEquals(expectedPlaceInformationSize, favouritePlaceInformationSize)
        }
    }

    @Test
    fun shouldCreatePlaceInformationTable() {
        realm.executeTransaction {
            //given: placeInformationMock

            //when
            val createdPlaceInformation = it.createObject(PlaceInformation::class.java, placeInformationMock.id).apply {
                placeId = placeInformationMock.placeId
                description = placeInformationMock.description
                isFavourite = placeInformationMock.isFavourite
            }

            //then
            assertEquals(placeInformationMock.id, createdPlaceInformation.id)
            assertEquals(placeInformationMock.placeId, createdPlaceInformation.placeId)
            assertEquals(placeInformationMock.description, createdPlaceInformation.description)
            assertEquals(placeInformationMock.isFavourite, createdPlaceInformation.isFavourite)
        }


    }

    @Test
    fun shouldGetAllPlaceInformationInRealm() {
        realm.executeTransaction {
            //given
            it.createObject(PlaceInformation::class.java, placeInformationMock.id)
            it.createObject(PlaceInformation::class.java, UUID.randomUUID().toString())
            val expectedSizeOfPlaceInformationInDb = 2

            //when
            val actualSizeOfPlaceInformationMockInDb = it.where(PlaceInformation::class.java).findAll().size

            //then
            assertEquals(expectedSizeOfPlaceInformationInDb, actualSizeOfPlaceInformationMockInDb)
        }
    }

    @Test
    fun shouldGetSpecificPlaceInformationInRealm() {
        realm.executeTransaction {
            //given
            it.createObject(PlaceInformation::class.java, placeInformationMock.id)
            it.createObject(PlaceInformation::class.java, UUID.randomUUID().toString())
            val expectedSizeOfSpecificPlaceInformationInDb = 1

            //when
            val actualSizeOfPlaceInformationMockInDb = it.where(PlaceInformation::class.java).equalTo("id", placeInformationMock.id).findAll().size

            //then
            assertEquals(expectedSizeOfSpecificPlaceInformationInDb, actualSizeOfPlaceInformationMockInDb)
        }
    }

    @Test
    fun shouldRemoveAllPlaceInformationFromRealm() {
        realm.executeTransaction {
            //given
            it.createObject(PlaceInformation::class.java, placeInformationMock.id)
            it.createObject(PlaceInformation::class.java, UUID.randomUUID().toString())
            it.createObject(PlaceInformation::class.java, UUID.randomUUID().toString())
            val expectedSizeOfPlacesInformationTable = 0

            //when
            val placeInformationRealmResults =
                it.where(PlaceInformation::class.java).findAll()

            placeInformationRealmResults.deleteAllFromRealm()

            val actualSizeOfPlaceInformationTable = it.where(PlaceInformation::class.java).findAll().size

            //then
            assertEquals(expectedSizeOfPlacesInformationTable, actualSizeOfPlaceInformationTable)
        }
    }

    @Test
    fun shouldRemoveSpecificPlaceInformationFromRealm() {
        realm.executeTransaction {
            //given
            it.createObject(PlaceInformation::class.java, placeInformationMock.id)
            it.createObject(PlaceInformation::class.java, UUID.randomUUID().toString())
            it.createObject(PlaceInformation::class.java, UUID.randomUUID().toString())
            val expectedSizeOfPlacesInformationTable = 2

            //when
            val placeInformationRealmResults =
                it.where(PlaceInformation::class.java).equalTo("id", placeInformationMock.id).findAll()

            placeInformationRealmResults.deleteAllFromRealm()

            val actualSizeOfPlaceInformationTable = it.where(PlaceInformation::class.java).findAll().size

            //then
            assertEquals(expectedSizeOfPlacesInformationTable, actualSizeOfPlaceInformationTable)
        }
    }

    @After
    fun tearDown() {
        realm.close()
    }
}