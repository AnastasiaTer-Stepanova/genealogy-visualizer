package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.Christening;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ChristeningControllerTest extends IntegrationTest {

    static void assertChristening(Christening christening1, Christening christening2) {
        assertNotNull(christening1);
        assertNotNull(christening2);
        assertEquals(christening1.getName(), christening2.getName());
        assertEquals(christening1.getChristeningDate(), christening2.getChristeningDate());
        assertEquals(christening1.getBirthDate(), christening2.getBirthDate());
        assertEquals(christening1.getSex(), christening2.getSex());
        assertEquals(christening1.getLegitimacy(), christening2.getLegitimacy());
    }
}