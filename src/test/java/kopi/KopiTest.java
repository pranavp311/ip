package kopi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class KopiTest {
    @TempDir
    Path tempDirectory;

    @Test
    void getResponse_supportedCommands_commandsExecuted() {
        Kopi kopi = new Kopi(tempDirectory.resolve("kopi.txt"));

        assertTrue(kopi.getResponse("todo read book").contains("read book"));
        assertTrue(kopi.getResponse("deadline return book /by 2026-09-06").contains("return book"));
        assertTrue(kopi.getResponse(
                "event project meeting /from 2026-09-07 /to 2026-09-08").contains("project meeting"));

        String taskList = kopi.getResponse("list");
        assertTrue(taskList.contains("1. [T][ ] read book"));
        assertTrue(taskList.contains("2. [D][ ] return book"));
        assertTrue(taskList.contains("3. [E][ ] project meeting"));

        String matches = kopi.getResponse("find BOOK");
        assertTrue(matches.contains("read book"));
        assertTrue(matches.contains("return book"));
        assertFalse(matches.contains("project meeting"));

        assertTrue(kopi.getResponse("mark 1").contains("[T][X] read book"));
        assertTrue(kopi.getResponse("unmark 1").contains("[T][ ] read book"));
        assertTrue(kopi.getResponse("delete 3").contains("project meeting"));
        assertEquals("Bye. Hope to see you again soon!", kopi.getResponse("bye"));
        assertEquals("OOPS!!! I don't understand that command.", kopi.getResponse("dance"));
        assertTrue(kopi.isExitCommand("bye"));
    }

    @Test
    void getResponse_savedTask_taskLoadedByNewInstance() {
        Path dataPath = tempDirectory.resolve("nested").resolve("kopi.txt");
        Kopi firstKopi = new Kopi(dataPath);
        firstKopi.getResponse("todo persist this task");

        Kopi secondKopi = new Kopi(dataPath);

        assertTrue(secondKopi.getResponse("list").contains("persist this task"));
    }
}
