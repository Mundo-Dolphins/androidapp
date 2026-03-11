package es.mundodolphins.app.install

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class InstallReferrerParserTest {
    @Test
    fun `returns null for null referrer`() {
        assertThat(InstallReferrerParser.parseEpisodeId(null)).isNull()
    }

    @Test
    fun `returns null for blank referrer`() {
        assertThat(InstallReferrerParser.parseEpisodeId("")).isNull()
    }

    @Test
    fun `parses episodeId from a simple referrer`() {
        assertThat(InstallReferrerParser.parseEpisodeId("episodeId=1704067200000"))
            .isEqualTo(1704067200000L)
    }

    @Test
    fun `parses episodeId when preceded by other params`() {
        assertThat(InstallReferrerParser.parseEpisodeId("utm_source=web&episodeId=1704067200000"))
            .isEqualTo(1704067200000L)
    }

    @Test
    fun `parses episodeId when followed by other params`() {
        assertThat(
            InstallReferrerParser.parseEpisodeId(
                "episodeId=1704067200000&utm_campaign=episode",
            ),
        ).isEqualTo(1704067200000L)
    }

    @Test
    fun `parses episodeId from a compound referrer with multiple params`() {
        assertThat(
            InstallReferrerParser.parseEpisodeId(
                "utm_source=web&episodeId=1704067200000&utm_campaign=podcast",
            ),
        ).isEqualTo(1704067200000L)
    }

    @Test
    fun `returns null when referrer contains no episodeId key`() {
        assertThat(InstallReferrerParser.parseEpisodeId("utm_source=web&utm_campaign=podcast"))
            .isNull()
    }

    @Test
    fun `returns null when episodeId value is not a long`() {
        assertThat(InstallReferrerParser.parseEpisodeId("episodeId=not_a_number"))
            .isNull()
    }

    @Test
    fun `does not match a key that merely starts with episodeId`() {
        assertThat(InstallReferrerParser.parseEpisodeId("episodeIdExtra=1704067200000"))
            .isNull()
    }

    @Test
    fun `returns null for referrer with only the key and no value`() {
        assertThat(InstallReferrerParser.parseEpisodeId("episodeId=")).isNull()
    }
}
