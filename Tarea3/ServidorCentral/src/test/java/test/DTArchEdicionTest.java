package test;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import logica.datatypes.DTArchEdicion;
import logica.utils.LocalDateAdapter;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DTArchEdicionTest {

    @Test
    void constructorCompleto_yGettersDevuelvenValores() {
        LocalDate fi = LocalDate.of(2025, 11, 10);
        LocalDate ff = LocalDate.of(2025, 11, 30);
        LocalDate fa = LocalDate.of(2025, 12, 1);

        DTArchEdicion dto = new DTArchEdicion(
                "Evento X",
                "ED25",
                fi,
                ff,
                fa,
                "orgNick"
        );

        assertEquals("Evento X", dto.getNombreEvento());
        assertEquals("ED25", dto.getSiglaEdicion());
        assertEquals(fi, dto.getFechaInicio());
        assertEquals(ff, dto.getFechaFin());
        assertEquals(fa, dto.getFechaArchivado());
        assertEquals("orgNick", dto.getOrganizadorNick());
    }

    @Test
    void constructorSinFechaArchivado_dejaFechaArchivadoNull() {
        LocalDate fi = LocalDate.of(2025, 6, 1);
        LocalDate ff = LocalDate.of(2025, 6, 2);

        DTArchEdicion dto = new DTArchEdicion(
                "Evento Y",
                "ED26",
                fi,
                ff,
                "org2"
        );

        assertEquals("Evento Y", dto.getNombreEvento());
        assertEquals("ED26", dto.getSiglaEdicion());
        assertEquals(fi, dto.getFechaInicio());
        assertEquals(ff, dto.getFechaFin());
        assertNull(dto.getFechaArchivado());
        assertEquals("org2", dto.getOrganizadorNick());
    }

    @Test
    void settersActualizanCamposCorrectamente() {
        DTArchEdicion dto = new DTArchEdicion();

        dto.setNombreEvento("EV");
        dto.setSiglaEdicion("SIG");
        dto.setFechaInicio(LocalDate.of(2024, 1, 2));
        dto.setFechaFin(LocalDate.of(2024, 1, 3));
        dto.setFechaArchivado(LocalDate.of(2024, 2, 1));
        dto.setOrganizadorNick("org");

        assertEquals("EV", dto.getNombreEvento());
        assertEquals("SIG", dto.getSiglaEdicion());
        assertEquals(LocalDate.of(2024, 1, 2), dto.getFechaInicio());
        assertEquals(LocalDate.of(2024, 1, 3), dto.getFechaFin());
        assertEquals(LocalDate.of(2024, 2, 1), dto.getFechaArchivado());
        assertEquals("org", dto.getOrganizadorNick());
    }

    @Test
    void anotacionesJaxb_presentesYCorrectas() throws Exception {
        // Clase: @XmlAccessorType(XmlAccessType.FIELD)
        XmlAccessorType accessor = DTArchEdicion.class.getAnnotation(XmlAccessorType.class);
        assertNotNull(accessor, "@XmlAccessorType faltante");
        assertEquals(XmlAccessType.FIELD, accessor.value(), "XmlAccessType debe ser FIELD");

        // Clase: @XmlType(name="DTArchEdicion", propOrder=...)
        XmlType xmlType = DTArchEdicion.class.getAnnotation(XmlType.class);
        assertNotNull(xmlType, "@XmlType faltante");
        assertEquals("DTArchEdicion", xmlType.name());
        String[] esperado = {
                "nombreEvento",
                "siglaEdicion",
                "fechaInicio",
                "fechaFin",
                "fechaArchivado",
                "organizadorNick"
        };
        assertArrayEquals(esperado, xmlType.propOrder(), "propOrder debe coincidir exactamente");

        // Campos fecha tienen @XmlJavaTypeAdapter(LocalDateAdapter.class)
        assertTieneAdapterLocalDate("fechaInicio");
        assertTieneAdapterLocalDate("fechaFin");
        assertTieneAdapterLocalDate("fechaArchivado");
    }

    private static void assertTieneAdapterLocalDate(String fieldName) throws NoSuchFieldException {
        Field f = DTArchEdicion.class.getDeclaredField(fieldName);
        XmlJavaTypeAdapter ann = f.getAnnotation(XmlJavaTypeAdapter.class);
        assertNotNull(ann, () -> "@XmlJavaTypeAdapter faltante en " + fieldName);
        assertEquals(LocalDateAdapter.class, ann.value(), () -> "Adapter incorrecto en " + fieldName);
    }

  
}