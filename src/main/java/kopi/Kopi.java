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
                    ui.showLine();
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
                return markTask(input);
            case UNMARK:
                return unmarkTask(input);
            case DELETE:
                return deleteTask(input);
            case TODO:
                // Fallthrough
            case DEADLINE:
                // Fallthrough
            case EVENT:
                return addTask(input, command);
            default:
                throw new KopiException("I don't understand that command.");
        }
    }

    private String markTask(String input) throws KopiException {
        int index = parser.parseTaskNumber(input, "mark", tasks.size());
        Task task = tasks.get(index);
        task.markAsDone();
        storage.save(tasks.getAll());
        return ui.getMarked(task);
    }

    private String unmarkTask(String input) throws KopiException {
        int index = parser.parseTaskNumber(input, "unmark", tasks.size());
        Task task = tasks.get(index);
        task.markAsNotDone();
        storage.save(tasks.getAll());
        return ui.getUnmarked(task);
    }

    private String deleteTask(String input) throws KopiException {
        int index = parser.parseTaskNumber(input, "delete", tasks.size());
        Task removedTask = tasks.delete(index);
        storage.save(tasks.getAll());
        return ui.getDeleted(removedTask, tasks.size());
    }

    private String addTask(String input, Command command) throws KopiException {
        Task task = parser.parseTask(input, command);
        if (tasks.hasDuplicateOf(task)) {
            throw new KopiException("That task is already in the list.");
        }
        tasks.add(task);
        storage.save(tasks.getAll());
        return ui.getAdded(task);
    }

    /** Starts Kopi using its default data file. */
    public static void main(String[] args) {
        new Kopi(Path.of("data", "kopi.txt")).run();
    }
}
