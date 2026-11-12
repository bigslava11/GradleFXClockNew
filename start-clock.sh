#!/command/with-contenv bash

set -Ee

echo "=================================="
echo "FXCLOCK STARTING"
echo "=================================="

export DISPLAY=:20
export JAVA_TOOL_OPTIONS="-Dprism.order=sw"

echo "Checking Java..."
java -version

echo "Checking application launcher..."

if [ ! -x /opt/fxclock/bin/GradleFXClockNew ]; then
    echo "ERROR: Application launcher missing!"
    ls -la /opt/fxclock/bin/
    exit 1
fi

echo "Application launcher found."

echo "Waiting for X Server..."

until [ -S /tmp/.X11-unix/X20 ]; do
    sleep 1
done

echo "X Server ready!"

echo "Launching JavaFX application..."

exec /opt/fxclock/bin/GradleFXClockNew
