package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.SchoolRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read school app name and repository data`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Gangauli Vidyalaya", appName)

        val repo = SchoolRepository(context)
        assertTrue(repo.campusFacilities.isNotEmpty())
        assertTrue(repo.studentResults.any { it.rollNo == "260101" })
        assertNotNull(repo.getTimetableFor("CLASS_12_SCI", "MON").firstOrNull())

        // Verify institutional theme palette & typography definitions
        assertEquals(com.example.ui.theme.RoyalNavyPrimary, com.example.ui.theme.InstitutionalLightColorScheme.primary)
        assertEquals(com.example.ui.theme.SaffronGoldSecondary, com.example.ui.theme.InstitutionalLightColorScheme.secondary)
        assertNotNull(com.example.ui.theme.Typography.displayLarge.fontFamily)
        assertNotNull(com.example.ui.theme.Typography.bodyLarge.fontFamily)
    }
}
