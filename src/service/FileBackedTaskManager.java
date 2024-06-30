package service;

import enums.Status;
import enums.TaskType;
import exceptions.ManagerSaveException;
import models.Epic;
import models.SubTask;
import models.Task;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;


public class FileBackedTaskManager extends InMemoryTaskManager {
    File createFile = new File("resources/file.csv");

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = createFile;

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
                writer.write("id,type,name,status,description,startTime,endTime,duration,epic\n");
                for (Task task : tasksMap.values()) writer.write(task.toString() + "\n");
                for (Epic epic : epicsMap.values()) writer.write(epic.toString() + "\n");
                for (SubTask subtask : subTasksMap.values()) writer.write(subtask.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }


    static FileBackedTaskManager loadFromFile(File file) throws IllegalStateException {
        FileBackedTaskManager fB = new FileBackedTaskManager(file);
        try {
            Reader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            br.readLine();
            String currentLine = br.readLine();
            String nextLine = br.readLine();
            while (currentLine != null) {
                if (nextLine == null && !currentLine.contains(TaskType.TASK.toString()) && !currentLine.contains(TaskType.EPIC.toString()) && !currentLine.contains(TaskType.SUBTASK.toString())) {
                    String[] ids = currentLine.split(",");
                    for (String id : ids) {
                        Task task;
                        int taskId = Integer.parseInt(id);
                        if (fB.tasksMap.containsKey(taskId)) {
                            task = fB.tasksMap.get(taskId);
                        } else if (fB.epicsMap.containsKey(taskId)) {
                            task = fB.epicsMap.get(taskId);
                        } else if (fB.subTasksMap.containsKey(taskId)) {
                            task = fB.subTasksMap.get(taskId);
                        } else {
                            System.out.println("Нет такого таска");
                            break;
                        }
                        fB.historyManager.add(task);
                    }
                } else {
                    Task task = fromString(currentLine);
                    int taskId = task.getId();
                    switch (task.getTaskType()) {
                        case TASK:
                            fB.tasksMap.put(taskId, task);
                            fB.addTaskToSortedTasks(task);
                            break;
                        case EPIC:
                            fB.epicsMap.put(taskId, (Epic) task);
                            break;
                        case SUBTASK:
                            SubTask subtask = (SubTask) task;
                            int epicId = subtask.getEpicId();
                            Epic epic = fB.epicsMap.get(epicId);
                            if (epic == null) {
                                System.out.println("Нет такого tasks.Epic");
                                break;
                            }
                            fB.subTasksMap.put(taskId, subtask);
                            fB.addTaskToSortedTasks(subtask);
                            epic.addSubTaskId(taskId);
                            fB.updateEpicFields(epic);
                            break;
                        default:
                            throw new IllegalStateException("Неверное значение задачи: " + task.getTaskType());
                    }
                    if (fB.counter <= taskId) fB.counter = ++taskId;
                }
                currentLine = nextLine;
                nextLine = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла: " + e.getMessage());
        }
        return fB;
    }


    private static Task fromString(String value) {
        Task task = null;
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        TaskType type = TaskType.valueOf(split[1]);
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String desc = split[4];
        String startTimeStr = split[5];
        LocalDateTime startTime = null;
        if (!startTimeStr.equals("null")) startTime = LocalDateTime.parse(startTimeStr);
        String endTimeStr = split[6];
        LocalDateTime endTime = null;
        if (!endTimeStr.equals("null")) endTime = LocalDateTime.parse(endTimeStr);
        int duration = Integer.parseInt(split[7]);
        switch (type) {
            case TASK -> task = new Task(name, desc, status, id, startTime, duration);
            case EPIC -> task = new Epic(name, desc, id);
            case SUBTASK -> task = new SubTask(name, desc, status, id, startTime, duration, Integer.parseInt(split[8]));
        }
        return task;
    }
}
