import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A small personal-assistant chatbot.
 */
public class Kopi {
    private static final Path DATA_FILE = Path.of("data", "kopi.txt");
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = " _  __           _\n"
            + "| |/ /___  _ __ (_)\n"
            + "| ' // _ \\| '_ \\| |\n"
            + "| . \\ (_) | |_) | |\n"
            + "|_|\\_\\___/| .__/|_|\n"
            + "          |_|";

    private enum Command {
        BYE, LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT
    }

    public static void main(String[] args) {
        System.out.println(LINE);
        System.out.println(BANNER);
        System.out.println("Hello! I'm Kopi.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        Scanner scanner = new Scanner(System.in);
        List<Task> tasks;
        try {
            tasks = loadTasks();
        } catch (KopiException e) {
            System.out.println("OOPS!!! " + e.getMessage());
            tasks = new ArrayList<>();
        }
        chat:
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            try {
                Command command = parseCommand(input);
                switch (command) {
                case BYE:
                    break chat;
                case LIST:
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.printf("%d. %s%n", i + 1, tasks.get(i));
                    }
                    break;
                case MARK:
                    int taskNumber = parseTaskNumber(input, "mark", tasks.size());
                    tasks.get(taskNumber).markAsDone();
                    saveTasks(tasks);
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks.get(taskNumber));
                    break;
                case UNMARK:
                    int unmarkNumber = parseTaskNumber(input, "unmark", tasks.size());
                    tasks.get(unmarkNumber).markAsNotDone();
                    saveTasks(tasks);
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + tasks.get(unmarkNumber));
                    break;
                case DELETE:
                    int deleteNumber = parseTaskNumber(input, "delete", tasks.size());
                    Task removedTask = tasks.remove(deleteNumber);
                    saveTasks(tasks);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + removedTask);
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                    break;
                case TODO:
                case DEADLINE:
                case EVENT:
                    Task task = parseTask(input, command);
                    tasks.add(task);
                    saveTasks(tasks);
                    System.out.println("added: " + task);
                    break;
                }
            } catch (KopiException e) {
                System.out.println("OOPS!!! " + e.getMessage());
            }
            System.out.println(LINE);
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    /**
     * Extracts a supported command from a line of user input.
     */
    private static Command parseCommand(String input) throws KopiException {
        if (input.isBlank()) {
            throw new KopiException("I don't understand an empty command.");
        }
        String commandWord = input.strip().split("\\s+", 2)[0].toUpperCase();
        try {
            return Command.valueOf(commandWord);
        } catch (IllegalArgumentException e) {
            throw new KopiException("I don't understand that command.");
        }
    }

    /**
     * Creates a task from a validated task-creation command.
     */
    private static Task parseTask(String input, Command command) throws KopiException {
        if (command == Command.TODO) {
            String description = input.substring(4).trim();
            requireText(description, "A todo needs a description.");
            return new Todo(description);
        }
        if (command == Command.DEADLINE) {
            int byIndex = input.indexOf(" /by ");
            if (byIndex < 0) {
                throw new KopiException("Use: deadline DESCRIPTION /by TIME");
            }
            String description = input.substring(8, byIndex).trim();
            String by = input.substring(byIndex + 5).trim();
            requireText(description, "A deadline needs a description.");
            requireText(by, "A deadline needs a time after /by.");
            return new Deadline(description, by);
        }
        if (command == Command.EVENT) {
            int fromIndex = input.indexOf(" /from ");
            int toIndex = input.indexOf(" /to ");
            if (fromIndex < 0 || toIndex < fromIndex) {
                throw new KopiException("Use: event DESCRIPTION /from START /to END");
            }
            String description = input.substring(5, fromIndex).trim();
            String from = input.substring(fromIndex + 7, toIndex).trim();
            String to = input.substring(toIndex + 5).trim();
            requireText(description, "An event needs a description.");
            requireText(from, "An event needs a start time after /from.");
            requireText(to, "An event needs an end time after /to.");
            return new Event(description, from, to);
        }
        throw new KopiException("That command cannot create a task.");
    }

    /**
     * Converts a one-based task number into a valid list index.
     */
    private static int parseTaskNumber(String input, String command, int taskCount) throws KopiException {
        String numberText = input.substring(command.length()).trim();
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new KopiException("Give me a valid task number for " + command + ".");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new KopiException("That task number is not in the list.");
        }
        return taskNumber - 1;
    }

    private static void requireText(String text, String message) throws KopiException {
        if (text.isEmpty()) {
            throw new KopiException(message);
        }
    }

    /** Loads tasks from the data file, or returns an empty list if it does not exist. */
    private static List<Task> loadTasks() throws KopiException {
        List<Task> tasks = new ArrayList<>();
        if (Files.notExists(DATA_FILE)) {
            return tasks;
        }
        try {
            for (String line : Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8)) {
                tasks.add(parseStoredTask(line));
            }
            return tasks;
        } catch (IOException e) {
            throw new KopiException("I couldn't load tasks from " + DATA_FILE + ".");
        }
    }

    /** Saves all tasks, creating the data directory when necessary. */
    private static void saveTasks(List<Task> tasks) throws KopiException {
        try {
            Files.createDirectories(DATA_FILE.getParent());
            List<String> lines = tasks.stream().map(Task::toDataString).toList();
            Files.write(DATA_FILE, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new KopiException("I couldn't save tasks to " + DATA_FILE + ".");
        }
    }

    /** Converts one line from the data file back into a task. */
    private static Task parseStoredTask(String line) throws KopiException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3) {
            throw new KopiException("The data file contains an invalid task.");
        }

        Task task;
        switch (fields[0]) {
        case "T":
            if (fields.length != 3) {
                throw new KopiException("The data file contains an invalid todo.");
            }
            task = new Todo(fields[2]);
            break;
        case "D":
            if (fields.length != 4) {
                throw new KopiException("The data file contains an invalid deadline.");
            }
            task = new Deadline(fields[2], fields[3]);
            break;
        case "E":
            if (fields.length != 5) {
                throw new KopiException("The data file contains an invalid event.");
            }
            task = new Event(fields[2], fields[3], fields[4]);
            break;
        default:
            throw new KopiException("The data file contains an unknown task type.");
        }

        if (fields[1].equals("1")) {
            task.markAsDone();
        } else if (!fields[1].equals("0")) {
            throw new KopiException("The data file contains an invalid task status.");
        }
        return task;
    }
}
