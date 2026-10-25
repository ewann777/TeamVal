package com.example.teamval

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.teamval.data.storage.TeamLogoStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Stage 4.1 image pipeline: optimized copies in private storage keep their aspect
 * ratio, are downscaled to a maximum side and can be removed again.
 */
@RunWith(AndroidJUnit4::class)
class TeamLogoStorageTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun largeLandscapeBitmapIsDownscaledKeepingAspectRatio() {
        val storage = TeamLogoStorage(context)
        val source = Bitmap.createBitmap(2000, 1000, Bitmap.Config.ARGB_8888)

        val fileName = storage.persistBitmap(source)
        val stored = storage.bitmapFor(fileName)

        assertNotNull(stored)
        // Longest side is capped at 1024 and the 2:1 aspect ratio is preserved.
        assertEquals(TeamLogoStorage.MAX_SIDE, maxOf(stored!!.width, stored.height))
        assertEquals(2f, stored.width.toFloat() / stored.height, 0.02f)
        assertTrue(storage.exists(fileName))

        assertTrue(storage.delete(fileName))
        assertFalse(storage.exists(fileName))
    }

    @Test
    fun portraitBitmapKeepsItsProportions() {
        val storage = TeamLogoStorage(context)
        val source = Bitmap.createBitmap(900, 1600, Bitmap.Config.ARGB_8888)

        val fileName = storage.persistBitmap(source)
        val stored = storage.bitmapFor(fileName)

        assertNotNull(stored)
        assertEquals(TeamLogoStorage.MAX_SIDE, maxOf(stored!!.width, stored.height))
        assertTrue(stored.height > stored.width)
        assertEquals(900f / 1600f, stored.width.toFloat() / stored.height, 0.02f)

        storage.delete(fileName)
    }

    @Test
    fun smallSquareBitmapIsStoredWithoutUpscaling() {
        val storage = TeamLogoStorage(context)
        val source = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)

        val fileName = storage.persistBitmap(source)
        val stored = storage.bitmapFor(fileName)

        assertNotNull(stored)
        assertEquals(512, stored!!.width)
        assertEquals(512, stored.height)

        storage.delete(fileName)
    }
}
