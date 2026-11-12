
# ==========================================
# STAGE 1: Build JavaFX application
# ==========================================

FROM eclipse-temurin:22-jdk AS builder

WORKDIR /app

COPY . .

# Prepare Gradle wrapper and build application
RUN sed -i 's/\r$//' gradlew && \
    chmod +x gradlew && \
    ./gradlew --no-daemon clean installDist


# ==========================================
# STAGE 2: Selkies Linux desktop
# ==========================================

FROM ghcr.io/selkies-project/selkies-egl-desktop:26.04

# Install Java runtime
USER 0
SHELL ["/bin/sh", "-c"]

RUN selkies-privileged-files release

USER 1000
SHELL ["/usr/bin/fakeroot", "--", "/bin/sh", "-c"]

RUN apt-get update && \
    apt-get install -y --no-install-recommends openjdk-25-jre && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

USER 0
SHELL ["/bin/sh", "-c"]

RUN selkies-privileged-files restore

# ==========================================
# Install JavaFX application
# ==========================================

COPY --from=builder --chown=1000:1000 \
    /app/build/install/GradleFXClockNew \
    /opt/fxclock

# ==========================================
# Register JavaFX startup service
# ==========================================

RUN mkdir -p /etc/service/fxclock

COPY --chown=1000:1000 --chmod=755 \
    start-clock.sh /etc/service/fxclock/run

RUN ls -la /etc/service/fxclock/ && \
    test -x /etc/service/fxclock/run && \
    test -x /opt/fxclock/bin/GradleFXClockNew

# Return to desktop user
USER 1000

# ==========================================
# Selkies configuration
# ==========================================

ENV TZ=Europe/Riga

ENV DISPLAY=:20

ENV START_PLASMA=false

ENV SELKIES_COMMAND_ENABLED=false

ENV SELKIES_ENABLE_BASIC_AUTH=false

ENV SELKIES_ENABLE_HTTPS=false

ENV SELKIES_PORT=10000

ENV SELKIES_AUDIO_ENABLED=true

ENV SELKIES_AUDIO_ON_START=true

# JavaFX software rendering
ENV JAVA_TOOL_OPTIONS="-Dprism.order=sw"

# ==========================================
# Network
# ==========================================

EXPOSE 10000
