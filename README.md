# Azure DevOps MCP Server

A Model Context Protocol (MCP) server that provides tools for querying Azure DevOps projects, work items, repositories, and builds. Built with Spring Boot and communicates via stdio for integration with MCP clients like Claude Desktop.

## Features

This MCP server provides the following tools:

- **list_projects** - List all projects in your Azure DevOps organization
- **get_project** - Get detailed information about a specific project
- **get_work_items** - Query work items using WIQL (Work Item Query Language)
- **get_work_item** - Get detailed information about a specific work item
- **get_repositories** - List Git repositories in a project
- **get_builds** - Get recent builds from a project

## Prerequisites

- Java 17 or higher
- Azure DevOps account with access to your organization
- Personal Access Token (PAT) for Azure DevOps

## Setup

### 1. Create Azure DevOps Personal Access Token

1. Sign in to your Azure DevOps organization (https://dev.azure.com/{yourorganization})
2. Go to User Settings → Personal Access Tokens
3. Create a new token with the following scopes:
   - **Project and Team**: Read
   - **Work Items**: Read
   - **Code (Git)**: Read
   - **Build**: Read

### 2. Configure Environment Variables

Set the following environment variables:

```bash
export AZURE_DEVOPS_ORGANIZATION=your-organization-name
export AZURE_DEVOPS_PAT=your-personal-access-token
```

Or create a `.env` file in the project root:

```
AZURE_DEVOPS_ORGANIZATION=your-organization-name
AZURE_DEVOPS_PAT=your-personal-access-token
```

### 3. Build the Project

```bash
./mvnw clean package
```

## Usage

### Running with Claude Desktop

1. Add the server configuration to your Claude Desktop config file:

**macOS/Linux**: `~/Library/Application Support/Claude/claude_desktop_config.json`

**Windows**: `%APPDATA%\Claude\claude_desktop_config.json`

```json
{
  "mcpServers": {
    "azure-devops": {
      "command": "java",
      "args": [
        "-jar",
        "/path/to/azure-devops-mcp-server-0.0.1-SNAPSHOT.jar",
        "--stdio"
      ],
      "env": {
        "AZURE_DEVOPS_ORGANIZATION": "your-organization-name",
        "AZURE_DEVOPS_PAT": "your-personal-access-token"
      }
    }
  }
}
```

2. Restart Claude Desktop

3. You should now see the Azure DevOps tools available in Claude Desktop's MCP panel

### Running Standalone

For testing or development:

```bash
java -jar target/azure-devops-mcp-server-0.0.1-SNAPSHOT.jar --stdio
```

The server will listen for JSON-RPC messages on stdin and respond on stdout.

## Example Usage

Once connected to Claude Desktop, you can use natural language to interact with your Azure DevOps organization:

- "List all projects in my Azure DevOps organization"
- "Show me the recent work items in the MyProject project"
- "Get details for work item 1234 in MyProject"
- "List all repositories in MyProject"
- "Show me the latest builds in MyProject"

## Tool Reference

### list_projects

List all projects in your Azure DevOps organization.

**Parameters:** None

**Example:**

```json
{
  "name": "list_projects",
  "arguments": {}
}
```

### get_project

Get detailed information about a specific project.

**Parameters:**

- `project` (string, required): Project name or ID

**Example:**

```json
{
  "name": "get_project",
  "arguments": {
    "project": "MyProject"
  }
}
```

### get_work_items

Query work items using WIQL (Work Item Query Language).

**Parameters:**

- `project` (string, required): Project name or ID
- `wiql` (string, optional): WIQL query. If not provided, gets recent work items.

**Example:**

```json
{
  "name": "get_work_items",
  "arguments": {
    "project": "MyProject",
    "wiql": "SELECT [System.Id], [System.Title], [System.State] FROM WorkItems WHERE [System.WorkItemType] = 'Bug' AND [System.State] = 'Active'"
  }
}
```

### get_work_item

Get detailed information about a specific work item.

**Parameters:**

- `project` (string, required): Project name or ID
- `id` (integer, required): Work item ID

**Example:**

```json
{
  "name": "get_work_item",
  "arguments": {
    "project": "MyProject",
    "id": 1234
  }
}
```

### get_repositories

List Git repositories in a project.

**Parameters:**

- `project` (string, required): Project name or ID

**Example:**

```json
{
  "name": "get_repositories",
  "arguments": {
    "project": "MyProject"
  }
}
```

### get_builds

Get recent builds from a project.

**Parameters:**

- `project` (string, required): Project name or ID
- `top` (integer, optional): Maximum number of builds to return (default: 10)

**Example:**

```json
{
  "name": "get_builds",
  "arguments": {
    "project": "MyProject",
    "top": 5
  }
}
```

## Development

### Building

```bash
./mvnw clean compile
```

### Testing

```bash
./mvnw test
```

### Running in Development Mode

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--stdio"
```

## Troubleshooting

### Authentication Issues

- Verify your Personal Access Token has the correct scopes
- Ensure the token hasn't expired
- Check that the organization name is correct

### Connection Issues

- Verify environment variables are set correctly
- Check network connectivity to dev.azure.com
- Ensure your organization URL is accessible

### MCP Integration Issues

- Verify the jar path in your Claude Desktop config is correct
- Check that Java 17+ is installed and accessible
- Restart Claude Desktop after configuration changes
- Check Claude Desktop logs for error messages

## License

MIT License - see LICENSE file for details
