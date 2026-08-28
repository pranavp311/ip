package kopi;

import java.nio.file.Path;

/**
 * Coordinates the components of the Kopi chatbot.
 */
public class Kopi {
    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /** Creates a chatbot that stores its tasks at the given path. */
    public Kopi(Path dataPath) {
        parser = new Parser();
        storage = new Storage(dataPath);
        ui = new Ui();

        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (KopiException e) {
            ui.showError(e.getMessage());
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    /** Reads and executes commands until the user exits. */
    public void run() {
        ui.showWelcome();
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            try {
                Command command = parser.parseCommand(input);
                if (command == Command.BYE) {
                    break;
                }
                execute(command, input);
            } catch (KopiException e) {
                ui.showError(e.getMessage());
            }
            ui.showLine();
        }
        ui.showGoodbye();
    }

    /** Executes one parsed command. */
    private void execute(Command command, String input) throws KopiException {
        switch (command) {
            case LIST:
                ui.showTaskList(tasks);
                break;
            case MARK:
                int markIndex = parser.parseTaskNumber(input, "mark", tasks.size());
                tasks.get(markIndex).markAsDone();
                storage.save(tasks.getAll());
                ui.showMarked(tasks.get(markIndex));
                break;
            case UNMARK:
                int unmarkIndex = parser.parseTaskNumber(input, "unmark", tasks.size());
                tasks.get(unmarkIndex).markAsNotDone();
                storage.save(tasks.getAll());
                ui.showUnmarked(tasks.get(unmarkIndex));
                break;
            case DELETE:
                int deleteIndex = parser.parseTaskNumber(input, "delete", tasks.size());
                Task removedTask = tasks.delete(deleteIndex);
                storage.save(tasks.getAll());
                ui.showDeleted(removedTask, tasks.size());
                break;
            case TODO:
                // Fallthrough
            case DEADLINE:
                // Fallthrough
            case EVENT:
                Task task = parser.parseTask(input, command);
                tasks.add(task);
                storage.save(tasks.getAll());
                ui.showAdded(task);
                break;
            default:
                throw new KopiException("I don't understand that command.");
        }
    }

    /** Starts Kopi using its default data file. */
    public static void main(String[] args) {
        new Kopi(Path.of("data", "kopi.txt")).run();
    }
}
