#!/bin/sh

set -eu

echo "================================"
echo "FXCLOCK SERVICE STARTING"
echo "================================"

export DISPLAY=:20

echo "Java version:"
java -version

echo "Checking JavaFX libraries..."

if [ ! -d /opt/fxclock/lib ]; then
    echo "ERROR: Application libraries not found!"
    exit 1
fi

echo "Waiting for X Server..."

until [ -S /tmp/.X11-unix/X20 ]; do
    sleep 1
done

echo "X Server is ready."

echo "Starting FXClock..."

exec /opt/fxclock/bin/GradleFXClockNew
