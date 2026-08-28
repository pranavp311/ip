package kopi;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/** Handles all console input and output. */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = " _  __           _\n"
            + "| |/ /___  _ __ (_)\n"
            + "| ' // _ \\| '_ \\| |\n"
            + "| . \\ (_) | |_) | |\n"
            + "|_|\\_\\___/| .__/|_|\n"
            + "          |_|";

    private final PrintStream output;
    private final Scanner scanner;

    /** Creates a UI connected to standard input and output. */
    public Ui() {
        scanner = new Scanner(System.in);
        output = System.out;
    }

    /** Returns whether another command is available. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next command. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Shows Kopi's greeting. */
    public void showWelcome() {
        showLine();
        output.println(BANNER);
        output.println("Hello! I'm Kopi.");
        output.println("What can I do for you?");
        showLine();
    }

    /** Shows Kopi's farewell. */
    public void showGoodbye() {
        output.println("Bye. Hope to see you again soon!");
        showLine();
    }

    /** Shows all tasks with one-based numbering. */
    public void showTaskList(TaskList tasks) {
        output.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.printf("%d. %s%n", i + 1, tasks.get(i));
        }
    }

    /** Shows tasks that match a search keyword. */
    public void showMatches(List<Task> matches) {
        output.println("Here are the matching tasks in your list:");
        for (int i = 0; i < matches.size(); i++) {
            output.printf("%d. %s%n", i + 1, matches.get(i));
        }
    }

    /** Shows that a task was marked complete. */
    public void showMarked(Task task) {
        output.println("Nice! I've marked this task as done:");
        output.println("  " + task);
    }

    /** Shows that a task was marked incomplete. */
    public void showUnmarked(Task task) {
        output.println("OK, I've marked this task as not done yet:");
        output.println("  " + task);
    }

    /** Shows that a task was deleted. */
    public void showDeleted(Task task, int remainingCount) {
        output.println("Noted. I've removed this task:");
        output.println("  " + task);
        output.println("Now you have " + remainingCount + " tasks in the list.");
    }

    /** Shows that a task was added. */
    public void showAdded(Task task) {
        output.println("added: " + task);
    }

    /** Shows an error message. */
    public void showError(String message) {
        output.println("OOPS!!! " + message);
    }

    /** Shows a divider line. */
    public void showLine() {
        output.println(LINE);
    }
}
