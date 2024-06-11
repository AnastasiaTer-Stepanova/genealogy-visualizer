package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.EasyChristening;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ChristeningControllerTest extends IntegrationTest {

    static void assertChristening(EasyChristening christening1, EasyChristening christening2) {
        assertNotNull(christening1);
        assertNotNull(christening2);
        assertEquals(christening1.getName(), christening2.getName());
        assertEquals(christening1.getChristeningDate(), christening2.getChristeningDate());
        assertEquals(christening1.getBirthDate(), christening2.getBirthDate());
        assertEquals(christening1.getSex(), christening2.getSex());
        assertEquals(christening1.getLegitimacy(), christening2.getLegitimacy());
    }

    static void assertChristening(EasyChristening christening1, genealogy.visualizer.entity.Christening christening2) {
        assertNotNull(christening1);
        assertNotNull(christening2);
        assertEquals(christening1.getName(), christening2.getName());
        assertEquals(christening1.getChristeningDate(), christening2.getChristeningDate());
        assertEquals(christening1.getBirthDate(), christening2.getBirthDate());
        assertEquals(christening1.getSex().name(), christening2.getSex().name());
        assertEquals(christening1.getLegitimacy(), christening2.getLegitimacy());
    }
}
