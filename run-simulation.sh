#! /bin/bash

# Spawn HALSIM
./gradlew simulateJava
task_pid=$(cat build/pids/simulateJava.pid)

# Spawn a tail follower
tail -f build/stdout/simulateJava.log & tail_pid=$!

while [ 1 ]
do
    # -0 is a special "poke" signal - "are you around?"
    kill -0 $task_pid
    if [ $? -eq 0 ]
    then
        # Original task is still alive.
        sleep 2
        continue
    fi
    kill -TERM $tail_pid
    break
done
