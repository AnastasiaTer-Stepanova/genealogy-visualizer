package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.Death;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeathControllerTest extends IntegrationTest {

    static void assertDeath(Death death1, Death death2) {
        assertEquals(death1.getDate(), death2.getDate());
        assertFullName(death1.getFullName(), death2.getFullName());
    }
}
