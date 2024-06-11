package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.EasyDeath;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeathControllerTest extends IntegrationTest {

    static void assertDeath(EasyDeath death1, EasyDeath death2) {
        assertEquals(death1.getDate(), death2.getDate());
        assertFullName(death1.getFullName(), death2.getFullName());
    }

    static void assertDeath(EasyDeath death1, genealogy.visualizer.entity.Death death2) {
        assertEquals(death1.getDate(), death2.getDate());
        assertFullName(death1.getFullName(), death2.getFullName());
    }
}
