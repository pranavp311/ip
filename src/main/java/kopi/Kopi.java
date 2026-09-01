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
    private final String startupMessage;

    /** Creates a chatbot that stores its tasks at the given path. */
    public Kopi(Path dataPath) {
        parser = new Parser();
        storage = new Storage(dataPath);
        ui = new Ui();

        TaskList loadedTasks;
        String loadMessage = null;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (KopiException e) {
            loadMessage = ui.getError(e.getMessage());
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
        startupMessage = loadMessage;
    }

    /** Reads and executes commands until the user exits. */
    public void run() {
        ui.showWelcome();
        if (startupMessage != null) {
            ui.showResponse(startupMessage);
            ui.showLine();
        }
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            try {
                Command command = parser.parseCommand(input);
                ui.showResponse(execute(command, input));
                if (command == Command.BYE) {
                    return;
                }
            } catch (KopiException e) {
                ui.showResponse(ui.getError(e.getMessage()));
            }
            ui.showLine();
        }
        ui.showResponse(ui.getGoodbye());
        ui.showLine();
    }

    /** Executes one command and returns its user-facing response. */
    public String getResponse(String input) {
        try {
            return execute(parser.parseCommand(input), input);
        } catch (KopiException e) {
            return ui.getError(e.getMessage());
        }
    }

    /** Returns whether the input asks Kopi to exit. */
    public boolean isExitCommand(String input) {
        try {
            return parser.parseCommand(input) == Command.BYE;
        } catch (KopiException e) {
            return false;
        }
    }

    /** Returns any error raised while loading stored tasks. */
    public String getStartupMessage() {
        return startupMessage;
    }

    private String execute(Command command, String input) throws KopiException {
        switch (command) {
            case BYE:
                return ui.getGoodbye();
            case LIST:
                return ui.getTaskList(tasks);
            case FIND:
                String keyword = parser.parseKeyword(input, "find");
                return ui.getMatches(tasks.find(keyword));
            case MARK:
                int markIndex = parser.parseTaskNumber(input, "mark", tasks.size());
                tasks.get(markIndex).markAsDone();
                storage.save(tasks.getAll());
                return ui.getMarked(tasks.get(markIndex));
            case UNMARK:
                int unmarkIndex = parser.parseTaskNumber(input, "unmark", tasks.size());
                tasks.get(unmarkIndex).markAsNotDone();
                storage.save(tasks.getAll());
                return ui.getUnmarked(tasks.get(unmarkIndex));
            case DELETE:
                int deleteIndex = parser.parseTaskNumber(input, "delete", tasks.size());
                Task removedTask = tasks.delete(deleteIndex);
                storage.save(tasks.getAll());
                return ui.getDeleted(removedTask, tasks.size());
            case TODO:
                // Fallthrough
            case DEADLINE:
                // Fallthrough
            case EVENT:
                Task task = parser.parseTask(input, command);
                tasks.add(task);
                storage.save(tasks.getAll());
                return ui.getAdded(task);
            default:
                throw new KopiException("I don't understand that command.");
        }
    }

    /** Starts Kopi using its default data file. */
    public static void main(String[] args) {
        new Kopi(Path.of("data", "kopi.txt")).run();
    }
}
