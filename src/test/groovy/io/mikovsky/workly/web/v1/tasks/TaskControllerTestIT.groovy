package io.mikovsky.workly.web.v1.tasks

import groovy.json.JsonSlurper
import io.mikovsky.workly.IntegrationTest
import io.mikovsky.workly.exceptions.ErrorCode
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

import java.time.LocalDate

class TaskControllerTestIT extends IntegrationTest {

    def "should get all private Tasks for user"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultTaskId1 = storeTask(defaultToken, "Task 1")

        def anotherId = registerAnotherUser()
        def anotherToken = getAnotherUserToken()
        def anotherTaskId1 = storeTask(anotherToken, "Task 2")

        when:
        def request = MockMvcRequestBuilders.get("/api/tasks")
                .header("Authorization", defaultToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == 200

        def body = new JsonSlurper().parseText(response.contentAsString)
        body.size() == 1
        body[0].id == defaultTaskId1
        body[0].name == "Task 1"
        body[0].description == DEFAULT_TASK_DESCRIPTION
        body[0].dueDate == DEFAULT_TASK_DUE_DATE
        body[0].completed == false
        body[0].createdAt != null
        body[0].updatedAt != null
        body[0].createdAt == body[0].updatedAt
    }

    def "should create new task for user"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()

        def taskName = "MyTask 1"
        def taskDescription = "MyTask 1 Description"
        def taskDueDate = LocalDate.now().plusDays(2).toString()

        when:
        def json = """
        {
            "name": "${taskName}",
            "description": "${taskDescription}",
            "dueDate": "${taskDueDate}"
        }
        """
        def request = MockMvcRequestBuilders.post("/api/tasks")
                .header("Authorization", token)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == 200

        def body = new JsonSlurper().parseText(response.contentAsString)
        body.id != null
        body.name == taskName
        body.description == taskDescription
        body.dueDate == taskDueDate
        body.completed == false
        body.createdAt != null
        body.updatedAt != null
        body.createdAt == body.updatedAt
    }

    def "should return error on create new task because of invalid payload"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()

        expect:
        def json = """
        {
            "name": ${taskName},
            "description": "${taskDescription}",
            "dueDate": "${taskDueDate}"
        }
        """
        def request = MockMvcRequestBuilders.post("/api/tasks")
                .header("Authorization", token)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        def response = mvc.perform(request).andReturn().response
        response != null
        response.status == 400

        where:
        taskName         | taskDescription     | taskDueDate
        null             | "Valid Description" | LocalDate.now().plusDays(1).toString()
        "\"\""           | "Valid Description" | LocalDate.now().plusDays(1).toString()
        "\"A\""          | "Valid Description" | LocalDate.now().plusDays(1).toString()
        "\"Valid Name\"" | "Valid Description" | LocalDate.now().minusDays(2).toString()
    }

    def "should update task"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()
        def taskId = storeTask(token)

        def newTaskName = "My Updated Task 1"
        def newTaskDescription = "My Updated Task Description 1"
        def newTaskDueDate = LocalDate.now().plusDays(10).toString()

        when:
        def json = """
        {
            "name": "${newTaskName}",
            "description": "${newTaskDescription}",
            "dueDate": "${newTaskDueDate}",
            "completed": true
        }
        """
        def request = MockMvcRequestBuilders.put("/api/tasks/${taskId}")
                .header("Authorization", token)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == 200

        def body = new JsonSlurper().parseText(response.contentAsString)
        body.id == taskId
        body.name == newTaskName
        body.description == newTaskDescription
        body.dueDate == newTaskDueDate
        body.completed == true
        body.createdAt != null
        body.updatedAt != null
        body.createdAt != body.updatedAt
    }

    def "should return error on task update because of invalid task ID"() {
        given:
        def id = registerDefaultUser()
        def token = getDefaultUserToken()

        when:
        def json = """
        {
            "name": "test test",
            "description": "test test test",
            "dueDate": "${LocalDate.now().plusDays(1).toString()}",
            "completed": true
        }
        """
        def request = MockMvcRequestBuilders.put("/api/tasks/${123123123123}")
                .header("Authorization", token)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == 404

        def body = new JsonSlurper().parseText(response.contentAsString)
        body.errorCode == ErrorCode.TASK_NOT_FOUND.toString()
        body.errorMessage == ErrorCode.TASK_NOT_FOUND.getMessage()
    }

}
