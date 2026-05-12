package es.mundodolphins.app.contracts

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ApiContractSchemaValidationTest {

    private val mapper = ObjectMapper()
    private val factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7)

    private fun validate(schemaName: String, exampleName: String) {
        val rootDir = File("..").canonicalFile
        val schemaFile = File(rootDir, "contracts/schemas/$schemaName")
        val exampleFile = File(rootDir, "contracts/examples/$exampleName")

        assertTrue("Schema file not found: ${schemaFile.absolutePath}", schemaFile.exists())
        assertTrue("Example file not found: ${exampleFile.absolutePath}", exampleFile.exists())

        val schema = factory.getSchema(schemaFile.inputStream())
        val node: JsonNode = mapper.readTree(exampleFile.inputStream())

        val errors = schema.validate(node)
        
        val errorMessage = errors.joinToString("\n") { it.message }
        assertTrue("Validation errors in $exampleName against $schemaName:\n$errorMessage", errors.isEmpty())
    }

    @Test
    fun `articles example matches schema`() = validate("articles.schema.json", "articles.valid.json")

    @Test
    fun `episodes example matches schema`() = validate("episodes.schema.json", "episodes.valid.json")

    @Test
    fun `seasons example matches schema`() = validate("seasons.schema.json", "seasons.valid.json")

    @Test
    fun `videos example matches schema`() = validate("videos.schema.json", "videos.valid.json")

    @Test
    fun `social example matches schema`() = validate("social.schema.json", "social.valid.json")

    @Test
    fun `historical seasons example matches schema`() = validate("historical-seasons.schema.json", "historical-seasons.valid.json")

    @Test
    fun `historical games example matches schema`() = validate("historical-games.schema.json", "historical-games.valid.json")
}
