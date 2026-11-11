
#!/usr/bin/with-contenv bash

set -eu

# Use Selkies' default X11 display
export DISPLAY="${DISPLAY:-:20}"

# Wait until the graphical display is available
DISPLAY_NUMBER="${DISPLAY#*:}"

until [ -S "/tmp/.X11-unix/X${DISPLAY_NUMBER}" ]; do
    sleep 1
done

# Start the JavaFX application
exec s6-setuidgid ubuntu \
    /opt/fxclock/bin/GradleFXClockNew
