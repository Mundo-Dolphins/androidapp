package es.mundodolphins.app.contracts

import com.networknt.schema.InputFormat
import com.networknt.schema.SchemaRegistry
import com.networknt.schema.SpecificationVersion
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ApiContractSchemaValidationTest {
    private val schemaRegistry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_7)

    private fun validate(
        schemaName: String,
        exampleName: String,
    ) {
        val rootDir = File("..").canonicalFile
        val schemaFile = File(rootDir, "contracts/schemas/$schemaName")

        assertTrue("Schema file not found: ${schemaFile.absolutePath}", schemaFile.exists())

        val exampleStream = javaClass.classLoader?.getResourceAsStream("api-contracts/$exampleName")
            ?: throw IllegalArgumentException("Example not found: $exampleName")
        val exampleJson = exampleStream.bufferedReader().use { it.readText() }

        val schema = schemaRegistry.getSchema(schemaFile.inputStream())
        val errors = schema.validate(exampleJson, InputFormat.JSON)

        val errorMessage = errors.joinToString("\n") { it.message }
        assertTrue(
            "Validation errors in $exampleName against $schemaName:\n$errorMessage",
            errors.isEmpty(),
        )
    }

    @Test
    fun `articles example matches schema`() {
        validate("articles.schema.json", "articles.valid.json")
    }

    @Test
    fun `episodes example matches schema`() {
        validate("episodes.schema.json", "episodes.valid.json")
    }

    @Test
    fun `seasons example matches schema`() {
        validate("season-index.schema.json", "seasons.valid.json")
    }

    @Test
    fun `videos example matches schema`() {
        validate("videos.schema.json", "videos.valid.json")
    }

    @Test
    fun `social example matches schema`() {
        validate("social.schema.json", "social.valid.json")
    }

    @Test
    fun `historical seasons example matches schema`() {
        validate(
            "historical-season-index.schema.json",
            "historical-seasons.valid.json",
        )
    }

    @Test
    fun `historical games example matches schema`() = validate("historical-season-games.schema.json", "historical-games.valid.json")

    @Test
    fun `historical season detail example matches schema`() = validate("historical-season-detail.schema.json", "historical-season-detail.valid.json")

    @Test
    fun `historical season stats example matches schema`() = validate("historical-season-stats.schema.json", "historical-season-stats.valid.json")
}
