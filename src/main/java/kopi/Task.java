package kopi;

/**
 * Represents a task and whether it has been completed.
 */
public abstract class Task {
    protected final String description;
    protected boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns the display marker for this task's completion state. */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns this task in the format used by the data file. */
    public abstract String toDataString();

    /** Returns the task's completion state and description for display. */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
