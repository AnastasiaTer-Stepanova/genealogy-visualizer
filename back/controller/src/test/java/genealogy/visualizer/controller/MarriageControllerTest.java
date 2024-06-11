package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.EasyMarriage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MarriageControllerTest extends IntegrationTest {

    static void assertMarriage(EasyMarriage marriage1, EasyMarriage marriage2) {
        assertEquals(marriage1.getDate(), marriage2.getDate());
        assertFullName(marriage1.getHusband(), marriage2.getHusband());
        assertFullName(marriage1.getWife(), marriage2.getWife());
        assertEquals(marriage1.getHusbandMarriageNumber(), marriage2.getHusbandMarriageNumber());
        assertEquals(marriage1.getWifeMarriageNumber(), marriage2.getWifeMarriageNumber());
    }

    static void assertMarriage(EasyMarriage marriage1, genealogy.visualizer.entity.Marriage marriage2) {
        assertEquals(marriage1.getDate(), marriage2.getDate());
        assertFullName(marriage1.getHusband(), marriage2.getHusband());
        assertFullName(marriage1.getWife(), marriage2.getWife());
        assertEquals(marriage1.getHusbandMarriageNumber(), marriage2.getHusbandMarriageNumber().intValue());
        assertEquals(marriage1.getWifeMarriageNumber(), marriage2.getWifeMarriageNumber().intValue());
    }
}
