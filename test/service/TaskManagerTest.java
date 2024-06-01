package service;

import enums.Status;
import interfaces.TaskManager;
import models.Epic;
import models.SubTask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;


    @Test
    @DisplayName("Проверка статуса и работы времени в задаче")
    public void testStatusAndTimeEpics() {
        Epic epic = taskManager.createEpic(new Epic("epic", "descriptionEpic"));
        SubTask subTask = taskManager.createSubTask(new SubTask("subTask", "descSubTask",
                Status.NEW, LocalDateTime.now(), 30, 100));
        Assertions.assertEquals(0, taskManager.getSubtaskList().size());
        subTask = taskManager.createSubTask(new SubTask("subTask", "descSubTask",
                Status.NEW, LocalDateTime.now(), 30, epic.getId()));
        Assertions.assertEquals(1, taskManager.getSubtaskList().size());
        Assertions.assertEquals(Status.NEW, taskManager.getEpicById(epic.getId()).getStatus());
        SubTask subTaskNew = taskManager.createSubTask(new SubTask("subTaskNew", "descriptionSubTaskNew",
                Status.DONE, LocalDateTime.now().plusDays(1), 30, epic.getId()));
        Assertions.assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
        subTask.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask);
        Assertions.assertEquals(epic.getStatus(), taskManager.getEpicById(epic.getId()).getStatus());
        Assertions.assertEquals(subTask.getStartTime(), taskManager.getEpicById(epic.getId()).getStartTime());
        Assertions.assertEquals(subTaskNew.getEndTime(), taskManager.getEpicById(epic.getId()).getEndTime());
    }

}
