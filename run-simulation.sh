#! /bin/bash

# Clear out the logfile
rm build/stdout/*

# Spawn HALSIM
./gradlew simulateJava --console=plain
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

    echo "[Simulation Runner] Killing processes"
    kill -TERM $tail_pid
    kill -9 $task_pid
    break
done

