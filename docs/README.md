# Kopi User Guide

Kopi is a command-line personal assistant that tracks todos, deadlines, and events.

## Adding tasks

- `todo DESCRIPTION` adds a task without a date or time.
- `deadline DESCRIPTION /by YYYY-MM-DD` adds a task with a deadline.
- `event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD` adds an event with start and end dates.

Examples:

```text
todo borrow book
deadline return book /by 2026-09-06
event project meeting /from 2026-09-07 /to 2026-09-08
```

## Listing tasks

Use `list` to display all tasks with their numbers, types, and completion states.

```text
1. [T][ ] borrow book
2. [D][ ] return book (by: Sept 06 2026)
3. [E][ ] project meeting (from: Sept 07 2026 to: Sept 08 2026)
```

## Finding tasks

Use `find KEYWORD` to display tasks whose descriptions contain the keyword. Matching ignores letter case.

## Updating task status

- `mark NUMBER` marks a task as complete.
- `unmark NUMBER` marks a task as incomplete.

## Deleting tasks

Use `delete NUMBER` to remove a task. The remaining tasks are renumbered automatically.

## Exiting

Use `bye` to close Kopi.

Kopi reports malformed commands and invalid task numbers without changing the task list.
