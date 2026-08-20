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
            if (input.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.printf("%d. %s%n", i + 1, tasks[i]);
                }
            } else if (input.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(input.substring(5)) - 1;
                tasks[taskNumber].markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + tasks[taskNumber]);
            } else if (input.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(input.substring(7)) - 1;
                tasks[taskNumber].markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + tasks[taskNumber]);
            } else {
                tasks[taskCount] = new Task(input);
                taskCount++;
                System.out.println("added: " + tasks[taskCount - 1]);
            }
            System.out.println(LINE);
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }
}
