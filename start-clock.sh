#!/bin/sh

set -eu

echo "================================"
echo "FXCLOCK SERVICE STARTING"
echo "================================"

export DISPLAY=:20

echo "Checking Java installation..."
java -version

echo "Checking application installation..."

if [ ! -f /opt/fxclock/lib/javafx-controls-24.0.2-linux.jar ]; then
    echo "JavaFX Controls JAR not found!"
    ls -la /opt/fxclock/lib/
    exit 1
fi

echo "JavaFX dependencies found."

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

