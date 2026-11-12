#!/bin/sh

set -eu

echo "================================"
echo "FXCLOCK SERVICE STARTING"
echo "================================"

export DISPLAY=:20

echo "Checking Java installation..."
java -version

echo "Checking JavaFX distribution..."

if [ ! -d /opt/fxclock/lib ]; then
    echo "ERROR: JavaFX library directory not found!"
    exit 1
fi

echo "Available application libraries:"
ls -la /opt/fxclock/lib/

echo "Waiting for X Server..."

until [ -S /tmp/.X11-unix/X20 ]; do
    sleep 1
done

echo "X Server is ready."

echo "Starting JavaFX application..."

exec java \
    -Dprism.order=sw \
    --module-path /opt/fxclock/lib \
    --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.swing \
    -cp "/opt/fxclock/lib/*" \
    org.example.FXClock
