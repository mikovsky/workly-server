package com.mikovskycloud.workly.web.v1.sections

import com.mikovskycloud.workly.IntegrationTest
import com.mikovskycloud.workly.exceptions.ErrorCode
import com.mikovskycloud.workly.exceptions.WorklyException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Unroll

class SectionControllerTestIT extends IntegrationTest {

    def "should get all sections from project"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)
        def defaultSectionId1 = storeSection(defaultToken, defaultProjectId, "Section 1")
        def defaultSectionId2 = storeSection(defaultToken, defaultProjectId, "Section 2")

        def anotherId = registerAnotherUser()
        def anotherToken = getAnotherUserToken()
        def anotherProjectId = storeProject(anotherToken)
        def anotherSectionId1 = storeSection(anotherToken, anotherProjectId, "Another Section 1")
        def anotherSectionId2 = storeSection(anotherToken, anotherProjectId, "Another Section 2")

        when:
        def request = MockMvcRequestBuilders.get("/api/projects/${defaultProjectId}/sections")
                .header("Authorization", defaultToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.OK.value()

        def body = parseBody(response) as List<?>
        body.size() == 2

        def section1 = findById(body, defaultSectionId1)
        section1.id == defaultSectionId1
        section1.name == "Section 1"

        def section2 = findById(body, defaultSectionId2)
        section2.id == defaultSectionId2
        section2.name == "Section 2"
    }

    def "should return error on get all section from project because project does not exists"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()

        when:
        def request = MockMvcRequestBuilders.get("/api/projects/123123123123/sections")
                .header("Authorization", defaultToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.NOT_FOUND.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.PROJECT_NOT_FOUND.toString()
        body.errorMessage == ErrorCode.PROJECT_NOT_FOUND.getMessage()
    }

    def "should return error on get all section from project because user is not a member of the project"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)
        def defaultSectionId1 = storeSection(defaultToken, defaultProjectId, "Section 1")
        def defaultSectionId2 = storeSection(defaultToken, defaultProjectId, "Section 2")

        def anotherId = registerAnotherUser()
        def anotherToken = getAnotherUserToken()

        when:
        def request = MockMvcRequestBuilders.get("/api/projects/${defaultProjectId}/sections")
                .header("Authorization", anotherToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.FORBIDDEN.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.FORBIDDEN.toString()
        body.errorMessage == ErrorCode.FORBIDDEN.getMessage()
    }

    def "should add section to the project"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()
        def projectId = storeProject(token)
        def sectionName = "Section 123"

        when:
        def json = """
        {
            "name": "${sectionName}"
        }
        """
        def request = MockMvcRequestBuilders.post("/api/projects/${projectId}/sections")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.OK.value()

        def body = parseBody(response)
        body.id != null
        body.name == sectionName
    }

    def "should return error on add section to the project because project does not exists"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()
        def sectionName = "Section 123"

        when:
        def json = """
        {
            "name": "${sectionName}"
        }
        """
        def request = MockMvcRequestBuilders.post("/api/projects/123123123123/sections")
                .header("Authorization", token)
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

    def "should return error on add section to the project because user is not a member of the project"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)

        def anotherId = registerAnotherUser()
        def anotherToken = getAnotherUserToken()

        when:
        def json = """
        {
            "name": "Section Name 1"
        }
        """
        def request = MockMvcRequestBuilders.post("/api/projects/${defaultProjectId}/sections")
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

    @Unroll
    def "should return error on add section to the project because of invalid payload"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()
        def projectId = storeProject(token)

        expect:
        def json = """
        {
            "name": ${sectionName}
        }
        """
        def request = MockMvcRequestBuilders.post("/api/projects/${projectId}/sections")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response
        response != null
        response.status == responseStatus

        where:
        sectionName                   | responseStatus
        null                          | HttpStatus.BAD_REQUEST.value()
        "\"\""                        | HttpStatus.BAD_REQUEST.value()
        "\"A\""                       | HttpStatus.BAD_REQUEST.value()
        "\"${STRING_33_CHARACTERS}\"" | HttpStatus.BAD_REQUEST.value()
    }

    def "should update section from the project"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()
        def projectId = storeProject(token)
        def sectionId = storeSection(token, projectId, "Section 1")
        def newSectionName = "Updated Section 1"

        when:
        def json = """
        {
            "name": "${newSectionName}"
        }
        """
        def request = MockMvcRequestBuilders.put("/api/projects/${projectId}/sections/${sectionId}")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.OK.value()

        def body = parseBody(response)
        body.id == sectionId
        body.name == newSectionName
    }

    def "should return error on update section from the project project does not exists"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()
        def projectId = storeProject(token)
        def sectionId = storeSection(token, projectId, "Section 1")

        when:
        def json = """
        {
            "name": "Updated Section Name"
        }
        """
        def request = MockMvcRequestBuilders.put("/api/projects/123123123123/sections/${sectionId}")
                .header("Authorization", token)
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

    def "should return error on update section from the project user is not a member of the project"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)
        def defaultSectionId = storeSection(defaultToken, defaultProjectId)

        def anotherId = registerAnotherUser()
        def anotherToken = getAnotherUserToken()

        when:
        def json = """
        {
            "name": "Updated Section Name"
        }
        """
        def request = MockMvcRequestBuilders.put("/api/projects/${defaultProjectId}/sections/${defaultSectionId}")
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

    def "should return error on update section from the project section with given id does not exists in this project"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()
        def projectId = storeProject(token)
        def sectionId = storeSection(token, projectId, "Section 1")

        when:
        def json = """
        {
            "name": "Section 1"
        }
        """
        def request = MockMvcRequestBuilders.put("/api/projects/${projectId}/sections/123123123123")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.NOT_FOUND.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.SECTION_NOT_FOUND.toString()
        body.errorMessage == ErrorCode.SECTION_NOT_FOUND.getMessage()
    }

    @Unroll
    def "should return error on update section from the project of invalid payload"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()
        def projectId = storeProject(token)
        def sectionId = storeSection(token, projectId)

        expect:
        def json = """
        {
            "name": ${sectionName}
        }
        """
        def request = MockMvcRequestBuilders.put("/api/projects/${projectId}/sections/${sectionId}")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response
        response != null
        response.status == responseStatus

        where:
        sectionName                   | responseStatus
        null                          | HttpStatus.BAD_REQUEST.value()
        "\"\""                        | HttpStatus.BAD_REQUEST.value()
        "\"A\""                       | HttpStatus.BAD_REQUEST.value()
        "\"${STRING_33_CHARACTERS}\"" | HttpStatus.BAD_REQUEST.value()
    }

    def "should delete section from the project"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()
        def projectId = storeProject(token)
        def sectionId = storeSection(token, projectId)

        when:
        def request = MockMvcRequestBuilders.delete("/api/projects/${projectId}/sections/${sectionId}")
                .header("Authorization", token)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.NO_CONTENT.value()
    }

    def "should return error on delete section from the project project does not exists"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()
        def projectId = storeProject(token)
        def sectionId = storeSection(token, projectId)

        when:
        def request = MockMvcRequestBuilders.delete("/api/projects/123123123123/sections/${sectionId}")
                .header("Authorization", token)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.NOT_FOUND.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.PROJECT_NOT_FOUND.toString()
        body.errorMessage == ErrorCode.PROJECT_NOT_FOUND.getMessage()
    }

    def "should return error on delete section from the project user is not a member of the project"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)
        def defaultSectionId = storeSection(defaultToken, defaultProjectId)

        def anotherId = registerAnotherUser()
        def anotherToken = getAnotherUserToken()

        when:
        def request = MockMvcRequestBuilders.delete("/api/projects/${defaultProjectId}/sections/${defaultSectionId}")
                .header("Authorization", anotherToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.FORBIDDEN.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.FORBIDDEN.toString()
        body.errorMessage == ErrorCode.FORBIDDEN.getMessage()
    }

    def "should return error on delete section from the project section with given id does not exists"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()
        def projectId = storeProject(token)
        def sectionId = storeSection(token, projectId)

        when:
        def request = MockMvcRequestBuilders.delete("/api/projects/${projectId}/sections/123123123123")
                .header("Authorization", token)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.NOT_FOUND.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.SECTION_NOT_FOUND.toString()
        body.errorMessage == ErrorCode.SECTION_NOT_FOUND.getMessage()
    }

    static def findById(sections, id) {
        return sections.stream()
                .filter({ it -> it.id == id })
                .findFirst()
                .orElseThrow({ WorklyException.sectionNotFound() })
    }

}
