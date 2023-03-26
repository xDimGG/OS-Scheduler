## About this project
I built this project for my Operating Systems class at UAlbany to gain a deeper understanding of how an operating system's scheduler and memory management works.

## Features
- Fair scheduling: The scheduler prevents a process from misusing high resource utilization
- Memory Paging: The scheduler allows callers to allocate memory one page at a time and keeps track of which ones are free
- Swapfile: The scheduler moves pages to disk if the OS runs out of free pages
- Read/Write to disk via syscalls
- Maintain mutex locks for asynchronous memory use
- Pipe/Rand via syscalls
- Internal timer that ticks on every loop

## Drawbacks
- Since this is built in Java, we were not expected to maintain a pointer counter and have a quanta variable.
- The processes are expected to return in a reasonable amount of time and notify the scheduler whether or not they have finished running.
- The processes are expected to obey the return values of syscalls like Sleep and Mutex Lock/Unlock.
