# Use the official Ubuntu base image
FROM ubuntu:noble-20260210.1

# Update package lists and install curl and jq
RUN apt-get update && apt-get install -y \
    curl \
    jq

# Set the working directory
WORKDIR /app

COPY v1 /app/v1

# Copy your bash script into the container
COPY schema-registry-autoimport.sh /app/schema-registry-autoimport.sh

# Make the script executable
RUN chmod +x /app/schema-registry-autoimport.sh

# Set the entrypoint to your bash script
CMD ["bash", "-c", "./schema-registry-autoimport.sh && sleep infinity"]
