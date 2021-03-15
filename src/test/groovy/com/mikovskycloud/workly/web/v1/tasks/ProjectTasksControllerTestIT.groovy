package com.mikovskycloud.workly.web.v1.tasks

import com.mikovskycloud.workly.IntegrationTest
import com.mikovskycloud.workly.exceptions.ErrorCode
import com.mikovskycloud.workly.exceptions.WorklyException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Unroll

import java.time.LocalDate

class ProjectTasksControllerTestIT extends IntegrationTest {

    def "should get all project Tasks"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)
        def defaultTaskId1 = storeProjectTask(defaultToken, defaultProjectId, defaultId)
        def defaultTaskId2 = storeProjectTask(defaultToken, defaultProjectId, defaultId, "Task 2")

        def anotherId = registerAnotherUser()
        def anotherToken = getAnotherUserToken()
        def anotherProjectId = storeProject(anotherToken)
        def anotherTaskId1 = storeProjectTask(anotherToken, anotherProjectId, anotherId)
        def anotherTaskId2 = storeProjectTask(anotherToken, anotherProjectId, anotherId, "Task 2")

        when:
        def request = MockMvcRequestBuilders.get("/api/projects/${defaultProjectId}/tasks")
                .header("Authorization", defaultToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.OK.value()

        def body = parseBody(response) as List<?>
        body.size() == 2

        def task1 = findById(body, defaultTaskId1)
        task1.name == DEFAULT_TASK_NAME
        task1.description == DEFAULT_TASK_DESCRIPTION
        task1.completed == false
        task1.dueDate == DEFAULT_TASK_DUE_DATE
        task1.projectId == defaultProjectId
        task1.sectionId == null
        task1.assigneeId == defaultId
        task1.createdAt != null
        task1.updatedAt != null
        task1.createdAt == task1.updatedAt

        def task2 = findById(body, defaultTaskId2)
        task2.name == "Task 2"
        task2.description == DEFAULT_TASK_DESCRIPTION
        task2.completed == false
        task2.dueDate == DEFAULT_TASK_DUE_DATE
        task2.projectId == defaultProjectId
        task2.sectionId == null
        task2.assigneeId == defaultId
        task2.createdAt != null
        task2.updatedAt != null
        task2.createdAt == task2.updatedAt
    }

    def "should return error on get all project Tasks because project does not exists"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()

        when:
        def request = MockMvcRequestBuilders.get("/api/projects/123123123123/tasks")
                .header("Authorization", defaultToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.NOT_FOUND.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.PROJECT_NOT_FOUND.toString()
        body.errorMessage == ErrorCode.PROJECT_NOT_FOUND.getMessage()
    }

    def "should return error on get all project Tasks because user is not a member of the project"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)
        def defaultTaskId1 = storeProjectTask(defaultToken, defaultProjectId, defaultId)
        def defaultTaskId2 = storeProjectTask(defaultToken, defaultProjectId, defaultId, "Task 2")

        def anotherId = registerAnotherUser()
        def anotherToken = getAnotherUserToken()

        when:
        def request = MockMvcRequestBuilders.get("/api/projects/${defaultProjectId}/tasks")
                .header("Authorization", anotherToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.FORBIDDEN.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.FORBIDDEN.toString()
        body.errorMessage == ErrorCode.FORBIDDEN.getMessage()
    }

    def "should add project Task to the project"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)

        def taskName = "Some Task Name"
        def taskDescription = "Some Task Description"
        def taskDueDate = LocalDate.now().toString()
        def taskAssigneeId = defaultId

        when:
        def json = """
        {
            "name": "${taskName}",
            "description": "${taskDescription}",
            "dueDate": "${taskDueDate}",
            "assigneeId": ${taskAssigneeId}
        }
        """
        def request = MockMvcRequestBuilders.post("/api/projects/${defaultProjectId}/tasks")
                .header("Authorization", defaultToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.OK.value()

        def body = parseBody(response)
        body.name == taskName
        body.description == taskDescription
        body.completed == false
        body.dueDate == taskDueDate
        body.projectId == defaultProjectId
        body.sectionId == null
        body.assigneeId == defaultId
        body.createdAt != null
        body.updatedAt != null
        body.createdAt == body.updatedAt
    }

    def "should return error on add project Task to the project because project does not exists"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()

        def taskName = "Some Task Name"
        def taskDescription = "Some Task Description"
        def taskDueDate = LocalDate.now().toString()
        def taskAssigneeId = defaultId

        when:
        def json = """
        {
            "name": "${taskName}",
            "description": "${taskDescription}",
            "dueDate": "${taskDueDate}",
            "assigneeId": ${taskAssigneeId}
        }
        """
        def request = MockMvcRequestBuilders.post("/api/projects/123123123123/tasks")
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

    def "should return error on add project Task to the project because user is not a member of the project"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)

        def anotherId = registerAnotherUser()
        def anotherToken = getAnotherUserToken()

        def taskName = "Some Task Name"
        def taskDescription = "Some Task Description"
        def taskDueDate = LocalDate.now().toString()
        def taskAssigneeId = defaultId

        when:
        def json = """
        {
            "name": "${taskName}",
            "description": "${taskDescription}",
            "dueDate": "${taskDueDate}",
            "assigneeId": ${taskAssigneeId}
        }
        """
        def request = MockMvcRequestBuilders.post("/api/projects/${defaultProjectId}/tasks")
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
    def "should return error on add project Task to the project because of invalid payload"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)

        expect:
        def json = """
        {
            "name": ${taskName},
            "description": ${taskDescription},
            "dueDate": ${taskDueDate},
            "assigneeId": ${taskAssigneeId},
        }
        """
        def request = MockMvcRequestBuilders.post("/api/projects/${defaultProjectId}/tasks")
                .header("Authorization", defaultToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response

        response != null
        response.status == HttpStatus.BAD_REQUEST.value()

        where:
        taskName                      | taskDescription                | taskDueDate                             | taskAssigneeId
        null                          | null                           | null                                    | null
        "\"\""                        | null                           | null                                    | null
        "\"A\""                       | null                           | null                                    | null
        "\"${STRING_65_CHARACTERS}\"" | null                           | null                                    | null
        "\"Valid Name\""              | "\"${STRING_256_CHARACTERS}\"" | null                                    | null
        "\"Valid Name\""              | "Valid Description"            | LocalDate.now().minusDays(2).toString() | null
        "\"Valid Name\""              | "Valid Description"            | LocalDate.now().plusDays(2).toString()  | -1
    }

    def "should add project Task to the project and section"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)
        def defaultSectionId = storeSection(defaultToken, defaultProjectId)

        def taskName = "Some Task Name"
        def taskDescription = "Some Task Description"
        def taskDueDate = LocalDate.now().toString()
        def taskSectionId = defaultSectionId
        def taskAssigneeId = defaultId

        when:
        def json = """
        {
            "name": "${taskName}",
            "description": "${taskDescription}",
            "dueDate": "${taskDueDate}",
            "sectionId": ${taskSectionId},
            "assigneeId": ${taskAssigneeId}
        }
        """
        def request = MockMvcRequestBuilders.post("/api/projects/${defaultProjectId}/tasks")
                .header("Authorization", defaultToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.OK.value()

        def body = parseBody(response)
        body.name == taskName
        body.description == taskDescription
        body.completed == false
        body.dueDate == taskDueDate
        body.projectId == defaultProjectId
        body.sectionId == taskSectionId
        body.assigneeId == defaultId
        body.createdAt != null
        body.updatedAt != null
        body.createdAt == body.updatedAt
    }

    def "should return error on add project Task to the project and section because section does not exists"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)

        def taskName = "Some Task Name"
        def taskDescription = "Some Task Description"
        def taskDueDate = LocalDate.now().toString()
        def taskSectionId = 123123123123
        def taskAssigneeId = defaultId

        when:
        def json = """
        {
            "name": "${taskName}",
            "description": "${taskDescription}",
            "dueDate": "${taskDueDate}",
            "sectionId": ${taskSectionId},
            "assigneeId": ${taskAssigneeId}
        }
        """
        def request = MockMvcRequestBuilders.post("/api/projects/${defaultProjectId}/tasks")
                .header("Authorization", defaultToken)
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

    def "should update project Task from the project"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)
        def defaultTaskId = storeProjectTask(defaultToken, defaultProjectId, defaultId)
        def defaultSectionId = storeSection(defaultToken, defaultProjectId)

        def anotherId = registerAnotherUser()
        addMemberToProject(defaultToken, defaultProjectId, ANOTHER_EMAIL)

        def updatedName = "New Task Name"
        def updatedDescription = "New Task Description"
        def updatedDueDate = LocalDate.now().plusDays(7).toString()
        def updatedCompleted = true
        def updatedSectionId = defaultSectionId
        def updatedAssigneeId = anotherId

        when:
        def json = """
        {
            "name": "${updatedName}",
            "description": "${updatedDescription}",
            "dueDate": "${updatedDueDate}",
            "completed": "${updatedCompleted}",
            "sectionId": "${updatedSectionId}",
            "assigneeId": "${updatedAssigneeId}"
        }
        """
        def request = MockMvcRequestBuilders.put("/api/projects/${defaultProjectId}/tasks/${defaultTaskId}")
                .header("Authorization", defaultToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.OK.value()

        def body = parseBody(response)
        body.name == updatedName
        body.description == updatedDescription
        body.completed == updatedCompleted
        body.dueDate == updatedDueDate
        body.projectId == defaultProjectId
        body.sectionId == updatedSectionId
        body.assigneeId == updatedAssigneeId
        body.createdAt != null
        body.updatedAt != null
        body.createdAt != body.updatedAt
    }

    def "should return error on update project Task from the project because project does not exists"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)
        def defaultTaskId = storeProjectTask(defaultToken, defaultProjectId, defaultId)

        when:
        def json = """
        {
            "name": "New Task Name",
            "description": "New Task Description",
            "dueDate": "${LocalDate.now().plusDays(7).toString()}",
            "completed": true
        }
        """
        def request = MockMvcRequestBuilders.put("/api/projects/123123123123/tasks/${defaultTaskId}")
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

    def "should return error on update project Task from the project because user is not a member of the project"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)
        def defaultTaskId = storeProjectTask(defaultToken, defaultProjectId, defaultId)

        def anotherId = registerAnotherUser()
        def anotherToken = getAnotherUserToken()

        when:
        def json = """
        {
            "name": "New Task Name",
            "description": "New Task Description",
            "dueDate": "${LocalDate.now().plusDays(7).toString()}",
            "completed": true
        }
        """
        def request = MockMvcRequestBuilders.put("/api/projects/${defaultProjectId}/tasks/${defaultTaskId}")
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

    def "should return error on update project Task from the project because task does not exists"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)
        def defaultTaskId = storeProjectTask(defaultToken, defaultProjectId, defaultId)

        when:
        def json = """
        {
            "name": "New Task Name",
            "description": "New Task Description",
            "dueDate": "${LocalDate.now().plusDays(7).toString()}",
            "completed": true
        }
        """
        def request = MockMvcRequestBuilders.put("/api/projects/${defaultProjectId}/tasks/123123123123")
                .header("Authorization", defaultToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.NOT_FOUND.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.TASK_NOT_FOUND.toString()
        body.errorMessage == ErrorCode.TASK_NOT_FOUND.getMessage()
    }

    def "should return error on update project Task from the project because of invalid payload"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)
        def defaultTaskId = storeProjectTask(defaultToken, defaultProjectId, defaultId)

        expect:
        def json = """
        {
            "name": ${taskName},
            "description": ${taskDescription},
            "dueDate": ${taskDueDate},
            "sectionId": ${taskSectionId},
            "assigneeId": ${taskAssigneeId},
        }
        """
        def request = MockMvcRequestBuilders.put("/api/projects/${defaultProjectId}/tasks/${defaultTaskId}")
                .header("Authorization", defaultToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        def response = mvc.perform(request).andReturn().response

        response != null
        response.status == HttpStatus.BAD_REQUEST.value()

        where:
        taskName                      | taskDescription                | taskDueDate                             | taskSectionId | taskAssigneeId
        null                          | null                           | null                                    | null          | null
        "\"\""                        | null                           | null                                    | null          | null
        "\"A\""                       | null                           | null                                    | null          | null
        "\"${STRING_65_CHARACTERS}\"" | null                           | null                                    | null          | null
        "\"Valid Name\""              | "\"${STRING_256_CHARACTERS}\"" | null                                    | null          | null
        "\"Valid Name\""              | "Valid Description"            | LocalDate.now().minusDays(2).toString() | null          | null
        "\"Valid Name\""              | "Valid Description"            | LocalDate.now().plusDays(2).toString()  | -1            | null
        "\"Valid Name\""              | "Valid Description"            | LocalDate.now().plusDays(2).toString()  | null          | -1
    }

    def "should return error on update project Task to the project and section because section does not exists"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)
        def defaultTaskId = storeProjectTask(defaultToken, defaultProjectId, defaultId)

        def anotherId = registerAnotherUser()
        addMemberToProject(defaultToken, defaultProjectId, ANOTHER_EMAIL)

        def updatedName = "New Task Name"
        def updatedDescription = "New Task Description"
        def updatedDueDate = LocalDate.now().plusDays(7).toString()
        def updatedCompleted = true
        def updatedSectionId = 123123123123
        def updatedAssigneeId = anotherId

        when:
        def json = """
        {
            "name": "${updatedName}",
            "description": "${updatedDescription}",
            "dueDate": "${updatedDueDate}",
            "completed": "${updatedCompleted}",
            "sectionId": "${updatedSectionId}",
            "assigneeId": "${updatedAssigneeId}"
        }
        """
        def request = MockMvcRequestBuilders.put("/api/projects/${defaultProjectId}/tasks/${defaultTaskId}")
                .header("Authorization", defaultToken)
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

    def "should delete project Task from the project"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)
        def defaultTaskId = storeProjectTask(defaultToken, defaultProjectId, defaultId)

        when:
        def request = MockMvcRequestBuilders.delete("/api/projects/${defaultProjectId}/tasks/${defaultTaskId}")
                .header("Authorization", defaultToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.NO_CONTENT.value()
    }

    def "should return error on delete project Task from the project because project does not exists"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)
        def defaultTaskId = storeProjectTask(defaultToken, defaultProjectId, defaultId)

        when:
        def request = MockMvcRequestBuilders.delete("/api/projects/123123123123/tasks/${defaultTaskId}")
                .header("Authorization", defaultToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.NOT_FOUND.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.PROJECT_NOT_FOUND.toString()
        body.errorMessage == ErrorCode.PROJECT_NOT_FOUND.getMessage()
    }

    def "should return error on delete project Task from the project because user is not a member of the project"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)
        def defaultTaskId = storeProjectTask(defaultToken, defaultProjectId, defaultId)

        def anotherId = registerAnotherUser()
        def anotherToken = getAnotherUserToken()

        when:
        def request = MockMvcRequestBuilders.delete("/api/projects/${defaultProjectId}/tasks/${defaultTaskId}")
                .header("Authorization", anotherToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.FORBIDDEN.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.FORBIDDEN.toString()
        body.errorMessage == ErrorCode.FORBIDDEN.getMessage()
    }

    def "should return error on delete project Task from the project because task does not exists"() {
        given:
        def defaultId = registerDefaultUser()
        def defaultToken = getDefaultUserToken()
        def defaultProjectId = storeProject(defaultToken)
        def defaultTaskId = storeProjectTask(defaultToken, defaultProjectId, defaultId)

        when:
        def request = MockMvcRequestBuilders.delete("/api/projects/${defaultProjectId}/tasks/123123123123")
                .header("Authorization", defaultToken)
        def response = mvc.perform(request).andReturn().response

        then:
        response != null
        response.status == HttpStatus.NOT_FOUND.value()

        def body = parseBody(response)
        body.errorCode == ErrorCode.TASK_NOT_FOUND.toString()
        body.errorMessage == ErrorCode.TASK_NOT_FOUND.getMessage()
    }

    static def findById(tasks, id) {
        return tasks.stream()
                .filter({ it -> it.id == id })
                .findFirst()
                .orElseThrow({ WorklyException.taskNotFound() })
    }

}
