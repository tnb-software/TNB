#!/bin/sh

# This unlocks the account even when using key-based auth, otherwise it would fail with 'account is locked'
PASSWORD=${SSH_PASSWORD:-redhat}
echo "redhat:$PASSWORD" | chpasswd

# Check if public key is mounted
if [ -f /etc/ssh/tnb_authorized_key ]; then
    echo "Public key detected, configuring key-based authentication..."

    # Validate key format
    if ! ssh-keygen -l -f /etc/ssh/tnb_authorized_key > /dev/null 2>&1; then
        echo "ERROR: Invalid SSH public key format at /etc/ssh/tnb_authorized_key"
        exit 1
    fi

    # Setup authorized_keys for redhat user
    mkdir -p /home/redhat/.ssh
    cp /etc/ssh/tnb_authorized_key /home/redhat/.ssh/authorized_keys
    chown -R redhat:redhat /home/redhat/.ssh
    chmod 700 /home/redhat/.ssh
    chmod 600 /home/redhat/.ssh/authorized_keys

    # Disable password authentication
    sed -i 's/PasswordAuthentication yes/PasswordAuthentication no/' /etc/ssh/sshd_config

    echo "Key-based authentication configured successfully"
else
    echo "No public key found, using password authentication..."
    echo "Password authentication configured successfully"
fi

# Generate host keys
ssh-keygen -A

# Start SSH daemon
echo "Starting SSH server..."
exec /usr/sbin/sshd -D -e "$@"
