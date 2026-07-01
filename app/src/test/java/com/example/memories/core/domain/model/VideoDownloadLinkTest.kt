package com.example.memories.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VideoDownloadLinkTest {

    private fun videoWith(files: List<VideoFile>) = Video(
        id = 1, width = 1, height = 1, url = "u", image = "img", duration = 20,
        user = User(id = 1, name = "n", profileUrl = "u"), videoFiles = files,
    )

    private fun file(quality: String?, fileType: String?, link: String) =
        VideoFile(id = 1, quality = quality, fileType = fileType, width = null, height = null, link = link)

    @Test
    fun downloadLink_prefersHdMp4() {
        val video = videoWith(
            listOf(
                file("sd", "video/mp4", "sd.mp4"),
                file("hd", "video/mp4", "hd.mp4"),
                file("hd", "video/webm", "hd.webm"),
            )
        )

        assertEquals("hd.mp4", video.downloadLink)
    }

    @Test
    fun downloadLink_fallsBackToAnyMp4WhenNoHd() {
        val video = videoWith(
            listOf(
                file("sd", "video/webm", "sd.webm"),
                file("sd", "video/mp4", "sd.mp4"),
            )
        )

        assertEquals("sd.mp4", video.downloadLink)
    }

    @Test
    fun downloadLink_fallsBackToFirstWhenNoMp4() {
        val video = videoWith(
            listOf(
                file("sd", "video/webm", "first.webm"),
                file("hd", "video/webm", "second.webm"),
            )
        )

        assertEquals("first.webm", video.downloadLink)
    }

    @Test
    fun downloadLink_nullWhenNoFiles() {
        assertNull(videoWith(emptyList()).downloadLink)
    }
}
