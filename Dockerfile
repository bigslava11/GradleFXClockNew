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

# Verify generated distribution
RUN echo "========== GRADLE DISTRIBUTION ==========" && \
    find /app/build/install/GradleFXClockNew -maxdepth 3 -type f


# ==========================================
# STAGE 2: Selkies Linux desktop
# ==========================================

FROM ghcr.io/selkies-project/selkies-egl-desktop:26.04

USER 0
SHELL ["/bin/sh", "-c"]

# Allow installation of runtime dependencies
RUN selkies-privileged-files release

USER 1000
SHELL ["/usr/bin/fakeroot", "--", "/bin/sh", "-c"]

# Install Java 22 runtime and JavaFX native dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        libgtk-3-0 \
        libgl1 \
        libx11-6 \
        libxext6 \
        libxrender1 \
        libxtst6 \
        libxi6 \
    && apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Restore privileged filesystem
USER 0
SHELL ["/bin/sh", "-c"]

RUN selkies-privileged-files restore

# Copy Java 22 runtime from Temurin builder
COPY --from=builder /opt/java/openjdk /opt/java/openjdk

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="/opt/java/openjdk/bin:${PATH}"

# ==========================================
# Install JavaFX application
# ==========================================

COPY --from=builder --chown=1000:1000 \
    /app/build/install/GradleFXClockNew \
    /opt/fxclock

# ==========================================
# Register JavaFX startup service
# ==========================================

RUN mkdir -p /etc/service/fxclock && \
    chown -R 1000:1000 /etc/service/fxclock && \
    chmod 755 /etc/service/fxclock

COPY --chown=1000:1000 start-clock.sh /etc/service/fxclock/run

RUN sed -i 's/\r$//' /etc/service/fxclock/run && \
    chmod 755 /etc/service/fxclock/run && \
    test -x /etc/service/fxclock/run

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

