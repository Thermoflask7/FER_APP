package zaragoza.mateo.ferpantallas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import zaragoza.mateo.ferpantallas.modelos.EducationLevel
import zaragoza.mateo.ferpantallas.modelos.UserProfile
import zaragoza.mateo.ferpantallas.modelos.matchesProfile
import zaragoza.mateo.ferpantallas.network.PostDto
import zaragoza.mateo.ferpantallas.network.toModel

/**
 * PRUEBAS QUE FALLAN A PROPÓSITO.
 *
 * Sirven para demostrar que el pipeline de CI/CD se detiene cuando las pruebas fallan:
 * el job "Unit tests" queda en rojo y el job "Build APKs" no se ejecuta (no se genera .apk).
 *
 * Para que el pipeline vuelva a pasar, borra este archivo.
 */
class FailingDemoTest {

    @Test
    fun falla_postSinIdDeberiaExistir() {
        // toModel() regresa null cuando no hay id, así que esto falla.
        assertNotNull(PostDto(id = null, body = "sin id").toModel())
    }

    @Test
    fun falla_slugDeUniversidadIncorrecto() {
        // El slug real es "uni", no "universidad".
        assertEquals("universidad", EducationLevel.UNIVERSIDAD.slug)
    }

    @Test
    fun falla_aspiranteNoDeberiaVerPostsDeBecados() {
        // Un aspirante NO ve publicaciones solo para becados, así que esto falla.
        val aspirante = UserProfile(audience = "aspirantes", level = "uni")
        assertTrue(matchesProfile(setOf("becados"), emptySet(), aspirante))
    }
}
