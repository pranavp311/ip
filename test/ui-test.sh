#!/bin/sh
set -eu

PROJECT_ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
TEST_DIR=$(mktemp -d)
trap 'rm -rf "$TEST_DIR"' EXIT

mkdir "$TEST_DIR/classes"
javac -d "$TEST_DIR/classes" "$PROJECT_ROOT"/src/main/java/*.java

printf 'todo read book\ndeadline return book /by Sunday\nevent meeting /from Mon /to Tue\nmark 2\nunmark 2\ndelete 1\nlist\nblah\nbye\n' \
    | java -cp "$TEST_DIR/classes" Kopi > "$TEST_DIR/output.txt"

grep -Fq 'added: [T][ ] read book' "$TEST_DIR/output.txt"
grep -Fq 'added: [D][ ] return book (by: Sunday)' "$TEST_DIR/output.txt"
grep -Fq 'added: [E][ ] meeting (from: Mon to: Tue)' "$TEST_DIR/output.txt"
grep -Fq "Nice! I've marked this task as done:" "$TEST_DIR/output.txt"
grep -Fq "OK, I've marked this task as not done yet:" "$TEST_DIR/output.txt"
grep -Fq "Noted. I've removed this task:" "$TEST_DIR/output.txt"
grep -Fq '1. [D][ ] return book (by: Sunday)' "$TEST_DIR/output.txt"
grep -Fq "OOPS!!! I don't understand that command." "$TEST_DIR/output.txt"

echo 'UI test passed.'
