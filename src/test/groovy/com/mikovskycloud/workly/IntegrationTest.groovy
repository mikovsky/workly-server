package com.mikovskycloud.workly


import groovy.json.JsonSlurper
import com.mikovskycloud.workly.repositories.ProjectMemberRepository
import com.mikovskycloud.workly.repositories.ProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Specification

import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IntegrationTest extends Specification {

    static final String DEFAULT_EMAIL = "test@email.com"
    static final String DEFAULT_PASSWORD = "kla3tkf04md1335fa"
    static final String DEFAULT_FIRST_NAME = "John"
    static final String DEFAULT_LAST_NAME = "Doe"

    static final String ANOTHER_EMAIL = "test2@email.com"
    static final String ANOTHER_PASSWORD = "qweasdzxc123dhg"
    static final String ANOTHER_FIRST_NAME = "Louis"
    static final String ANOTHER_LAST_NAME = "Villain"

    static final String DEFAULT_TASK_NAME = "Default Task"
    static final String DEFAULT_TASK_DESCRIPTION = "Default Task Description"
    static final String DEFAULT_TASK_DUE_DATE = LocalDate.now().toString()

    static final String DEFAULT_PROJECT_NAME = "Default Project"

    @Autowired
    protected MockMvc mvc

    @Autowired
    protected com.mikovskycloud.workly.repositories.UserRepository userRepository

    @Autowired
    protected com.mikovskycloud.workly.repositories.TaskRepository taskRepository

    @Autowired
    protected ProjectRepository projectRepository

    @Autowired
    protected ProjectMemberRepository projectMemberRepository;

    void setup() {
        projectMemberRepository.deleteAll()
        projectRepository.deleteAll()
        taskRepository.deleteAll()
        userRepository.deleteAll()
    }

    static def parseBody(response) {
        return new JsonSlurper().parseText(response.contentAsString)
    }

    protected long registerDefaultUser() {
        return registerUser(DEFAULT_EMAIL, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_PASSWORD)
    }

    protected String getDefaultUserToken() {
        return login(DEFAULT_EMAIL, DEFAULT_PASSWORD)
    }

    protected long registerAnotherUser() {
        return registerUser(ANOTHER_EMAIL, ANOTHER_FIRST_NAME, ANOTHER_LAST_NAME, ANOTHER_PASSWORD)
    }

    protected String getAnotherUserToken() {
        return login(ANOTHER_EMAIL, ANOTHER_PASSWORD)
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

    protected long storeTask(String token,
                             String name = DEFAULT_TASK_NAME,
                             String description = DEFAULT_TASK_DESCRIPTION,
                             String dueDate = DEFAULT_TASK_DUE_DATE) {
        def json = """
        {
            "name": "${name}",
            "description": "${description}",
            "dueDate": "${dueDate}"
        }
        """

        def request = MockMvcRequestBuilders.post("/api/tasks")
                .header("Authorization", token)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)

        def response = mvc.perform(request).andReturn().response

        def body = new JsonSlurper().parseText(response.contentAsString)

        return body.id
    }

    protected long storeProject(String token,
                                String name = DEFAULT_PROJECT_NAME) {
        def json = """
        {
            "name": "${name}"
        }
        """

        def request = MockMvcRequestBuilders.post("/api/projects")
                .header("Authorization", token)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)

        def response = mvc.perform(request).andReturn().response

        def body = new JsonSlurper().parseText(response.contentAsString)

        return body.id
    }

    protected void addMemberToProject(String token,
                                      Long projectId,
                                      Long userId) {
        def json = """
        {
            "userId": ${userId}
        }
        """

        def request = MockMvcRequestBuilders.post("/api/projects/${projectId}/members")
                .header("Authorization", token)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)

        mvc.perform(request)
    }

}
