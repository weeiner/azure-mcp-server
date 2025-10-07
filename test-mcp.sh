#!/bin/bash

# Test script for MCP server
echo "Testing MCP Server..."

# Test 1: Initialize the server
echo '{"jsonrpc": "2.0", "id": 1, "method": "initialize", "params": {"protocolVersion": "2025-06-18", "capabilities": {}, "clientInfo": {"name": "test-client", "version": "1.0.0"}}}' | java -jar target/azure-devops-mcp-server-0.0.1-SNAPSHOT.jar --stdio

echo -e "\n\n--- Test 2: List available tools ---"

# Test 2: List tools
echo '{"jsonrpc": "2.0", "id": 2, "method": "tools/list", "params": {}}' | java -jar target/azure-devops-mcp-server-0.0.1-SNAPSHOT.jar --stdio

echo -e "\n\n--- Test 3: Call list_projects tool ---"

# Test 3: Call a tool
echo '{"jsonrpc": "2.0", "id": 3, "method": "tools/call", "params": {"name": "list_projects", "arguments": {}}}' | java -jar target/azure-devops-mcp-server-0.0.1-SNAPSHOT.jar --stdio