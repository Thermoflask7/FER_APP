package zaragoza.mateo.ferpantallas.modelos

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EnumsTest {

    @Test
    fun slugsDeNivel_coincidenConStrapi() {
        assertEquals("prepa", EducationLevel.PREPARATORIA.slug)
        assertEquals("uni", EducationLevel.UNIVERSIDAD.slug)
        assertEquals("posgrado", EducationLevel.POSGRADO.slug)
    }

    @Test
    fun slugsDePerfil_coincidenConStrapi() {
        assertEquals("aspirantes", ScholarshipProfile.ASPIRANTE.slug)
        assertEquals("becados", ScholarshipProfile.BECADO.slug)
        assertEquals("otros", ScholarshipProfile.OTROS.slug)
    }

    @Test
    fun nombresGuardados_noCambian() {
        // Estos nombres se guardan en SharedPreferences; renombrarlos rompería datos existentes.
        assertEquals(listOf("CLARO", "OSCURO", "SISTEMA"), AppTheme.entries.map { it.name })
        assertEquals(listOf("NORMAL", "GRANDE", "MUY_GRANDE"), TextSize.entries.map { it.name })
        assertEquals(listOf("PREPARATORIA", "UNIVERSIDAD", "POSGRADO"), EducationLevel.entries.map { it.name })
        assertEquals(listOf("ASPIRANTE", "BECADO", "OTROS"), ScholarshipProfile.entries.map { it.name })
    }

    @Test
    fun tamanosDeTexto_vanEnAumento() {
        val escalas = TextSize.entries.map { it.scale }
        assertEquals(1f, TextSize.NORMAL.scale)
        assertEquals(escalas.sorted(), escalas)
    }

    @Test
    fun pantallasDeDetalle_noMuestranBarras() {
        listOf(Destination.BIENVENIDA, Destination.POST_DETAIL, Destination.EVENT_DETAIL, Destination.SOBRE_TI)
            .forEach {
                assertFalse("${it.name} no debe mostrar header", it.showsHeader)
                assertFalse("${it.name} no debe mostrar bottom bar", it.showsBottomBar)
            }
    }

    @Test
    fun pantallasPrincipales_muestranBarras() {
        listOf(Destination.FEED, Destination.LIKED, Destination.PREGUNTAS, Destination.AJUSTES)
            .forEach {
                assertTrue(it.showsHeader)
                assertTrue(it.showsBottomBar)
            }
    }
}
