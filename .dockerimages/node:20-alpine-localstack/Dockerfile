# Use the official Node.js 20 Alpine image as the base
FROM node:20-alpine

# Set working directory (optional, but good practice)
WORKDIR /app

# Install system dependencies using apk (Alpine Linux package manager)
# --no-cache reduces image size by not caching package lists
# git: for SCM operations
# python3: required for pip and awscli/localstack
# py3-pip: Python package installer
# curl: needed to download Terraform
# unzip: needed to extract Terraform binary
RUN apk add --no-cache \
    git \
    python3 \
    py3-pip \
    curl \
    unzip

# Install Python packages using pip3
# awscli: AWS Command Line Interface
# localstack[all]: Provides awslocal and all LocalStack dependencies
RUN pip3  install terraform-local awscli awscli-local --break-system-packages

# Install Terraform
# Download a specific version of Terraform (e.g., 1.8.5 for stability)
# Unzip it to /usr/local/bin to make it globally accessible
# Remove the downloaded zip file to keep the image clean
ARG TERRAFORM_VERSION="1.8.5"
RUN curl -LO https://releases.hashicorp.com/terraform/${TERRAFORM_VERSION}/terraform_${TERRAFORM_VERSION}_linux_amd64.zip && \
    unzip terraform_${TERRAFORM_VERSION}_linux_amd64.zip -d /usr/local/bin/ && \
    rm terraform_${TERRAFORM_VERSION}_linux_amd64.zip

# Verify installations (optional, but good for debugging image build)
# These commands will run during the Docker build process
RUN git --version
RUN python3 --version
RUN pip3 --version
RUN aws --version
RUN awslocal --version

# Set default command (optional, depends on your use case)
# CMD ["node"]
