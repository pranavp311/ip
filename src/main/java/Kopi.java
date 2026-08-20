import java.util.Scanner;

/**
 * A small personal-assistant chatbot.
 */
public class Kopi {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = " _  __           _\n"
            + "| |/ /___  _ __ (_)\n"
            + "| ' // _ \\| '_ \\| |\n"
            + "| . \\ (_) | |_) | |\n"
            + "|_|\\_\\___/| .__/|_|\n"
            + "          |_|";

    public static void main(String[] args) {
        System.out.println(LINE);
        System.out.println(BANNER);
        System.out.println("Hello! I'm Kopi.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[100];
        int taskCount = 0;
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                break;
            }
            try {
                if (input.equals("list")) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.printf("%d. %s%n", i + 1, tasks[i]);
                    }
                } else if (input.equals("mark") || input.startsWith("mark ")) {
                    int taskNumber = parseTaskNumber(input, "mark", taskCount);
                    tasks[taskNumber].markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks[taskNumber]);
                } else if (input.equals("unmark") || input.startsWith("unmark ")) {
                    int taskNumber = parseTaskNumber(input, "unmark", taskCount);
                    tasks[taskNumber].markAsNotDone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + tasks[taskNumber]);
                } else {
                    if (taskCount == tasks.length) {
                        throw new KopiException("The task list is full.");
                    }
                    Task task = parseTask(input);
                    tasks[taskCount] = task;
                    taskCount++;
                    System.out.println("added: " + task);
                }
            } catch (KopiException e) {
                System.out.println("OOPS!!! " + e.getMessage());
            }
            System.out.println(LINE);
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    private static Task parseTask(String input) throws KopiException {
        if (input.equals("todo") || input.startsWith("todo ")) {
            String description = input.substring(4).trim();
            requireText(description, "A todo needs a description.");
            return new Todo(description);
        }
        if (input.equals("deadline") || input.startsWith("deadline ")) {
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
        if (input.equals("event") || input.startsWith("event ")) {
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
        throw new KopiException("I don't understand that command.");
    }

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
}
