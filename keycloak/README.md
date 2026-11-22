# Keycloak Setup for microservice project

This file describes how Keycloak is configured and how to use it with the provided microservice demo.

## Overview
- Keycloak container is defined in `docker-compose.yml` and mapped to `localhost:9090`.
- Postgres is used as Keycloak DB (`postgres:16`) and Keycloak uses the database via environment variables.
- A sample realm, clients, users and roles are provided in `keycloak/realm-export.json` and imported on startup via `KEYCLOAK_IMPORT`.

## Built-in realm & credentials
- Realm: `keni`
- Users:
-  - admin / admin (role: admin)
-  - user / password (role: user)
- Clients:
  - `apigateway` (public: false; secret: `apigateway-secret`, redirectUri: `http://localhost:8080/*`)
  - `orderservice` (bearerOnly; secret: `orderservice-secret`)
  - `userservice` (bearerOnly; secret: `userservice-secret`)

## Start commands
1. Start Postgres and Keycloak using Docker Compose:
   ```powershell
   cd D:\Java\microservice
   docker compose up -d postgres keycloak
   ```
2. Check Keycloak logs until it shows `Started Keycloak`  or the realm is imported:
   ```powershell
   docker compose logs -f keycloak
   ```
3. Admin console is available at `http://localhost:9090`.
   - Login with `admin` / `admin`.

## Get a token for testing (script example)
From PowerShell, get a JWT access token using the `apigateway` client (direct grant):

```powershell
$token = (Invoke-RestMethod -Method Post -Uri "http://localhost:9090/realms/keni/protocol/openid-connect/token" -Body @{
    grant_type = 'password';
    client_id = 'apigateway';
    client_secret = 'apigateway-secret';
    username = 'user';
    password = 'password'
}).access_token

Write-Output $token
```

## Test a protected service
- Use the token in `Authorization` header:

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8082/users" -Headers @{ Authorization = "Bearer $token" }
```
(Adjust service port and endpoint accordingly)

## Notes & Next steps
- The `orderservice` and `userservice` are configured as resource servers (JWT validation) and will validate tokens using the Keycloak realm JWKS endpoint.
- The API Gateway is also configured as a resource server and will require valid JWTs from Keycloak to access its endpoints.
- You can expand the realm JSON to add more roles, groups, or clients as needed for your microservice exercises.
- For production, do not store admin credentials or secrets in plaintext; use Docker secrets or a secret management solution.
