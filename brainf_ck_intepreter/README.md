# 🧠 Brainfuck Interpreter

This project is a **Brainfuck interpreter** implemented in **x86 Assembly**.  
It executes Brainfuck programs by parsing the code, managing the memory tape, and handling input/output commands.

The project demonstrates low-level programming, pointer management, and interpreter design concepts.

---

## ✨ Features

- Full support for standard Brainfuck commands:
  - `>` — move the pointer right
  - `<` — move the pointer left
  - `+` — increment the memory cell
  - `-` — decrement the memory cell
  - `.` — output the current cell as a character
  - `,` — input a character into the current cell
  - `[` — start loop
  - `]` — end loop
- Memory management for the tape (configurable size)
- Error detection for unbalanced loops
- Efficient command parsing
- Optional assembly-level implementation for performance