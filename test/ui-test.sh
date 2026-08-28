#!/bin/sh
set -eu

PROJECT_ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
TEST_DIR=$(mktemp -d)
trap 'rm -rf "$TEST_DIR"' EXIT

mkdir "$TEST_DIR/classes"
javac -d "$TEST_DIR/classes" "$PROJECT_ROOT"/src/main/java/kopi/*.java

(cd "$TEST_DIR" && printf 'todo read book\ndeadline return book /by 2026-09-06\nevent meeting /from 2026-09-07 /to 2026-09-08\nmark 2\nunmark 2\ndelete 1\nlist\ndeadline invalid /by Sunday\nblah\nbye\n' \
    | java -cp "$TEST_DIR/classes" kopi.Kopi > "$TEST_DIR/output.txt")

grep -Fq 'added: [T][ ] read book' "$TEST_DIR/output.txt"
grep -Fq 'added: [D][ ] return book (by: Sept 06 2026)' "$TEST_DIR/output.txt"
grep -Fq 'added: [E][ ] meeting (from: Sept 07 2026 to: Sept 08 2026)' "$TEST_DIR/output.txt"
grep -Fq "Nice! I've marked this task as done:" "$TEST_DIR/output.txt"
grep -Fq "OK, I've marked this task as not done yet:" "$TEST_DIR/output.txt"
grep -Fq "Noted. I've removed this task:" "$TEST_DIR/output.txt"
grep -Fq '1. [D][ ] return book (by: Sept 06 2026)' "$TEST_DIR/output.txt"
grep -Fq 'OOPS!!! Use dates in yyyy-MM-dd format.' "$TEST_DIR/output.txt"
grep -Fq "OOPS!!! I don't understand that command." "$TEST_DIR/output.txt"

(cd "$TEST_DIR" && printf 'list\nbye\n' | java -cp "$TEST_DIR/classes" kopi.Kopi > "$TEST_DIR/reloaded-output.txt")

grep -Fq '1. [D][ ] return book (by: Sept 06 2026)' "$TEST_DIR/reloaded-output.txt"
grep -Fq '2. [E][ ] meeting (from: Sept 07 2026 to: Sept 08 2026)' "$TEST_DIR/reloaded-output.txt"

echo 'UI test passed.'
