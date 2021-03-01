package io.mikovsky.workly.web.v1.users

import groovy.json.JsonSlurper
import io.mikovsky.workly.IntegrationTest
import io.mikovsky.workly.exceptions.ErrorCode
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Unroll

class UserControllerTestIT extends IntegrationTest {

    def "should get information about user by userID"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()

        when:
        def request = MockMvcRequestBuilders.get("/api/users/${id}")
                .header("Authorization", token)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == 200

        def responseBody = new JsonSlurper().parseText(response.contentAsString)
        responseBody.id == id
        responseBody.email == DEFAULT_EMAIL
        responseBody.firstName == DEFAULT_FIRST_NAME
        responseBody.lastName == DEFAULT_LAST_NAME
        responseBody.jobTitle == null
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
        response.status == 200

        def responseBody = new JsonSlurper().parseText(response.contentAsString)
        responseBody.id == id
        responseBody.email != DEFAULT_EMAIL
        responseBody.firstName != DEFAULT_FIRST_NAME
        responseBody.lastName != DEFAULT_LAST_NAME
        responseBody.jobTitle != null
        responseBody.email == newEmail
        responseBody.firstName == newFirstName
        responseBody.lastName == newLastName
        responseBody.jobTitle == newJobTitle
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
        response.status == 400

        def responseBody = new JsonSlurper().parseText(response.contentAsString)
        responseBody.errorCode == ErrorCode.EMAIL_ALREADY_EXISTS.toString()
        responseBody.errorMessage == ErrorCode.EMAIL_ALREADY_EXISTS.getMessage()
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
        response.status == 400

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
        response.status == 200

        def responseBody = new JsonSlurper().parseText(response.contentAsString)
        responseBody.id == id
        responseBody.email == DEFAULT_EMAIL
        responseBody.firstName == DEFAULT_FIRST_NAME
        responseBody.lastName == DEFAULT_LAST_NAME
        responseBody.jobTitle == null

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
        response.status == 401

        def responseBody2 = new JsonSlurper().parseText(response.contentAsString)
        responseBody2.errorCode == ErrorCode.UNAUTHORIZED.toString()
        responseBody2.errorMessage == ErrorCode.UNAUTHORIZED.getMessage()

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
        response.status == 200

        def responseBody3 = new JsonSlurper().parseText(response.contentAsString)
        responseBody3.success == true
        responseBody3.token != null
        responseBody3.token != ""
        responseBody3.token.contains("Bearer ")
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
        response.status == 400

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
