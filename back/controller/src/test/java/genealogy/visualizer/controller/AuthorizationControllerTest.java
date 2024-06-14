package genealogy.visualizer.controller;

import genealogy.visualizer.api.model.ErrorResponse;
import genealogy.visualizer.api.model.User;
import genealogy.visualizer.model.exception.ForbiddenException;
import genealogy.visualizer.service.ParamDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthorizationControllerTest extends IntegrationTest {

    private static final String AUTHORIZATION_PATH = "/authorization";
    private static final String REGISTRATION_PATH = "/registration";
    private static final String REGISTRATION_ENABLE_PARAM_NAME = "registration_enable";

    @Autowired
    ParamDAO paramDAO;

    @Test
    void authorizationTest() throws Exception {
        authorization();
    }

    @Test
    void authorizationForbiddenTest() throws Exception {
        User user = generator.nextObject(User.class);
        String responseJson = mockMvc.perform(
                        post(AUTHORIZATION_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                .andExpectAll(
                        status().isForbidden(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse response = objectMapper.readValue(responseJson, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(response.getMessage(), ForbiddenException.MESSAGE);
        assertEquals(response.getCode(), HttpStatus.FORBIDDEN.value());
    }

    @Test
    void registrationTest() throws Exception {
        paramDAO.updateValueByName(REGISTRATION_ENABLE_PARAM_NAME, Boolean.TRUE.toString());
        User user = generator.nextObject(User.class);
        String responseJson = mockMvc.perform(
                        post(REGISTRATION_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                .andExpectAll(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertEquals(responseJson, "");
        assertNotNull(userRepository.findByLogin(user.getLogin()));
    }

    @Test
    void registrationForbiddenTest() throws Exception {
        paramDAO.updateValueByName(REGISTRATION_ENABLE_PARAM_NAME, Boolean.TRUE.toString());
        User user = generator.nextObject(User.class);
        user.setLogin(userExisting.getLogin());
        String responseJson = mockMvc.perform(
                        post(REGISTRATION_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                .andExpectAll(
                        status().isForbidden(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse response = objectMapper.readValue(responseJson, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(response.getMessage(), String.format("Пользователь с логином: %s уже существует", user.getLogin()));
        assertEquals(response.getCode(), HttpStatus.FORBIDDEN.value());
    }

    @Test
    void registrationForbiddenParamTest() throws Exception {
        paramDAO.updateValueByName(REGISTRATION_ENABLE_PARAM_NAME, Boolean.FALSE.toString());
        User user = generator.nextObject(User.class);
        user.setLogin(userExisting.getLogin());
        String responseJson = mockMvc.perform(
                        post(REGISTRATION_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                .andExpectAll(
                        status().isForbidden(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        ErrorResponse response = objectMapper.readValue(responseJson, ErrorResponse.class);
        assertNotNull(response);
        assertEquals(response.getMessage(), "Регистрация в данный момент недоступна.");
        assertEquals(response.getCode(), HttpStatus.FORBIDDEN.value());
    }

    @AfterEach
    void tearDown() {
        System.out.println("----------------------End test------------------------");
        userRepository.deleteAll();
        super.tearDown();
    }

}