FROM registry.access.redhat.com/ubi8/ubi-minimal:latest

# Install OpenSSH server
RUN microdnf install -y openssh-server openssh-clients shadow-utils && \
    microdnf clean all

# Create necessary directories
RUN mkdir -p /var/run/sshd /etc/ssh/ca /home

# Generate host keys
RUN ssh-keygen -A

# Create a basic sshd_config template
RUN echo "Port 22" > /etc/ssh/sshd_config && \
    echo "Protocol 2" >> /etc/ssh/sshd_config && \
    echo "HostKey /etc/ssh/ssh_host_rsa_key" >> /etc/ssh/sshd_config && \
    echo "HostKey /etc/ssh/ssh_host_ecdsa_key" >> /etc/ssh/sshd_config && \
    echo "HostKey /etc/ssh/ssh_host_ed25519_key" >> /etc/ssh/sshd_config && \
    echo "SyslogFacility AUTH" >> /etc/ssh/sshd_config && \
    echo "LogLevel INFO" >> /etc/ssh/sshd_config && \
    echo "PermitRootLogin no" >> /etc/ssh/sshd_config && \
    echo "PubkeyAuthentication yes" >> /etc/ssh/sshd_config && \
    echo "PasswordAuthentication yes" >> /etc/ssh/sshd_config && \
    echo "ChallengeResponseAuthentication no" >> /etc/ssh/sshd_config && \
    echo "UsePAM yes" >> /etc/ssh/sshd_config && \
    echo "X11Forwarding no" >> /etc/ssh/sshd_config && \
    echo "PrintMotd no" >> /etc/ssh/sshd_config && \
    echo "AcceptEnv LANG LC_*" >> /etc/ssh/sshd_config && \
    echo "Subsystem sftp internal-sftp" >> /etc/ssh/sshd_config

# Copy entrypoint script
COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

# Environment variables with defaults
ENV SFTP_SERVER_PORT=22 \
    SFTP_SERVER_USERNAME=test \
    SFTP_SERVER_PASSWORD=test \
    SFTP_SERVER_HOME=data \
    SFTP_SERVER_HOST=0.0.0.0 \
    SFTP_TRUSTED_USER_CA_KEYS=""

# Expose SSH/SFTP port
EXPOSE 22

# Start the SFTP server
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
