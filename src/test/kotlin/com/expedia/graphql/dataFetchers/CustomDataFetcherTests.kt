package com.expedia.graphql.dataFetchers

import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.SchemaGeneratorConfig
import com.expedia.graphql.extensions.deepName
import com.expedia.graphql.toSchema
import graphql.GraphQL
import graphql.schema.DataFetcher
import graphql.schema.DataFetcherFactory
import graphql.schema.DataFetcherFactoryEnvironment
import graphql.schema.DataFetchingEnvironment
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CustomDataFetcherTests {
    @Test
    fun `Custom DataFetcher can be used on functions`() {
        val config = SchemaGeneratorConfig(supportedPackages = listOf("com.expedia"), dataFetcherFactory = PetDataFetcherFactory())
        val schema = toSchema(listOf(TopLevelObject(AnimalQuery())), config = config)

        val animalType = schema.getObjectType("Animal")
        assertEquals("AnimalDetails", animalType.getFieldDefinition("details").type.deepName)

        val graphQL = GraphQL.newGraphQL(schema).build()
        val execute = graphQL.execute("{ findAnimal { id type details { specialId } } }")

        val data = execute.getData<Map<String, Any>>()["findAnimal"] as? Map<*, *>
        assertEquals(1, data?.get("id"))
        assertEquals("cat", data?.get("type"))

        val details = data?.get("details") as? Map<*, *>
        assertEquals(11, details?.get("specialId"))
    }
}

class AnimalQuery {
    fun findAnimal(): Animal = Animal(1, "cat")
}

data class Animal(
    val id: Int,
    val type: String
) {
    lateinit var details: AnimalDetails
}

data class AnimalDetails(val specialId: Int)

class PetDataFetcherFactory : DataFetcherFactory<Any> {
    override fun get(environment: DataFetcherFactoryEnvironment?): DataFetcher<Any> = AnimalDetailsDataFetcher()
}

class AnimalDetailsDataFetcher : DataFetcher<Any> {

    override fun get(environment: DataFetchingEnvironment?): AnimalDetails {
        val animal = environment?.getSource<Animal>()
        val specialId = animal?.id?.plus(10) ?: 0
        return animal.let { AnimalDetails(specialId) }
    }
}