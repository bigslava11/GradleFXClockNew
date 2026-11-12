#!/bin/sh

set -eu

echo "================================"
echo "FXCLOCK SERVICE STARTING"
echo "================================"

export DISPLAY=:20

echo "Checking Java installation..."
java -version

echo "Checking JavaFX application..."

if [ ! -f /opt/fxclock/bin/GradleFXClockNew ]; then
    echo "ERROR: Application launcher not found!"
    ls -la /opt/fxclock/bin/
    exit 1
fi

echo "Application launcher exists."

echo "Waiting for X Server..."

until [ -S /tmp/.X11-unix/X20 ]; do
    sleep 1
done

echo "X Server is ready."

echo "Starting JavaFX application..."

exec /opt/fxclock/bin/GradleFXClockNew
