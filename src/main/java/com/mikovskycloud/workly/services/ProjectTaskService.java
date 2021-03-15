package com.mikovskycloud.workly.services;

import com.mikovskycloud.workly.domain.Section;
import com.mikovskycloud.workly.domain.Task;
import com.mikovskycloud.workly.domain.User;
import com.mikovskycloud.workly.exceptions.WorklyException;
import com.mikovskycloud.workly.repositories.ProjectRepository;
import com.mikovskycloud.workly.repositories.SectionRepository;
import com.mikovskycloud.workly.repositories.TaskRepository;
import com.mikovskycloud.workly.repositories.UserRepository;
import com.mikovskycloud.workly.web.v1.tasks.payload.CreateProjectTaskRequest;
import com.mikovskycloud.workly.web.v1.tasks.payload.ProjectTaskResponse;
import com.mikovskycloud.workly.web.v1.tasks.payload.UpdateProjectTaskRequest;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectTaskService {

    private final ProjectRepository projectRepository;

    private final AuthorizeService authorizeService;

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    private final SectionRepository sectionRepository;

    public List<ProjectTaskResponse> findTasksWithProjectId(Long projectId, User user) {
        authorize(projectId, user.getId());

        List<Task> tasks = taskRepository.findAllByProjectId(projectId);
        return StreamEx.of(tasks)
                .map(ProjectTaskResponse::fromTask)
                .toList();
    }

    public ProjectTaskResponse saveTask(CreateProjectTaskRequest request, Long projectId, User user) {
        authorize(projectId, user.getId());
        validate(projectId, request.getAssigneeId(), request.getSectionId());

        Task task = request.toTask();
        task.setUserId(user.getId());
        task.setProjectId(projectId);

        Task savedTask = save(task);
        return ProjectTaskResponse.fromTask(savedTask);
    }

    public ProjectTaskResponse updateTask(UpdateProjectTaskRequest request, Long projectId, Long taskId, User user) {
        if (request.isEmpty()) throw WorklyException.emptyRequest();

        authorize(projectId, user.getId());
        validate(projectId, request.getAssigneeId(), request.getSectionId());

        Task task = findTaskById(taskId);
        if (request.getName() != null) task.setName(request.getName());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getCompleted() != null) task.setCompleted(request.getCompleted());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
        if (request.getSectionId() != null) task.setSectionId(request.getSectionId());
        if (request.getAssigneeId() != null) task.setAssigneeId(request.getAssigneeId());

        Task updatedTask = update(task);
        return ProjectTaskResponse.fromTask(updatedTask);
    }

    public ResponseEntity<Void> deleteTask(Long projectId, Long taskId, User user) {
        authorize(projectId, user.getId());

        if (!taskRepository.existsById(taskId)) {
            throw WorklyException.taskNotFound();
        }

        taskRepository.deleteById(taskId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public Task save(Task task) {
        Instant now = Instant.now();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        return taskRepository.save(task);
    }

    public Task update(Task task) {
        task.setUpdatedAt(Instant.now());
        return taskRepository.save(task);
    }

    private void authorize(Long projectId, Long userId) {
        if (!projectRepository.existsById(projectId)) {
            throw WorklyException.projectNotFound();
        }

        authorizeService.throwIfNotProjectMember(projectId, userId);
    }

    private void validate(Long projectId, Long assigneeId, Long sectionId) {
        if (assigneeId != null) {
            User assignee = userRepository.findById(assigneeId).orElseThrow(WorklyException::userNotFound);
            authorizeService.throwIfNotProjectMember(projectId, assignee.getId());
        }

        if (sectionId != null) {
            Section section = sectionRepository.findById(sectionId).orElseThrow(WorklyException::sectionNotFound);
            if (!section.getProjectId().equals(projectId)) throw WorklyException.forbidden();
        }
    }

    private Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(WorklyException::taskNotFound);
    }

}
