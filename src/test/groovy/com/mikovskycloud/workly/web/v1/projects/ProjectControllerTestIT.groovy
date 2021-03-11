package com.mikovskycloud.workly.web.v1.projects

import com.mikovskycloud.workly.IntegrationTest
import com.mikovskycloud.workly.exceptions.ErrorCode
import com.mikovskycloud.workly.exceptions.WorklyException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Unroll

class ProjectControllerTestIT extends IntegrationTest {

    def "should get all project for user"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken, "Default Project")

        def anotherId = registerAnotherUser()
        def anotherToken = getAnotherUserToken()
        def anotherProjectId = storeProject(anotherToken, "Another Project")

        when:
        def request = MockMvcRequestBuilders.get("/api/projects")
                .header("Authorization", defaultToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == 200

        def body = parseBody(response)
        body.size() == 1
        body[0].id == defaultProjectId
        body[0].name == "Default Project"
        body[0].ownerId == defaultId
        body[0].createdAt != null
        body[0].updatedAt != null
        body[0].createdAt == body[0].updatedAt
    }

    def "should create project for user"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()
        def projectName = "My Project Test"

        when:
        def json = """
        {
            "name": "${projectName}"
        }
        """
        def request = MockMvcRequestBuilders.post("/api/projects")
                .header("Authorization", token)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.OK.value()

        def body = parseBody(response)
        body.id != null
        body.name == projectName
        body.ownerId == id
        body.createdAt != null
        body.updatedAt != null
        body.createdAt == body.updatedAt
    }

    @Unroll
    def "should return error on project create because of invalid payload"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()

        expect:
        def json = """
        {
            "name": ${projectName}
        }
        """
        def request = MockMvcRequestBuilders.post("/api/projects")
                .header("Authorization", token)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        def response = mvc.perform(request).andReturn().response
        response != null
        response.status == responseStatus

        where:
        projectName                   | responseStatus
        null                          | HttpStatus.BAD_REQUEST.value()
        "\"\""                        | HttpStatus.BAD_REQUEST.value()
        "\"A\""                       | HttpStatus.BAD_REQUEST.value()
        "\"${STRING_65_CHARACTERS}\"" | HttpStatus.BAD_REQUEST.value()
    }

    def "should update project for user"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()
        def projectId = storeProject(token)

        def newProjectName = "My Updated Project"

        when:
        def json = """
        {
            "name": "${newProjectName}"
        }
        """
        def request = MockMvcRequestBuilders.put("/api/projects/${projectId}")
                .header("Authorization", token)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.OK.value()

        def body = parseBody(response)
        body.id == projectId
        body.name == newProjectName
        body.ownerId == id
        body.createdAt != null
        body.updatedAt != null
        body.createdAt != body.updatedAt
    }

    def "should return error on project update because project with given ID does not exists"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()

        when:
        def json = """
        {
            "name": "Some new updated project name"
        }
        """
        def request = MockMvcRequestBuilders.put("/api/projects/123123123123")
                .header("Authorization", token)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.NOT_FOUND.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.PROJECT_NOT_FOUND.toString()
        body.errorMessage == ErrorCode.PROJECT_NOT_FOUND.getMessage()
    }

    def "should return error on project update because project owner is a different user"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken, "Default Project")

        def anotherId = registerAnotherUser()
        def anotherToken = getAnotherUserToken()
        def anotherProjectId = storeProject(anotherToken, "Another Project")

        when:
        def json = """
        {
            "name": "Some new updated project name"
        }
        """
        def request = MockMvcRequestBuilders.put("/api/projects/${anotherProjectId}")
                .header("Authorization", defaultToken)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.FORBIDDEN.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.FORBIDDEN.toString()
        body.errorMessage == ErrorCode.FORBIDDEN.getMessage()
    }

    @Unroll
    def "should return error on project update because of invalid payload"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()
        def projectId = storeProject(token)

        expect:
        def json = """
        {
            "name": ${projectName}
        }
        """
        def request = MockMvcRequestBuilders.put("/api/projects/${projectId}")
                .header("Authorization", token)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        def response = mvc.perform(request).andReturn().response
        response != null
        response.status == responseStatus

        where:
        projectName                   | responseStatus
        null                          | HttpStatus.BAD_REQUEST.value()
        "\"\""                        | HttpStatus.BAD_REQUEST.value()
        "\"A\""                       | HttpStatus.BAD_REQUEST.value()
        "\"${STRING_65_CHARACTERS}\"" | HttpStatus.BAD_REQUEST.value()
    }

    def "should delete project for user"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()
        def projectId = storeProject(token)

        when:
        def request = MockMvcRequestBuilders.delete("/api/projects/${projectId}")
                .header("Authorization", token)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.NO_CONTENT.value()
        response.contentAsString == ""
    }

    def "should return error on project delete because project with given ID does not exists"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()

        when:
        def request = MockMvcRequestBuilders.delete("/api/projects/123123123123")
                .header("Authorization", token)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.NOT_FOUND.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.PROJECT_NOT_FOUND.toString()
        body.errorMessage == ErrorCode.PROJECT_NOT_FOUND.getMessage()
    }

    def "should return error on project delete because project owner is a different user"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken, "Default Project")

        def anotherId = registerAnotherUser()
        def anotherToken = getAnotherUserToken()
        def anotherProjectId = storeProject(anotherToken, "Another Project")

        when:
        def request = MockMvcRequestBuilders.delete("/api/projects/${anotherProjectId}")
                .header("Authorization", defaultToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.FORBIDDEN.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.FORBIDDEN.toString()
        body.errorMessage == ErrorCode.FORBIDDEN.getMessage()
    }

    def "should get all members of the project"() {
        given:
        def userId1 = registerUser("user1@email.com", "Jack", "Sparrow", "qweasd123zxc")
        def userId2 = registerUser("user2@email.com", "Will", "Turner", "asdzxcqwerty")

        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)

        addMemberToProject(defaultToken, defaultProjectId, "user1@email.com")
        addMemberToProject(defaultToken, defaultProjectId, "user2@email.com")

        when:
        def request = MockMvcRequestBuilders.get("/api/projects/${defaultProjectId}/members")
                .header("Authorization", defaultToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.OK.value()

        def body = parseBody(response) as List<?>
        body.size() == 3

        def owner = findById(body, defaultId)
        owner.firstName == DEFAULT_FIRST_NAME
        owner.lastName == DEFAULT_LAST_NAME
        owner.jobTitle == null

        def user1 = findById(body, userId1)
        user1.firstName == "Jack"
        user1.lastName == "Sparrow"
        user1.jobTitle == null

        def user2 = findById(body, userId2)
        user2.firstName == "Will"
        user2.lastName == "Turner"
        user2.jobTitle == null
    }

    def "should return error on get all members of the project because project does not exists"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()

        when:
        def request = MockMvcRequestBuilders.get("/api/projects/123123123123/members")
                .header("Authorization", defaultToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.NOT_FOUND.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.PROJECT_NOT_FOUND.toString()
        body.errorMessage == ErrorCode.PROJECT_NOT_FOUND.getMessage()
    }

    def "should return error on get all members of the project because user is not a member of the project"() {
        given:
        def userId1 = registerUser("user1@email.com", "Jack", "Sparrow", "qweasd123zxc")
        def userId2 = registerUser("user2@email.com", "Will", "Turner", "asdzxcqwerty")

        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)

        addMemberToProject(defaultToken, defaultProjectId, "user1@email.com")
        addMemberToProject(defaultToken, defaultProjectId, "user2@email.com")

        def anotherId = registerAnotherUser()
        def anotherToken = getAnotherUserToken()

        when:
        def request = MockMvcRequestBuilders.get("/api/projects/${defaultProjectId}/members")
                .header("Authorization", anotherToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.FORBIDDEN.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.FORBIDDEN.toString()
        body.errorMessage == ErrorCode.FORBIDDEN.getMessage()
    }

    def "should add member to the project"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)

        def anotherId = registerAnotherUser()

        when:
        def json = """
        {
            "email": "${ANOTHER_EMAIL}"
        }
        """
        def request = MockMvcRequestBuilders.post("/api/projects/${defaultProjectId}/members")
                .header("Authorization", defaultToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.OK.value()

        def body = parseBody(response)
        body.id == anotherId
        body.firstName == ANOTHER_FIRST_NAME
        body.lastName == ANOTHER_LAST_NAME
        body.jobTitle == null
    }

    def "should return error on add member to the project because project does not exists"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()

        def anotherId = registerAnotherUser()

        when:
        def json = """
        {
            "email": "${ANOTHER_EMAIL}"
        }
        """
        def request = MockMvcRequestBuilders.post("/api/projects/123123123123/members")
                .header("Authorization", defaultToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.NOT_FOUND.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.PROJECT_NOT_FOUND.toString()
        body.errorMessage == ErrorCode.PROJECT_NOT_FOUND.getMessage()
    }

    def "should return error on add member to the project because user is not project owner"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)

        def anotherId = registerAnotherUser()
        def anotherToken = getAnotherUserToken()

        when:
        def json = """
        {
            "email": "${ANOTHER_EMAIL}"
        }
        """
        def request = MockMvcRequestBuilders.post("/api/projects/${defaultProjectId}/members")
                .header("Authorization", anotherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.FORBIDDEN.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.FORBIDDEN.toString()
        body.errorMessage == ErrorCode.FORBIDDEN.getMessage()
    }

    def "should return error on add member to the project because member to add does not exists"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)

        when:
        def json = """
        {
            "email": "somenotexistinguser@email.com"
        }
        """
        def request = MockMvcRequestBuilders.post("/api/projects/${defaultProjectId}/members")
                .header("Authorization", defaultToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.NOT_FOUND.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.USER_NOT_FOUND.toString()
        body.errorMessage == ErrorCode.USER_NOT_FOUND.getMessage()
    }

    @Unroll
    def "should return error on add member to the project because of invalid payload"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)

        expect:
        def json = """
        {
            "email": ${email}
        }
        """
        def request = MockMvcRequestBuilders.post("/api/projects/${defaultProjectId}/members")
                .header("Authorization", defaultToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response
        response != null
        response.status == responseStatus

        where:
        email                                   | responseStatus
        null                                    | HttpStatus.BAD_REQUEST.value()
        "\"notvalidemail\""                     | HttpStatus.BAD_REQUEST.value()
        "\"a@b.c\""                             | HttpStatus.BAD_REQUEST.value()
        "\"${STRING_65_CHARACTERS}@email.com\"" | HttpStatus.BAD_REQUEST.value()
    }

    def "should delete member from the project"() {
        given:
        def userId1 = registerUser("user1@email.com", "Jack", "Sparrow", "qweasd123zxc")
        def userId2 = registerUser("user2@email.com", "Will", "Turner", "asdzxcqwerty")

        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)

        addMemberToProject(defaultToken, defaultProjectId, "user1@email.com")
        addMemberToProject(defaultToken, defaultProjectId, "user2@email.com")

        when:
        def request = MockMvcRequestBuilders.delete("/api/projects/${defaultProjectId}/members/${userId2}")
                .header("Authorization", defaultToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.NO_CONTENT.value()

        when:
        request = MockMvcRequestBuilders.get("/api/projects/${defaultProjectId}/members")
                .header("Authorization", defaultToken)
        response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.OK.value()

        def body = parseBody(response) as List<?>
        body.size() == 2

        def owner = findById(body, defaultId)
        owner.firstName == DEFAULT_FIRST_NAME
        owner.lastName == DEFAULT_LAST_NAME
        owner.jobTitle == null

        def user1 = findById(body, userId1)
        user1.firstName == "Jack"
        user1.lastName == "Sparrow"
        user1.jobTitle == null
    }

    def "should return error on delete member from the project because project does not exists"() {
        given:
        def userId1 = registerUser("user1@email.com", "Jack", "Sparrow", "qweasd123zxc")

        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()

        when:
        def request = MockMvcRequestBuilders.delete("/api/projects/123123123123/members/${userId1}")
                .header("Authorization", defaultToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.NOT_FOUND.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.PROJECT_NOT_FOUND.toString()
        body.errorMessage == ErrorCode.PROJECT_NOT_FOUND.getMessage()
    }

    def "should return error on delete member from the project because user is not project owner"() {
        given:
        def userId1 = registerUser("user1@email.com", "Jack", "Sparrow", "qweasd123zxc")
        def userId2 = registerUser("user2@email.com", "Will", "Turner", "asdzxcqwerty")

        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)

        def anotherId = registerAnotherUser()
        def anotherToken = getAnotherUserToken()

        addMemberToProject(defaultToken, defaultProjectId, "user1@email.com")
        addMemberToProject(defaultToken, defaultProjectId, "user2@email.com")
        addMemberToProject(defaultToken, defaultProjectId, ANOTHER_EMAIL)

        when:
        def request = MockMvcRequestBuilders.delete("/api/projects/${defaultProjectId}/members/${userId1}")
                .header("Authorization", anotherToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.FORBIDDEN.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.FORBIDDEN.toString()
        body.errorMessage == ErrorCode.FORBIDDEN.getMessage()
    }

    def "should return error on delete member from the project because member to remove does not exists"() {
        given:
        def userId1 = registerUser("user1@email.com", "Jack", "Sparrow", "qweasd123zxc")

        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)

        addMemberToProject(defaultToken, defaultProjectId, "user1@email.com")

        when:
        def request = MockMvcRequestBuilders.delete("/api/projects/${defaultProjectId}/members/123123123123")
                .header("Authorization", defaultToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.NOT_FOUND.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.USER_NOT_FOUND.toString()
        body.errorMessage == ErrorCode.USER_NOT_FOUND.getMessage()
    }

    def "should return error on delete member from the project because member to remove is not a member of the project"() {
        given:
        def userId1 = registerUser("user1@email.com", "Jack", "Sparrow", "qweasd123zxc")
        def userId2 = registerUser("user2@email.com", "Will", "Turner", "asdzxcqwerty")

        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)

        addMemberToProject(defaultToken, defaultProjectId, "user1@email.com")

        when:
        def request = MockMvcRequestBuilders.delete("/api/projects/${defaultProjectId}/members/${userId2}")
                .header("Authorization", defaultToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.FORBIDDEN.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.FORBIDDEN.toString()
        body.errorMessage == ErrorCode.FORBIDDEN.getMessage()
    }

    static def findById(members, id) {
        return members.stream()
                .filter({ it -> it.id == id })
                .findFirst()
                .orElseThrow({ WorklyException.userNotFound() })
    }

}
