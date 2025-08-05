# Stage 1: Build Environment
# This stage is used to install dependencies and build your application.
# It uses a larger base image that contains build tools.
FROM node:18-alpine AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json (or yarn.lock) first
# This allows Docker to cache these layers if dependencies haven't changed
COPY package*.json ./

# Install application dependencies
RUN npm install

# Copy the rest of the application source code
COPY . .

# Build your application (if applicable, e.g., for frontend frameworks or TypeScript)
# For a simple Node.js API, this might not be strictly necessary, or it could be 'npm run build'
# RUN npm run build

# Stage 2: Production Environment
# This stage creates a lean, production-ready image.
# It copies only the necessary build artifacts from the 'builder' stage.
FROM node:18-alpine

# Set the working directory
WORKDIR /app

# Copy only the installed node modules and the application code from the builder stage
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package*.json ./
COPY --from=builder /app/. .

# Expose the port your service listens on
EXPOSE 3000

# Define environment variables (e.g., for production mode)
ENV NODE_ENV=production

# Run the application as a non-root user for security best practices
# Create a non-root user and group
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
# Change ownership of the /app directory to the new user
RUN chown -R appuser:appgroup /app
# Switch to the non-root user
USER appuser
CMD ["npm", "start"]