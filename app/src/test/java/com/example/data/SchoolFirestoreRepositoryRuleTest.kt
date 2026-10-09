package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SchoolFirestoreRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun galleryPhoto_addObserveAndDelete_succeedsForAuthenticatedAdmin() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repo = SchoolFirestoreRepository(firestore, auth)

        val addRes = withTimeout(5000L) {
            repo.addGalleryPhoto(
                occasionTheme = "Saraswati Puja Utsav",
                title = "Vandana Ceremony at UMV Gangauli",
                imageSource = "PRESET_CAMPUS",
                dateLabel = "08 Oct 2026"
            )
        }
        assertTrue(addRes.isSuccess)
        val photoId = addRes.getOrThrow()

        val observed = withTimeout(3000L) {
            repo.observeGalleryPhotos(uid).first { list -> list.any { it.id == photoId } }
        }
        assertTrue(observed.any { it.id == photoId && it.occasionTheme == "Saraswati Puja Utsav" })

        val delRes = withTimeout(5000L) { repo.deleteGalleryPhoto(photoId) }
        assertTrue(delRes.isSuccess)
    }

    @Test
    fun admission_createAcceptAndCancel_succeedsForOwner() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repo = SchoolFirestoreRepository(firestore, auth)

        val admId = withTimeout(5000L) {
            repo.createAdmission(
                studentName = "Rohit Kumar",
                fatherName = "Manoj Kumar",
                classApplied = "Class 11",
                streamApplied = "Science",
                prevPercentage = "91%",
                mobileNumber = "9431011223",
                village = "Gangauli"
            ).getOrThrow()
        }

        val acceptRes = withTimeout(5000L) { repo.updateAdmissionStatus(admId, "ACCEPTED") }
        assertTrue(acceptRes.isSuccess)

        val listAfterAccept = withTimeout(3000L) {
            repo.observeAdmissions(uid).first { list -> list.any { it.id == admId && it.status == "ACCEPTED" } }
        }
        assertTrue(listAfterAccept.any { it.id == admId && it.status == "ACCEPTED" })

        val cancelRes = withTimeout(5000L) { repo.updateAdmissionStatus(admId, "CANCELLED") }
        assertTrue(cancelRes.isSuccess)
    }

    @Test
    fun schoolAccountAndResultsAndTeachers_crudOperationsSucceed() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repo = SchoolFirestoreRepository(firestore, auth)

        val cfgRes = withTimeout(5000L) {
            repo.saveSchoolAccountConfig(
                accountHolderName = "UMV Gangauli Development Fund",
                bankName = "SBI Simri",
                accountNumber = "38492010485",
                ifscCode = "SBIN0004821",
                upiId = "umvgangauli@sbi",
                qrCodeTitle = "Official Fee QR",
                qrCustomImageUri = "DEFAULT_QR"
            )
        }
        assertTrue(cfgRes.isSuccess)
        val cfg = withTimeout(3000L) {
            repo.observeSchoolAccountConfig(uid).first { it != null }
        }
        assertNotNull(cfg)
        assertEquals("umvgangauli@sbi", cfg?.upiId)

        val resId = withTimeout(5000L) {
            repo.publishClassResult(
                rollNo = "260199",
                studentName = "Sneha Kumari",
                fatherName = "Ramesh Rai",
                className = "Class 10",
                totalMarks = 455,
                maxMarks = 500,
                division = "1st Division",
                subjectBreakdown = "Math: 95 | Sci: 92",
                isLive = true
            ).getOrThrow()
        }
        assertTrue(withTimeout(5000L) { repo.toggleResultLiveStatus(resId, false).isSuccess })

        val tchrId = withTimeout(5000L) {
            repo.registerTeacher(
                name = "Prof. S. K. Dubey",
                designation = "PGT Mathematics",
                department = "Science",
                subject = "Mathematics",
                qualification = "M.Sc., B.Ed.",
                experience = "10 Years",
                phone = "9431099887"
            ).getOrThrow()
        }
        assertTrue(tchrId.isNotEmpty())
    }

    @Test
    fun crossUserAndUnauthenticatedAccess_failsWithPermissionDenied() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repo = SchoolFirestoreRepository(firestore, auth)
        val photoId = withTimeout(5000L) {
            repo.addGalleryPhoto(
                occasionTheme = "Bihar Diwas",
                title = "Cultural Event",
                imageSource = "PRESET_CAMPUS",
                dateLabel = "22 Mar 2026"
            ).getOrThrow()
        }

        // Bob cannot read or delete Alice's photo on the server
        signInTestUser(BOB_EMAIL)
        var crossUserReadDenied = false
        try {
            withTimeout(5000L) {
                firestore.collection("gallery_photos").document(photoId).get(Source.SERVER).await()
            }
        } catch (e: FirebaseFirestoreException) {
            crossUserReadDenied = e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED
        }
        assertTrue("Expected Bob cross-user read to be PERMISSION_DENIED", crossUserReadDenied)

        val bobDeleteRes = withTimeout(5000L) { repo.deleteGalleryPhoto(photoId) }
        assertTrue("Expected Bob cross-user delete to fail", bobDeleteRes.isFailure)

        // Unauthenticated caller fails on observeGalleryPhotos
        auth.signOut()
        var unauthDenied = false
        try {
            withTimeout(3000L) {
                repo.observeGalleryPhotos("unauth_${UUID.randomUUID()}").first()
            }
        } catch (t: Throwable) {
            unauthDenied = isPermissionDenied(t)
        }
        assertTrue("Expected unauthenticated observeGalleryPhotos to be PERMISSION_DENIED", unauthDenied)
    }

    private fun isPermissionDenied(t: Throwable?): Boolean {
        var current = t
        while (current != null) {
            if (current is FirebaseFirestoreException &&
                current.code == FirebaseFirestoreException.Code.PERMISSION_DENIED
            ) {
                return true
            }
            current = current.cause
        }
        return false
    }

    private companion object {
        const val ALICE_EMAIL = "alice_admin@umvgangauli.in"
        const val BOB_EMAIL = "bob_other@umvgangauli.in"
    }
}
