
# ==========================================
# STAGE 1: Build JavaFX application
# ==========================================

FROM eclipse-temurin:22-jdk AS builder

WORKDIR /app

COPY . .

# Normalize Windows line endings in Gradle wrapper
RUN sed -i 's/\r$//' gradlew && \
    chmod +x gradlew && \
    ./gradlew --no-daemon clean installDist


# ==========================================
# STAGE 2: Selkies Linux desktop
# ==========================================

FROM ghcr.io/selkies-project/selkies-egl-desktop:26.04

# Install Java runtime using Selkies' package procedure
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

# Return to the normal desktop user
USER 1000

# Copy the complete Java application distribution
COPY --from=builder --chown=1000:1000 \
    /app/build/install/GradleFXClockNew \
    /opt/fxclock

# Register the JavaFX application as a service
COPY --chown=1000:1000 --chmod=755 \
    start-clock.sh /etc/service/fxclock/run

# ==========================================
# Selkies configuration
# ==========================================

ENV TZ=Europe/Riga

# Run only the application, without Plasma shell
ENV START_PLASMA=false

# Disable the command panel
ENV SELKIES_COMMAND_ENABLED=false

# Public access without login
ENV SELKIES_ENABLE_BASIC_AUTH=false

# Render terminates HTTPS
ENV SELKIES_ENABLE_HTTPS=false

# Render public port
ENV SELKIES_PORT=10000

# Enable audio streaming
ENV SELKIES_AUDIO_ENABLED=true
ENV SELKIES_AUDIO_ON_START=true

EXPOSE 10000
