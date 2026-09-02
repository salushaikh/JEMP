package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.BrainstormStatus
import com.example.data.model.ProjectStatus
import com.example.data.repository.JoblessEmpireRepository
import kotlinx.coroutines.runBlocking
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
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Jobless Empire", appName)
    }

    @Test
    fun `verify jobless empire repository initialized with members and projects`() {
        val repo = JoblessEmpireRepository.instance
        assertTrue("Member profiles map must not be empty", repo.memberProfiles.value.isNotEmpty())
        assertTrue("Projects list must not be empty", repo.projects.value.isNotEmpty())
        assertTrue("Brainstorms list must not be empty", repo.brainstorms.value.isNotEmpty())

        val defaultMember = repo.memberProfiles.value["JEMP#014"]
        assertNotNull("Default member profile JEMP#014 should be present", defaultMember)
        assertEquals("Marcus Chen", defaultMember?.name)
    }

    @Test
    fun `verify member selection and application submission`() = runBlocking {
        val repo = JoblessEmpireRepository.instance
        repo.selectMember("JEMP#001")
        assertEquals("JEMP#001", repo.selectedMemberId.value)

        val initialCount = repo.applications.value.size
        val result = repo.submitApplication(
            fullName = "Taylor Builder",
            email = "taylor@example.com",
            resumeFileName = "taylor_resume.pdf",
            resumeFileSize = "1.4 MB",
            isCurrentlyUnemployed = true,
            consentChecked = true
        )

        assertTrue("Application submission should succeed", result.isSuccess)
        assertEquals(initialCount + 1, repo.applications.value.size)
        assertEquals("Taylor Builder", repo.latestSubmittedApplication.value?.fullName)
    }
}



