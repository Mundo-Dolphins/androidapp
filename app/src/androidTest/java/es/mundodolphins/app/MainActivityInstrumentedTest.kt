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
        // Verifica que el contexto de la app sea correcto
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("es.mundodolphins.app", appContext.packageName)
    }

    @Test
    fun activityLaunchesSuccessfully() {
        // Lanza la actividad y verifica que se inicia correctamente
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.use {
            assertEquals(MainActivity::class.java.name, it.result.resultData.component?.className)
        }
    }
}