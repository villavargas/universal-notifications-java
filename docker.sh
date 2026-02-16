#!/bin/bash

# Notifications Library - Docker Helper Script
# This script helps build and run the Docker image

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

IMAGE_NAME="notifications-library"
IMAGE_TAG="latest"
CONTAINER_NAME="notifications-library-demo"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
print_header() {
    echo -e "${BLUE}================================================${NC}"
    echo -e "${BLUE}  Notifications Library - Docker Manager${NC}"
    echo -e "${BLUE}================================================${NC}"
    echo ""
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

build_image() {
    print_info "Building Docker image..."
    docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
    print_success "Docker image built successfully: ${IMAGE_NAME}:${IMAGE_TAG}"
}

run_container() {
    print_info "Running container..."
    
    # Stop and remove existing container if it exists
    if [ "$(docker ps -aq -f name=${CONTAINER_NAME})" ]; then
        print_info "Removing existing container..."
        docker rm -f ${CONTAINER_NAME} 2>/dev/null || true
    fi
    
    # Run the container
    docker run --name ${CONTAINER_NAME} ${IMAGE_NAME}:${IMAGE_TAG}
    print_success "Container executed successfully!"
}

run_interactive() {
    print_info "Running container in interactive mode..."
    
    docker run -it --rm \
        --name ${CONTAINER_NAME} \
        ${IMAGE_NAME}:${IMAGE_TAG} \
        /bin/sh
}

run_with_custom_command() {
    print_info "Running container with custom command: $1"
    
    docker run --rm \
        --name ${CONTAINER_NAME} \
        ${IMAGE_NAME}:${IMAGE_TAG} \
        sh -c "$1"
}

show_logs() {
    print_info "Showing container logs..."
    docker logs ${CONTAINER_NAME}
}

stop_container() {
    print_info "Stopping container..."
    docker stop ${CONTAINER_NAME} 2>/dev/null || true
    print_success "Container stopped"
}

remove_container() {
    print_info "Removing container..."
    docker rm -f ${CONTAINER_NAME} 2>/dev/null || true
    print_success "Container removed"
}

clean_all() {
    print_info "Cleaning up..."
    remove_container
    docker rmi ${IMAGE_NAME}:${IMAGE_TAG} 2>/dev/null || true
    print_success "Cleanup completed"
}

show_help() {
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  build       Build the Docker image"
    echo "  run         Build and run the container (default)"
    echo "  demo        Run the demo examples (same as run)"
    echo "  shell       Start an interactive shell in the container"
    echo "  exec        Run a custom command in the container"
    echo "  logs        Show container logs"
    echo "  stop        Stop the running container"
    echo "  clean       Remove container and image"
    echo "  help        Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 build              # Build the Docker image"
    echo "  $0 run                # Build and run examples"
    echo "  $0 shell              # Open interactive shell"
    echo "  $0 exec 'ls -la'      # Run custom command"
    echo ""
}

# Main script
print_header

case "${1:-run}" in
    build)
        build_image
        ;;
    run|demo)
        build_image
        run_container
        ;;
    shell|interactive)
        build_image
        run_interactive
        ;;
    exec)
        if [ -z "$2" ]; then
            print_error "Please provide a command to execute"
            echo "Example: $0 exec 'ls -la'"
            exit 1
        fi
        build_image
        run_with_custom_command "$2"
        ;;
    logs)
        show_logs
        ;;
    stop)
        stop_container
        ;;
    clean)
        clean_all
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        print_error "Unknown command: $1"
        echo ""
        show_help
        exit 1
        ;;
esac

echo ""
print_success "Done!"
