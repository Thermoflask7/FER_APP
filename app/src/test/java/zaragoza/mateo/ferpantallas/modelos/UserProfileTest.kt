package zaragoza.mateo.ferpantallas.modelos

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserProfileTest {

    private val becadoUni = UserProfile(audience = "becados", level = "uni")

    @Test
    fun publicacionSinRestricciones_laVeTodos() {
        assertTrue(matchesProfile(emptySet(), emptySet(), becadoUni))
    }

    @Test
    fun perfilSinDatos_veTodo() {
        assertTrue(matchesProfile(setOf("aspirantes"), setOf("prepa"), UserProfile()))
    }

    @Test
    fun audienciaYNivelCoinciden_laVe() {
        assertTrue(matchesProfile(setOf("becados", "aspirantes"), setOf("uni"), becadoUni))
    }

    @Test
    fun audienciaNoCoincide_noLaVe() {
        assertFalse(matchesProfile(setOf("aspirantes"), emptySet(), becadoUni))
    }

    @Test
    fun nivelNoCoincide_noLaVe() {
        assertFalse(matchesProfile(emptySet(), setOf("prepa", "posgrado"), becadoUni))
    }

    @Test
    fun soloAudienciaRestringida_yCoincide_laVe() {
        assertTrue(matchesProfile(setOf("becados"), emptySet(), becadoUni))
    }

    @Test
    fun slugsDeEnums_funcionanConElFiltro() {
        val perfil = UserProfile(
            audience = ScholarshipProfile.ASPIRANTE.slug,
            level = EducationLevel.PREPARATORIA.slug
        )
        assertTrue(matchesProfile(setOf("aspirantes"), setOf("prepa"), perfil))
        assertFalse(matchesProfile(setOf("becados"), setOf("prepa"), perfil))
    }
}
