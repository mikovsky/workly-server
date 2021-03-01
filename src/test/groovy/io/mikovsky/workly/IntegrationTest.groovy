package io.mikovsky.workly

import groovy.json.JsonSlurper
import io.mikovsky.workly.repositories.ProjectRepository
import io.mikovsky.workly.repositories.TaskRepository
import io.mikovsky.workly.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Specification

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IntegrationTest extends Specification {

    static final String DEFAULT_EMAIL = "test@email.com"
    static final String DEFAULT_PASSWORD = "kla3tkf04md1335fa"
    static final String DEFAULT_FIRST_NAME = "John"
    static final String DEFAULT_LAST_NAME = "Doe"

    @Autowired
    protected MockMvc mvc

    @Autowired
    protected UserRepository userRepository

    @Autowired
    protected TaskRepository taskRepository

    @Autowired
    protected ProjectRepository projectRepository

    void setup() {
        projectRepository.deleteAll()
        taskRepository.deleteAll()
        userRepository.deleteAll()
    }

    protected long registerDefaultUser() {
        return registerUser(DEFAULT_EMAIL, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_PASSWORD)
    }

    protected String getDefaultUserToken() {
        return login(DEFAULT_EMAIL, DEFAULT_PASSWORD)
    }

    protected long registerUser(String email, String firstName, String lastName, String password) {
        def json = """
        {
            "email": "${email}",
            "firstName": "${firstName}",
            "lastName": "${lastName}",
            "password": "${password}"
        }
        """
        def request = MockMvcRequestBuilders.post("/api/auth/register")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)

        def response = mvc.perform(request).andReturn().response

        def body = new JsonSlurper().parseText(response.contentAsString)

        return body.id
    }

    protected String login(String email, String password) {
        def json = """
        {
            "email": "${email}",
            "password": "${password}"
        }
        """

        def request = MockMvcRequestBuilders.post("/api/auth/login")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)

        def response = mvc.perform(request).andReturn().response

        def body = new JsonSlurper().parseText(response.contentAsString)

        return body.token
    }

}
