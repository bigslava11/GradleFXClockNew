#!/bin/sh

set -eu

echo "================================"
echo "FXCLOCK SERVICE STARTING"
echo "================================"

export DISPLAY=:20

echo "Checking Java..."
java -version

echo "Checking JavaFX libraries..."

if [ ! -d /opt/fxclock/lib ]; then
    echo "ERROR: JavaFX libraries not found!"
    exit 1
fi

echo "Available JavaFX libraries:"
ls /opt/fxclock/lib/

echo "Waiting for X Server..."

until [ -S /tmp/.X11-unix/X20 ]; do
    sleep 1
done

echo "X Server is ready."

echo "Starting FXClock with JavaFX modules..."
# ==========================================
# TEMPORARY SELKIES CONNECTION MONITOR
# ==========================================

(
    count=0

    while [ "$count" -lt 36 ]; do

        echo "========== SESSION DIAGNOSTICS =========="
        date

        echo "========== PROCESS TREE =========="
        ps -ef --forest

        echo "========== ESTABLISHED CONNECTIONS =========="
        ss -tnp state established 2>/dev/null || true

        sleep 10
        count=$((count + 1))

    done

) &
exec java \
    -Dprism.order=sw \
    --module-path /opt/fxclock/lib \
    --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.swing \
    -cp "/opt/fxclock/lib/*" \
    org.example.FXClock

