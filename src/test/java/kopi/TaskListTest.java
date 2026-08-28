package kopi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void find_keywordWithDifferentCase_matchingTasksReturned() {
        TaskList tasks = new TaskList(List.of(
                new Todo("Read book"),
                new Todo("return BOOK"),
                new Todo("buy groceries")));

        List<String> matches = tasks.find("book").stream().map(Task::getDescription).toList();

        assertEquals(List.of("Read book", "return BOOK"), matches);
    }

    @Test
    void find_missingKeyword_emptyListReturned() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), tasks.find("meeting"));
    }
}
