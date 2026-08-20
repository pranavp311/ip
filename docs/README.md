# Kopi User Guide

Kopi is a command-line personal assistant that tracks todos, deadlines, and events.

## Adding tasks

- `todo DESCRIPTION` adds a task without a date or time.
- `deadline DESCRIPTION /by TIME` adds a task with a deadline.
- `event DESCRIPTION /from START /to END` adds an event with start and end times.

Examples:

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
```

## Listing tasks

Use `list` to display all tasks with their numbers, types, and completion states.

```text
1. [T][ ] borrow book
2. [D][ ] return book (by: Sunday)
3. [E][ ] project meeting (from: Mon 2pm to: 4pm)
```

## Updating task status

- `mark NUMBER` marks a task as complete.
- `unmark NUMBER` marks a task as incomplete.

## Deleting tasks

Use `delete NUMBER` to remove a task. The remaining tasks are renumbered automatically.

## Exiting

Use `bye` to close Kopi.

Kopi reports malformed commands and invalid task numbers without changing the task list.
