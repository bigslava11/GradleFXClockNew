#!/command/with-contenv bash

set -eu

export DISPLAY="${DISPLAY:-:20}"

DISPLAY_NUMBER="${DISPLAY#*:}"

until [ -S "/tmp/.X11-unix/X${DISPLAY_NUMBER}" ]; do
    sleep 1
done

echo "Starting GradleFXClockNew..."

exec /opt/fxclock/bin/GradleFXClockNew
