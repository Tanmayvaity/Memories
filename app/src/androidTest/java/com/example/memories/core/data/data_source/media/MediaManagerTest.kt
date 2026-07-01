package com.example.memories.core.data.data_source.media

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.memories.core.data.data_source.media.MediaManager
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.Type
import io.mockk.coVerify
import io.mockk.every
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import org.junit.Assert.assertTrue
import java.io.File

@RunWith(AndroidJUnit4::class)
class MediaManagerTest {

    private lateinit var context: Context
    private lateinit var mediaManager: MediaManager
    private lateinit var resolver: ContentResolver

    @Before
    fun setUp() {
        val realContext = InstrumentationRegistry.getInstrumentation().targetContext
        resolver = spyk(realContext.contentResolver)

        context = object : ContextWrapper(realContext) {
            override fun getContentResolver(): ContentResolver = resolver
        }

        mediaManager = spyk(MediaManager(context))

        File(context.getExternalFilesDir(null), "images").deleteRecursively()
        File(context.getExternalFilesDir(null), "videos").deleteRecursively()
    }

    @Test
    fun saveToInternalStorage_savesImageSuccessfullyAfterCompression() = runTest {
        val realUri = Uri.parse("content://com.android.providers.media.documents/document/image%3A123")

        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

        every { mediaManager.getMimeType(realUri) } returns "image/jpeg"
        coEvery { mediaManager.uriToBitmap(realUri) } returns Result.Success(bitmap)

        val result = mediaManager.saveToInternalStorage(listOf(realUri))

        if (result is Result.Error) {
            android.util.Log.e("TEST_FAILURE", "Image test failed", result.error)
        }

        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(1, successResult.data?.size)
        assertTrue(successResult.data?.first()?.path?.endsWith(".jpg") == true)
    }

    @Test
    fun saveToInternalStorage_savesVideoSuccessfullyWithoutCompression() = runTest {
        val realUri = Uri.parse("content://com.android.providers.media.documents/document/video%3A123")
        val dummyVideoContent = "dummy video bytes data".toByteArray()

        every { resolver.openInputStream(realUri) } answers {
            java.io.ByteArrayInputStream(dummyVideoContent)
        }
        every { mediaManager.getMimeType(realUri) } returns "video/mp4"

        val result = mediaManager.saveToInternalStorage(listOf(realUri))

        if (result is Result.Error) {
            android.util.Log.e("TEST_FAILURE", "Video test failed", result.error)
        }

        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(1, successResult.data?.size)
        assertTrue(successResult.data?.first()?.path?.endsWith(".mp4") == true)
    }

    @Test
    fun saveToInternalStorage_returnsErrorForUnsupportedType() = runTest {
        val realUri = Uri.parse("content://com.android.providers.media.documents/document/file%3A123")

        every { mediaManager.getMimeType(realUri) } returns "application/zip"

        val result = mediaManager.saveToInternalStorage(listOf(realUri))

        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertTrue(error is IllegalArgumentException)
        assertEquals("Unknown type", error.message)
    }

    @Test
    fun saveToInternalStorage_savesPngSuccessfully() = runTest {
        val realUri = Uri.parse("content://com.android.providers.media.documents/document/image%3A456")

        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

        every { mediaManager.getMimeType(realUri) } returns "image/png"
        coEvery { mediaManager.uriToBitmap(realUri) } returns Result.Success(bitmap)

        val result = mediaManager.saveToInternalStorage(listOf(realUri))

        if (result is Result.Error) {
            android.util.Log.e("TEST_FAILURE", "PNG test failed", result.error)
        }

        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(1, successResult.data?.size)
        assertTrue(successResult.data?.first()?.path?.endsWith(".png") == true)
    }
    @Test
    fun saveToInternalStorage_savesMultipleUrisSuccessfully() = runTest {
        val imageUri = Uri.parse("content://test/document/image%3A1")
        val videoUri = Uri.parse("content://test/document/video%3A2")

        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val dummyVideoContent = "dummy video bytes data".toByteArray()

        every { mediaManager.getMimeType(imageUri) } returns "image/jpeg"
        every { mediaManager.getMimeType(videoUri) } returns "video/mp4"
        coEvery { mediaManager.uriToBitmap(imageUri) } returns Result.Success(bitmap)
        every { resolver.openInputStream(videoUri) } answers {
            java.io.ByteArrayInputStream(dummyVideoContent)
        }

        val result = mediaManager.saveToInternalStorage(listOf(imageUri, videoUri))

        if (result is Result.Error) {
            android.util.Log.e("TEST_FAILURE", "Multi-uri test failed", result.error)
        }

        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(2, successResult.data?.size)
        assertTrue(successResult.data?.any { it.path?.endsWith(".jpg") == true } == true)
        assertTrue(successResult.data?.any { it.path?.endsWith(".mp4") == true } == true)
    }

    @Test
    fun saveToInternalStorage_deletesPartialFilesOnFailure() = runTest {
        val goodUri = Uri.parse("content://test/document/image%3A1")
        val badUri = Uri.parse("content://test/document/image%3A2")

        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

        every { mediaManager.getMimeType(goodUri) } returns "image/jpeg"
        every { mediaManager.getMimeType(badUri) } returns "image/jpeg"

        coEvery { mediaManager.uriToBitmap(goodUri) } returns Result.Success(bitmap)
        // second uri fails mid-loop after the first file was already created
        coEvery { mediaManager.uriToBitmap(badUri) } returns Result.Success(null)

        val result = mediaManager.saveToInternalStorage(listOf(goodUri, badUri))

        assertTrue(result is Result.Error)

        // the first file was created then cleaned up — verify nothing left in images dir
        val imagesDir = File(context.getExternalFilesDir(null), "images")
        val leftover = imagesDir.listFiles()?.size ?: 0
        assertEquals(0, leftover)
    }

    @Test
    fun saveToInternalStorage_videoErrorsWhenInputStreamNull() = runTest {
        val videoUri = Uri.parse("content://test/document/video%3A1")

        every { mediaManager.getMimeType(videoUri) } returns "video/mp4"
        every { resolver.openInputStream(videoUri) } returns null

        val result = mediaManager.saveToInternalStorage(listOf(videoUri))

        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertTrue(error is IllegalStateException)
        assertEquals("Unable to open input stream", error.message)
    }

    @Test
    fun saveToInternalStorage_imageErrorsWhenOutputStreamNull() = runTest {
        val imageUri = Uri.parse("content://test/document/image%3A1")
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

        every { mediaManager.getMimeType(imageUri) } returns "image/jpeg"
        coEvery { mediaManager.uriToBitmap(imageUri) } returns Result.Success(bitmap)
        every { resolver.openOutputStream(any<Uri>()) } returns null

        val result = mediaManager.saveToInternalStorage(listOf(imageUri))

        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertTrue(error is IllegalStateException)
        assertEquals("Unable to open output stream", error.message)
    }

    @Test
    fun saveToInternalStorage_emptyListReturnsSuccessWithEmptyData() = runTest {
        val result = mediaManager.saveToInternalStorage(emptyList())

        assertTrue(result is Result.Success)
        assertEquals(0, (result as Result.Success).data?.size)
    }

    @Test
    fun saveToInternalStorage_failureOnFirstUriCreatesNothing() = runTest {
        val badUri = Uri.parse("content://test/document/image%3A1")

        every { mediaManager.getMimeType(badUri) } returns "image/jpeg"
        coEvery { mediaManager.uriToBitmap(badUri) } returns Result.Success(null)

        val result = mediaManager.saveToInternalStorage(listOf(badUri))

        assertTrue(result is Result.Error)

        val imagesDir = File(context.getExternalFilesDir(null), "images")
        assertEquals(0, imagesDir.listFiles()?.size ?: 0)
    }

    @Test
    fun saveToInternalStorage_failureOnMiddleUriDeletesEarlierFilesAndStops() = runTest {
        val first = Uri.parse("content://test/document/image%3A1")   // succeeds
        val second = Uri.parse("content://test/document/image%3A2")  // fails
        val third = Uri.parse("content://test/document/image%3A3")   // should never be reached

        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

        every { mediaManager.getMimeType(first) } returns "image/jpeg"
        every { mediaManager.getMimeType(second) } returns "image/jpeg"
        every { mediaManager.getMimeType(third) } returns "image/jpeg"

        coEvery { mediaManager.uriToBitmap(first) } returns Result.Success(bitmap)
        coEvery { mediaManager.uriToBitmap(second) } returns Result.Success(null) // triggers throw
        coEvery { mediaManager.uriToBitmap(third) } returns Result.Success(bitmap)

        val result = mediaManager.saveToInternalStorage(listOf(first, second, third))

        assertTrue(result is Result.Error)

        // first file was created then cleaned; third never processed
        val imagesDir = File(context.getExternalFilesDir(null), "images")
        assertEquals(0, imagesDir.listFiles()?.size ?: 0)

        // third uri's bitmap should never have been requested
        coVerify(exactly = 0) { mediaManager.uriToBitmap(third) }
    }

    @Test
    fun saveToInternalStorage_videoCleanupOnLaterFailure() = runTest {
        val videoUri = Uri.parse("content://test/document/video%3A1")  // succeeds
        val badUri = Uri.parse("content://test/document/image%3A2")    // fails

        val dummyVideoContent = "dummy video bytes data".toByteArray()

        every { mediaManager.getMimeType(videoUri) } returns "video/mp4"
        every { mediaManager.getMimeType(badUri) } returns "image/jpeg"

        every { resolver.openInputStream(videoUri) } answers {
            java.io.ByteArrayInputStream(dummyVideoContent)
        }
        coEvery { mediaManager.uriToBitmap(badUri) } returns Result.Success(null) // triggers throw

        val result = mediaManager.saveToInternalStorage(listOf(videoUri, badUri))

        assertTrue(result is Result.Error)

        // the video file written for the first uri must be deleted
        val videosDir = File(context.getExternalFilesDir(null), "videos")
        assertEquals(0, videosDir.listFiles()?.size ?: 0)
    }

    @Test
    fun saveToInternalStorage_videoErrorsWhenInputStreamThrows() = runTest {
        val videoUri = Uri.parse("content://test/document/video%3A1")

        every { mediaManager.getMimeType(videoUri) } returns "video/mp4"
        every { resolver.openInputStream(videoUri) } throws
                java.io.FileNotFoundException("document not found")

        val result = mediaManager.saveToInternalStorage(listOf(videoUri))

        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertTrue(error is java.io.FileNotFoundException)

        // nothing should be left behind in videos/
        val videosDir = File(context.getExternalFilesDir(null), "videos")
        assertEquals(0, videosDir.listFiles()?.size ?: 0)
    }

    @Test
    fun saveToCacheStorage_rejectsVideo() = runTest {
        val videoUri = Uri.parse("content://test/document/video%3A1")
        val bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)

        every { mediaManager.getMimeType(videoUri) } returns "video/mp4"

        val result = mediaManager.saveToCacheStorage(videoUri, bitmap)

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).error is UnsupportedOperationException)
    }

    @Test
    fun saveToCacheStorage_savesImageSuccessfully() = runTest {
        val imageUri = Uri.parse("content://test/document/image%3A1")
        val bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)

        every { mediaManager.getMimeType(imageUri) } returns "image/jpeg"

        val result = mediaManager.saveToCacheStorage(imageUri, bitmap)

        assertTrue(result is Result.Success)
        val uriType = (result as Result.Success).data
        assertEquals(Type   .IMAGE_JPG, uriType?.type)
        // FileProvider returns a content:// uri
        assertTrue(uriType?.uri?.startsWith("content://") == true)
    }

    @Test
    fun saveToCacheStorage_rejectsUnknownType() = runTest {
        val uri = Uri.parse("content://test/document/x%3A1")
        val bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)

        every { mediaManager.getMimeType(uri) } returns "application/zip"

        val result = mediaManager.saveToCacheStorage(uri, bitmap)

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).error is IllegalArgumentException)
    }

    @Test
    fun getType_mapsKnownMimeTypes() {
        assertEquals(Type.IMAGE_JPG, mediaManager.getType("image/jpeg"))
        assertEquals(Type.IMAGE_JPG, mediaManager.getType("image/jpg"))
        assertEquals(Type.IMAGE_PNG, mediaManager.getType("image/png"))
        assertEquals(Type.VIDEO_MP4, mediaManager.getType("video/mp4"))
    }

    @Test
    fun getType_returnsUnknownForUnsupportedMime() {
        assertEquals(Type.UNKNOWN_TYPE, mediaManager.getType("application/zip"))
        assertEquals(Type.UNKNOWN_TYPE, mediaManager.getType("image/gif"))
    }

    @Test
    fun normalizeRotation_wrapsAndHandlesNegatives() = with(mediaManager) {
        assertEquals(0f, 0f.normalizeRotation())
        assertEquals(90f, 90f.normalizeRotation())
        assertEquals(0f, 360f.normalizeRotation())
        assertEquals(90f, 450f.normalizeRotation())
        assertEquals(270f, (-90f).normalizeRotation())
        assertEquals(359f, (-1f).normalizeRotation())
    }

    @Test
    fun deleteInternalMedia_deletesExistingFiles() = runTest {
        val dir = File(context.cacheDir, "del_test").apply { mkdirs() }
        val f1 = File(dir, "a.jpg").apply { writeBytes(byteArrayOf(1)) }
        val f2 = File(dir, "b.mp4").apply { writeBytes(byteArrayOf(2)) }

        val result = mediaManager.deleteInternalMedia(
            listOf(Uri.fromFile(f1), Uri.fromFile(f2))
        )

        assertTrue(result is Result.Success)
        assertFalse(f1.exists())
        assertFalse(f2.exists())
    }

    @Test
    fun deleteInternalMedia_succeedsWhenFileMissing() = runTest {
        val ghost = Uri.fromFile(File(context.cacheDir, "does_not_exist.jpg"))
        val result = mediaManager.deleteInternalMedia(listOf(ghost))
        assertTrue(result is Result.Success) // missing file is a no-op
    }


    @Test
    fun saveRemoteMediaToCache_errorsOnBadUrl() = runTest {
        val result = mediaManager.saveRemoteMediaToCache(
            url = "https://invalid.invalid/nope.jpg",
            isImage = true
        )
        assertTrue(result is Result.Error)
    }

}



