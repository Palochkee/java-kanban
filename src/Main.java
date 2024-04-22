import models.Epic;
import models.Status;
import models.Task;
import models.SubTask;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Трекер задач на каждый день!");
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Работа", "Сходить на работу");
        Task task2 = new Task("Семья", "Уделить время семье");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Гардероб", "Выбрать что одеть", Status.NEW, 0);
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask("Одеть костюм", "Пойти на работу", Status.NEW, 0, epic1.getId());
        taskManager.createSubTask(subTask1);
        SubTask subTask2 = new SubTask("Одеть треники", "Остаться дома", Status.NEW, 0, epic1.getId());
        taskManager.createSubTask(subTask2);

        Epic epic2 = new Epic("Питание", "Приготовить поесть", Status.NEW, 0);
        taskManager.createEpic(epic2);
        SubTask subTask3 = new SubTask("Приготовить поесть", "Выбрать, что приготовить", Status.NEW, 0, epic2.getId());
        taskManager.createSubTask(subTask3);
        SubTask subTask4 = new SubTask("Пойти голодным", "Поесть на работа", Status.NEW, 0, epic2.getId());
        taskManager.createSubTask(subTask4);

        System.out.println("\nСписок эпиков:");
        for (Epic epic : taskManager.getEpicList()) {
            System.out.println(epic.getName());
        }

        System.out.println("\nСписок задач:");
        for (Task task : taskManager.getTasksList()) {
            System.out.println(task.getName());
        }

        System.out.println("\nСписок подзадач для эпика 1:");
        for (SubTask subTask : taskManager.getSubTasksEpicsIds(epic1.getId())) {
            System.out.println(subTask.getName());
        }

        Epic epic = taskManager.getEpicById(epic1.getId());
        if (epic != null) {
            System.out.printf("%nСтатус эпика %s: %s%n", epic1.getName(), epic.getStatus());
        } else {
            System.out.println("Эпик не найден.");
        }

        task1.setStatus(Status.IN_PROGRESS);
        subTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);
        subTask2.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask2);
        subTask3.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask3);
        subTask4.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask4);

        System.out.printf("%nСтатус задачи %s: %s%n", task1.getName(), task1.getStatus());

        System.out.printf("%nСтатус подзадачи %s: для эпика %s: %s%n", subTask1.getName(), epic1.getName(),
                subTask1.getStatus());
        System.out.printf("Статус подзадачи %s: для эпика %s: %s%n", subTask2.getName(), epic1.getName(),
                subTask2.getStatus());
        System.out.printf("Статус подзадачи %s: для эпика %s: %s%n", subTask3.getName(), epic2.getName(),
                subTask3.getStatus());
        System.out.printf("Статус подзадачи %s: для эпика %s: %s%n", subTask4.getName(), epic2.getName(),
                subTask4.getStatus());

        Epic createdEpic = taskManager.getEpicById(epic1.getId());
        if (createdEpic != null) {
            System.out.printf("%nСтатус эпика %s: %s%n", createdEpic.getName(), createdEpic.getStatus());
        } else {
            System.out.println("Эпик не найден.");
        }

        taskManager.removeTaskId(task1.getId());
        taskManager.removeEpicId(epic2.getId());
        taskManager.removeSubTaskId(subTask1.getId());

        System.out.println("\nСписок задач после удаления задачи 1:");
        for (Task task : taskManager.getTasksList()) {
            System.out.println(task.getName());
        }

        System.out.println("\nСписок эпиков после удаления эпика 2:");
        for (Epic epics : taskManager.getEpicList()) {
            System.out.println(epics.getName());
        }

        System.out.println("\nСписок подзадач для эпика 1 после удаления подзадачи 1:");
        for (SubTask subtask : taskManager.getSubTasksEpicsIds(epic1.getId())) {
            System.out.println(subtask.getName());
            System.out.println();
            System.out.println("День был продуктивным");
        }
    }
}
