#!/bin/bash
set -e

# Configure SSH port
sed -i "s/^Port .*/Port ${SFTP_SERVER_PORT}/" /etc/ssh/sshd_config

# Create user if it doesn't exist
if ! id "${SFTP_SERVER_USERNAME}" &>/dev/null; then
    echo "Creating user: ${SFTP_SERVER_USERNAME}"
    useradd -m -d "/home/${SFTP_SERVER_USERNAME}" -s /bin/bash "${SFTP_SERVER_USERNAME}"
fi

# Set password for the user
echo "${SFTP_SERVER_USERNAME}:${SFTP_SERVER_PASSWORD}" | chpasswd

# Create and set permissions for home directory
USER_HOME="/home/${SFTP_SERVER_USERNAME}"
if [ "${SFTP_SERVER_HOME}" != "data" ]; then
    # Use custom home directory
    USER_HOME="/home/${SFTP_SERVER_USERNAME}/${SFTP_SERVER_HOME}"
fi

mkdir -p "${USER_HOME}"
chown -R "${SFTP_SERVER_USERNAME}:${SFTP_SERVER_USERNAME}" "${USER_HOME}"

# Configure TrustedUserCAKeys if provided
if [ -n "${SFTP_TRUSTED_USER_CA_KEYS}" ]; then
    echo "Configuring TrustedUserCAKeys"

    # Check if CA key is provided as a path or as key content
    if [ -f "${SFTP_TRUSTED_USER_CA_KEYS}" ]; then
        # It's a file path, copy it
        cp "${SFTP_TRUSTED_USER_CA_KEYS}" /etc/ssh/ca/ca.pub
    else
        # It's the key content itself, write it to file
        echo "${SFTP_TRUSTED_USER_CA_KEYS}" > /etc/ssh/ca/ca.pub
    fi

    chmod 644 /etc/ssh/ca/ca.pub

    # Add TrustedUserCAKeys to sshd_config if not already present
    if ! grep -q "^TrustedUserCAKeys" /etc/ssh/sshd_config; then
        echo "TrustedUserCAKeys /etc/ssh/ca/ca.pub" >> /etc/ssh/sshd_config
    fi

    echo "TrustedUserCAKeys configured with CA public key"
fi

# Ensure proper permissions for SSH directories
chmod 755 /var/run/sshd
chmod 600 /etc/ssh/ssh_host_*_key
chmod 644 /etc/ssh/ssh_host_*_key.pub

# Print configuration for debugging
echo "=========================================="
echo "SFTP Server Configuration:"
echo "  Port: ${SFTP_SERVER_PORT}"
echo "  Username: ${SFTP_SERVER_USERNAME}"
echo "  Home: ${USER_HOME}"
echo "  CA Keys: $([ -n "${SFTP_TRUSTED_USER_CA_KEYS}" ] && echo "Enabled" || echo "Disabled")"
echo "=========================================="

# Start SSH daemon in foreground
echo "Server listening on ${SFTP_SERVER_HOST}:${SFTP_SERVER_PORT}"
exec /usr/sbin/sshd -D -e
