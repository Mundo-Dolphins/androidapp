package es.mundodolphins.app

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityInstrumentedTest {
    @Test
    fun useAppContext() {
        // Verify that the app context is correct
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("es.mundodolphins.app", appContext.packageName)
    }

    @Test
    fun activityLaunchesSuccessfully() {
        // Launch the activity and verify it starts correctly
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.use {
            assertEquals(
                MainActivity::class.java.name,
                it.result.resultData.component
                    ?.className,
            )
        }
    }
}
