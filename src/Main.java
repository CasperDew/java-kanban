public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task washCar = new Task("Помыть машину", "С использованием воска");
        Task washFloorCreated = taskManager.addTask(washCar);
        System.out.println(washFloorCreated);

        System.out.println();

        Task washCarToUpdate = new Task(washCar.getId(), "Почистить салон", "Использовать пылесос",
                Status.IN_PROGRESS);
        Task washFloorUpdated = taskManager.updateTask(washCarToUpdate);
        System.out.println(washFloorUpdated);

        System.out.println();

        Epic test = new Epic("Проверить что-то еще", "какое-то описание");
        taskManager.addEpic(test);
        System.out.println(test);
        Subtask testTask1 = new Subtask("Обновить подзадачу", "такая задача", test.getId());
        taskManager.addSubtask(testTask1);
        System.out.println(test);
        testTask1.setStatus(Status.DONE);
        taskManager.updateSubtask(testTask1);
        System.out.println(testTask1);

    }
}
