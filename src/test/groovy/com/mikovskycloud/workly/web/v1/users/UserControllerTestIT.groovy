package com.mikovskycloud.workly.web.v1.users


import com.mikovskycloud.workly.IntegrationTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Unroll

class UserControllerTestIT extends IntegrationTest {

    def "should get information about same user"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()

        when:
        def request = MockMvcRequestBuilders.get("/api/users/${id}")
                .header("Authorization", token)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.OK.value()

        def body = parseBody(response)
        body.id == id
        body.email == DEFAULT_EMAIL
        body.firstName == DEFAULT_FIRST_NAME
        body.lastName == DEFAULT_LAST_NAME
        body.jobTitle == null
    }

    def "should get information about other user"() {
        given:
        def defaultId = registerDefaultUser()

        def anotherId = registerAnotherUser()
        def anotherToken = getAnotherUserToken()

        when:
        def request = MockMvcRequestBuilders.get("/api/users/${defaultId}")
                .header("Authorization", anotherToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.OK.value()

        def body = parseBody(response)
        body.id == defaultId
        body.email == DEFAULT_EMAIL
        body.firstName == DEFAULT_FIRST_NAME
        body.lastName == DEFAULT_LAST_NAME
        body.jobTitle == null
    }

    def "should update user information"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()
        def newEmail = "mdudek@email.com"
        def newFirstName = "Michal"
        def newLastName = "Dudek"
        def newJobTitle = "Software Engineer"

        when:
        def json = """
        {
            "email": "${newEmail}",
            "firstName": "${newFirstName}",
            "lastName": "${newLastName}",
            "jobTitle": "${newJobTitle}"
        }
        """
        def request = MockMvcRequestBuilders.put("/api/users")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.OK.value()

        def body = parseBody(response)
        body.id == id
        body.email != DEFAULT_EMAIL
        body.firstName != DEFAULT_FIRST_NAME
        body.lastName != DEFAULT_LAST_NAME
        body.jobTitle != null
        body.email == newEmail
        body.firstName == newFirstName
        body.lastName == newLastName
        body.jobTitle == newJobTitle
    }

    def "should return error on update user information when email already exists"() {
        given:
        registerUser("mdudek@email.com", "abcdef", "ghijkl", "mnopqrstu")
        registerDefaultUser()
        def token = getDefaultUserToken()
        def newEmail = "mdudek@email.com"
        def newFirstName = "Michal"
        def newLastName = "Dudek"
        def newJobTitle = "Software Engineer"

        when:
        def json = """
        {
            "email": "${newEmail}",
            "firstName": "${newFirstName}",
            "lastName": "${newLastName}",
            "jobTitle": "${newJobTitle}"
        }
        """
        def request = MockMvcRequestBuilders.put("/api/users")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.BAD_REQUEST.value()

        def body = parseBody(response)
        body.errorCode == com.mikovskycloud.workly.exceptions.ErrorCode.EMAIL_ALREADY_EXISTS.toString()
        body.errorMessage == com.mikovskycloud.workly.exceptions.ErrorCode.EMAIL_ALREADY_EXISTS.getMessage()
    }

    @Unroll
    def "should return error on update user information when payload is invalid"() {
        given:
        registerDefaultUser()
        def token = getDefaultUserToken()

        expect:
        def json = """
        {
            "email": ${email},
            "firstName": ${firstName},
            "lastName": ${lastName}
        }
        """
        def request = MockMvcRequestBuilders.put("/api/users")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response
        response != null
        response.status == HttpStatus.BAD_REQUEST.value()

        where:
        email                  | firstName    | lastName
        null                   | "\"Michal\"" | "\"Dudek\""
        "\"\""                 | "\"Michal\"" | "\"Dudek\""
        "\"notvalidemail\""    | "\"Michal\"" | "\"Dudek\""
        "\"mdudek@email.com\"" | null         | "\"Dudek\""
        "\"mdudek@email.com\"" | "\"\""       | "\"Dudek\""
        "\"mdudek@email.com\"" | "\"M\""      | "\"Dudek\""
        "\"mdudek@email.com\"" | "\"Michal\"" | null
        "\"mdudek@email.com\"" | "\"Michal\"" | "\"\""
        "\"mdudek@email.com\"" | "\"Michal\"" | "\"D\""
    }

    def "should update password"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()
        def newPassword = "qwertyuiopasdfghjjkll"

        when:
        def json = """
        {
            "currentPassword": "${DEFAULT_PASSWORD}",
            "newPassword": "${newPassword}"
        }
        """
        def request = MockMvcRequestBuilders.put("/api/users/password")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.OK.value()

        def body = parseBody(response)
        body.id == id
        body.email == DEFAULT_EMAIL
        body.firstName == DEFAULT_FIRST_NAME
        body.lastName == DEFAULT_LAST_NAME
        body.jobTitle == null

        when:
        json = """
        {
            "email": "${DEFAULT_EMAIL}",
            "password": "${DEFAULT_PASSWORD}"
        }
        """
        request = MockMvcRequestBuilders.post("/api/auth/login")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.UNAUTHORIZED.value()

        def body2 = parseBody(response)
        body2.errorCode == com.mikovskycloud.workly.exceptions.ErrorCode.UNAUTHORIZED.toString()
        body2.errorMessage == com.mikovskycloud.workly.exceptions.ErrorCode.UNAUTHORIZED.getMessage()

        when:
        json = """
        {
            "email": "${DEFAULT_EMAIL}",
            "password": "${newPassword}"
        }
        """
        request = MockMvcRequestBuilders.post("/api/auth/login")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.OK.value()

        def body3 = parseBody(response)
        body3.success == true
        body3.token != null
        body3.token != ""
        body3.token.contains("Bearer ")
    }

    @Unroll
    def "should return error on password update when payload is invalid"() {
        given:
        registerDefaultUser()
        def token = getDefaultUserToken()

        expect:
        def json = """
        {
            "currentPassword": ${currentPassword},
            "newPassword": ${newPassword}
        }
        """
        def request = MockMvcRequestBuilders.put("/api/users/password")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response
        response != null
        response.status == HttpStatus.BAD_REQUEST.value()

        where:
        currentPassword           | newPassword
        "\"${DEFAULT_PASSWORD}\"" | null
        "\"${DEFAULT_PASSWORD}\"" | "\"\""
        "\"${DEFAULT_PASSWORD}\"" | "\"abcdefg\""
        null                      | "\"validnewpassword123\""
        "\"\""                    | "\"validnewpassword123\""
        "\"abcdefg\""             | "\"validnewpassword123\""
        "\"notcurrentpassword\""  | "\"validnewpassword123\""
    }

}
