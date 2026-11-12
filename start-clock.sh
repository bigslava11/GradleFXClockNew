
#!/command/with-contenv bash

set -Ee

echo "=========================================="
echo "FXCLOCK STARTUP SERVICE"
echo "=========================================="

# JavaFX display configuration
export DISPLAY="${DISPLAY:-:20}"

echo "DISPLAY=$DISPLAY"

# Configure JavaFX software rendering
export JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-} -Dprism.order=sw"

# Check Java installation
echo "Checking Java installation..."

java -version

# Check application launcher
echo "Checking JavaFX application..."

if [ ! -x /opt/fxclock/bin/GradleFXClockNew ]; then
    echo "ERROR: JavaFX launcher not found!"
    ls -la /opt/fxclock/bin/
    exit 1
fi

echo "JavaFX launcher found."

# Wait for X Server
echo "Waiting for X Server..."

DISPLAY_NUMBER="${DISPLAY#*:}"

until [ -S "/tmp/.X11-unix/X${DISPLAY_NUMBER}" ]; do
    sleep 1
done

echo "X Server is ready!"

# Start JavaFX application
echo "=========================================="
echo "STARTING GRADLE FX CLOCK"
echo "=========================================="

exec /opt/fxclock/bin/GradleFXClockNew
