package zaragoza.mateo.ferpantallas.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import zaragoza.mateo.ferpantallas.modelos.PostAttachment
import java.time.OffsetDateTime
import java.time.ZoneId

class MappersTest {

    private fun local(iso: String) =
        OffsetDateTime.parse(iso).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()

    // ---------- PostDto.toModel ----------

    @Test
    fun post_sinId_regresaNull() {
        assertNull(PostDto(id = null, body = "hola").toModel())
    }

    @Test
    fun post_camposBasicos_seMapeanCorrectamente() {
        val post = PostDto(
            id = 7,
            documentId = "abc",
            title = "Convocatoria",
            description = "Descripción",
            body = "Cuerpo",
            publishedAt = "2026-03-01T10:00:00Z"
        ).toModel()

        assertNotNull(post)
        post!!
        assertEquals(7, post.id)
        assertEquals("abc", post.documentId)
        assertEquals("Convocatoria", post.title)
        assertEquals("Descripción", post.description)
        assertEquals("Cuerpo", post.body)
        assertEquals(local("2026-03-01T10:00:00Z"), post.createdAt)
    }

    @Test
    fun post_tituloYDescripcionEnBlanco_seVuelvenNull() {
        val post = PostDto(id = 1, title = "   ", description = "").toModel()!!
        assertNull(post.title)
        assertNull(post.description)
    }

    @Test
    fun post_bodyNull_seVuelveCadenaVacia() {
        assertEquals("", PostDto(id = 1, body = null).toModel()!!.body)
    }

    @Test
    fun post_sinPublishedAt_usaCreatedAt() {
        val post = PostDto(id = 1, createdAt = "2026-01-15T08:30:00Z").toModel()!!
        assertEquals(local("2026-01-15T08:30:00Z"), post.createdAt)
    }

    @Test
    fun post_fechaInvalida_noTruena() {
        assertNotNull(PostDto(id = 1, publishedAt = "no-es-fecha").toModel())
    }

    @Test
    fun post_editadoMenosDe5Minutos_noMarcaUpdatedAt() {
        val post = PostDto(
            id = 1,
            publishedAt = "2026-03-01T10:00:00Z",
            updatedAt = "2026-03-01T10:04:00Z"
        ).toModel()!!
        assertNull(post.updatedAt)
    }

    @Test
    fun post_editado5MinutosOMas_marcaUpdatedAt() {
        val post = PostDto(
            id = 1,
            publishedAt = "2026-03-01T10:00:00Z",
            updatedAt = "2026-03-01T10:05:00Z"
        ).toModel()!!
        assertEquals(local("2026-03-01T10:05:00Z"), post.updatedAt)
    }

    @Test
    fun post_thumbnailRelativa_seConvierteEnAbsoluta() {
        val post = PostDto(id = 1, thumbnail = MediaDto(url = "/uploads/foto.png")).toModel()!!
        assertEquals("${ApiConfig.HOST}/uploads/foto.png", post.thumbnail)
    }

    @Test
    fun post_thumbnailAbsoluta_seRespeta() {
        val url = "https://cdn.ejemplo.com/foto.png"
        val post = PostDto(id = 1, thumbnail = MediaDto(url = url)).toModel()!!
        assertEquals(url, post.thumbnail)
    }

    @Test
    fun post_thumbnailVacia_esNull() {
        assertNull(PostDto(id = 1, thumbnail = MediaDto(url = "")).toModel()!!.thumbnail)
    }

    @Test
    fun post_tags_seNormalizanAMinusculasSinEspacios() {
        val post = PostDto(
            id = 1,
            audiences = listOf(TagDto(slug = " Becados "), TagDto(slug = "ASPIRANTES"), TagDto(slug = null)),
            levels = listOf(TagDto(slug = "Uni"), TagDto(slug = "uni"))
        ).toModel()!!
        assertEquals(setOf("becados", "aspirantes"), post.audiences)
        assertEquals(setOf("uni"), post.levels)
    }

    // ---------- Adjuntos ----------

    @Test
    fun adjuntoImagen_seMapeaConDimensiones() {
        val post = PostDto(
            id = 1,
            attachments = listOf(
                AttachmentDto(id = 3, type = "image", media = listOf(MediaDto(url = "/img.jpg", width = 800, height = 600)))
            )
        ).toModel()!!

        val img = post.attachments.single() as PostAttachment.Image
        assertEquals("3", img.id)
        assertEquals("${ApiConfig.HOST}/img.jpg", img.url)
        assertEquals(800, img.width)
        assertEquals(600, img.height)
    }

    @Test
    fun adjuntoImagenSinMedia_seDescarta() {
        val post = PostDto(id = 1, attachments = listOf(AttachmentDto(type = "image"))).toModel()!!
        assertTrue(post.attachments.isEmpty())
    }

    @Test
    fun adjuntoVideo_usaUrlExternaSiNoHayMedia() {
        val post = PostDto(
            id = 1,
            attachments = listOf(AttachmentDto(type = "video", url = "https://youtu.be/xyz"))
        ).toModel()!!
        val video = post.attachments.single() as PostAttachment.Video
        assertEquals("https://youtu.be/xyz", video.url)
    }

    @Test
    fun adjuntoLink_conservaTituloYDescripcion() {
        val post = PostDto(
            id = 1,
            attachments = listOf(
                AttachmentDto(type = "link", url = "https://fer.org", title = "FER", description = "Sitio")
            )
        ).toModel()!!
        val link = post.attachments.single() as PostAttachment.Link
        assertEquals("https://fer.org", link.url)
        assertEquals("FER", link.title)
        assertEquals("Sitio", link.description)
    }

    @Test
    fun adjuntoArchivo_convierteKbABytesYUsaMimePorDefecto() {
        val post = PostDto(
            id = 1,
            attachments = listOf(
                AttachmentDto(type = "file", media = listOf(MediaDto(url = "/doc.pdf", name = "doc.pdf", size = 2.0)))
            )
        ).toModel()!!
        val file = post.attachments.single() as PostAttachment.File
        assertEquals("doc.pdf", file.name)
        assertEquals(2048L, file.size)
        assertEquals("application/octet-stream", file.mimeType)
    }

    @Test
    fun adjuntoSinId_usaElIndice() {
        val post = PostDto(
            id = 1,
            attachments = listOf(
                AttachmentDto(type = "link", url = "https://a.com"),
                AttachmentDto(type = "link", url = "https://b.com")
            )
        ).toModel()!!
        assertEquals(listOf("0", "1"), post.attachments.map { (it as PostAttachment.Link).id })
    }

    @Test
    fun adjuntoTipoDesconocido_seDescarta() {
        val post = PostDto(id = 1, attachments = listOf(AttachmentDto(type = "audio", url = "x"))).toModel()!!
        assertTrue(post.attachments.isEmpty())
    }

    // ---------- EventDto.toModel ----------

    private val eventoValido = EventDto(
        id = 10,
        title = "Reunión de becarios",
        dateTime = "2026-05-20T18:00:00Z",
        publishedAt = "2026-05-01T12:00:00Z"
    )

    @Test
    fun evento_valido_seMapea() {
        val event = eventoValido.toModel()!!
        assertEquals(10, event.id)
        assertEquals("Reunión de becarios", event.title)
        assertEquals(local("2026-05-20T18:00:00Z"), event.dateTime)
        assertEquals(local("2026-05-01T12:00:00Z"), event.createdAt)
    }

    @Test
    fun evento_sinId_sinTituloOSinFecha_regresaNull() {
        assertNull(eventoValido.copy(id = null).toModel())
        assertNull(eventoValido.copy(title = null).toModel())
        assertNull(eventoValido.copy(dateTime = null).toModel())
        assertNull(eventoValido.copy(dateTime = "mañana").toModel())
    }

    @Test
    fun evento_camposOpcionales_tienenValoresPorDefecto() {
        val event = eventoValido.copy(address = "  ").toModel()!!
        assertEquals("", event.description)
        assertEquals("", event.location)
        assertEquals("", event.cost)
        assertEquals("", event.registerLink)
        assertEquals("", event.photo)
        assertNull(event.address)
        assertNull(event.endDateTime)
    }

    @Test
    fun evento_fotoRelativa_seConvierteEnAbsoluta() {
        val event = eventoValido.copy(photo = MediaDto(url = "uploads/evento.jpg")).toModel()!!
        assertEquals("${ApiConfig.HOST}/uploads/evento.jpg", event.photo)
    }
}
