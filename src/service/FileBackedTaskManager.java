package service;

import enums.Status;
import enums.TaskType;
import exceptions.ManagerSaveException;
import models.Epic;
import models.SubTask;
import models.Task;

import java.io.*;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {
    File FILE = new File("resources/file.csv");

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = FILE;

    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeTaskId(int id) {
        super.removeTaskId(id);
        save();
    }

    @Override
    public void removeEpicId(int id) {
        super.removeEpicId(id);
        save();
    }

    @Override
    public void removeSubTaskId(int id) {
        super.removeSubTaskId(id);
        save();
    }

    @Override
    public Task createTask(Task task) {
        Task pathTask = super.createTask(task);
        save();
        return pathTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic pathEpic = super.createEpic(epic);
        save();
        return pathEpic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask pathSubTask = super.createSubTask(subTask);
        save();
        return pathSubTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }


    @Override
    public Task getTaskById(int taskId) {
        Task task = super.getTaskById(taskId);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = super.getEpicById(epicId);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        SubTask subTask = super.getSubTaskById(subTaskId);
        save();
        return subTask;
    }

    private void save() {
        try {
            Files.deleteIfExists(file.toPath());
            Files.createFile(file.toPath());
            try (Writer writer = new FileWriter(file)) {
                writer.write("id,type,name,status,description,epic\n");
                for (Task task : tasksMap.values()) writer.write(task.toString() + "\n");
                for (Epic epic : epicsMap.values()) writer.write(epic.toString() + "\n");
                for (SubTask subtask : subTasksMap.values()) writer.write(subtask.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    static FileBackedTaskManager loadFromFile(File file) throws IllegalStateException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try {
            Reader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            br.readLine();
            String currentLine = br.readLine();
            String nextLine = br.readLine();
            while (currentLine != null) {
                if (nextLine == null && !currentLine.contains(TaskType.TASK.toString())
                        && !currentLine.contains(TaskType.EPIC.toString())
                        && !currentLine.contains(TaskType.SUBTASK.toString())) {
                    String[] ids = currentLine.split(",");
                    for (String id : ids) {
                        Task task;
                        int taskId = Integer.parseInt(id);
                        if (fileBackedTaskManager.tasksMap.containsKey(taskId)) {
                            task = fileBackedTaskManager.tasksMap.get(taskId);
                        } else if (fileBackedTaskManager.epicsMap.containsKey(taskId)) {
                            task = fileBackedTaskManager.epicsMap.get(taskId);
                        } else if (fileBackedTaskManager.subTasksMap.containsKey(taskId)) {
                            task = fileBackedTaskManager.subTasksMap.get(taskId);
                        } else {
                            System.out.println("Нет такого таска");
                            break;
                        }
                        fileBackedTaskManager.historyManager.add(task);
                    }
                } else {
                    Task task = fromString(currentLine);
                    int taskId = task.getId();
                    switch (task.getTaskType()) {
                        case TASK:
                            fileBackedTaskManager.tasksMap.put(taskId, task);
                            break;
                        case EPIC:
                            fileBackedTaskManager.epicsMap.put(taskId, (Epic) task);
                            break;
                        case SUBTASK:
                            SubTask subtask = (SubTask) task;
                            int epicId = subtask.getEpicId();
                            int epic = fileBackedTaskManager.epicsMap.get(epicId).getId();
                            if (epic == 0) {
                                System.out.println("Нет такого tasks.Epic");
                                break;
                            }
                            fileBackedTaskManager.subTasksMap.put(taskId, subtask);
                            fileBackedTaskManager.updateStatus(epic);
                            break;
                        default:
                            throw new IllegalStateException("Неверное значение задачи: " + task.getTaskType());
                    }
                    if (fileBackedTaskManager.counter <= taskId) fileBackedTaskManager.counter = ++taskId;
                }
                currentLine = nextLine;
                nextLine = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла: " + e.getMessage());
        }
        return fileBackedTaskManager;
    }

    private static Task fromString(String value) {
        Task task = null;
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        TaskType type = TaskType.valueOf(split[1]);
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String desc = split[4];
        switch (type) {
            case TASK -> task = new Task(name, desc, status, id);
            case EPIC -> task = new Epic(name, desc, id);
            case SUBTASK -> task = new SubTask(name, desc, status, id, Integer.parseInt(split[5]));
        }
        return task;
    }
}
