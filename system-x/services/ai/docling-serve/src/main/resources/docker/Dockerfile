FROM python:3.11-slim-bookworm

ARG DOCLING_VERSION=1.12.0

# Install system dependencies (curl, git, OpenGL for OpenCV)
RUN apt-get update && apt-get install -y \
    curl \
    git \
    libgl1 \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*

# Install PyTorch (amd64/arm64 have PyPI wheels)
RUN pip install --no-cache-dir torch torchvision

# Install pypdfium2
RUN pip install --no-cache-dir pypdfium2

# Create constraints file
RUN echo "torch" > /tmp/constraints.txt && \
    echo "torchvision" >> /tmp/constraints.txt && \
    echo "pypdfium2" >> /tmp/constraints.txt

# Install docling-serve with UI support
RUN pip install --no-cache-dir \
    --constraint /tmp/constraints.txt \
    "docling-serve[ui]==${DOCLING_VERSION}"

# Verify installation
RUN python -c "import torch; import docling_serve; import gradio; print(f'PyTorch {torch.__version__}, Gradio {gradio.__version__}')"

EXPOSE 5001

HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:5001/health || exit 1

CMD ["docling-serve", "run"]
