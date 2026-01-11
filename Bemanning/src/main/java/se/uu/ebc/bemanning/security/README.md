# Bemanning Security Configuration

This directory contains the security configuration for the Bemanning application, which integrates with CAS (Central Authentication Service) for authentication.

## Components

### BemanningSecurityConfig.java
Full-featured Spring Security configuration class that:
- Configures CAS authentication for web interface
- Sets up different authentication handling for REST endpoints
- Implements role-based access control
- Provides Single Sign-Out support
- Requires Apereo CAS client library

### BemanningSecurityConfigBasic.java
Simplified Spring Security configuration class that:
- Provides basic CAS authentication
- Doesn't require full Apereo CAS client library
- Activated with `spring.profiles.active=basic`
- Good fallback if dependency issues occur

### Security Classes
- **AuditorAwareImpl**: Provides current user information for JPA auditing
- **BemanningUser**: Extended Spring Security User class with department mapping
- **BemanningUserService**: Interface for user details service
- **RESTAuthenticationEntryPoint**: Custom entry point that handles both CAS and REST authentication
- **SecurityService**: Implementation of user details service that loads users from database
- **SecurityServiceException**: Custom exception for security service errors
- **UserRepo**: Repository for user/person entities

## Configuration Properties

The following properties should be configured in your `application.properties` or environment:

```properties
# CAS Server Configuration
cas.server.url=https://cas.uu.se/cas
cas.server.login.url=https://cas.uu.se/cas/login
cas.server.logout.url=https://cas.uu.se/cas/logout

# Application Server Configuration
app.server.url=http://localhost:8080
app.service.security=bemanning
```

## Security Rules

### Web Interface
- `/` - Public access
- `/login`, `/logout` - Public access
- Static resources (`/css/**`, `/js/**`, etc.) - Public access
- `/admin/**` - Requires ADMIN role
- All other pages - Requires authentication via CAS

### REST API
- `/rest/**` - Requires authentication
- Returns HTTP 401 for unauthenticated requests (no CAS redirect)

## Usage

### For Web Users
1. Access any protected page
2. Automatically redirected to CAS login
3. After successful authentication, redirected back to original page
4. Use `/logout` to sign out (will also sign out from CAS)

### For REST API Clients
1. Must authenticate separately (e.g., via API key, JWT, etc.)
2. Will receive HTTP 401 responses for unauthenticated requests
3. No automatic CAS redirects

## Dependencies

Make sure your `pom.xml` includes:

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-cas</artifactId>
</dependency>
<dependency>
    <groupId>org.apereo.cas.client</groupId>
    <artifactId>cas-client-core</artifactId>
    <version>3.6.4</version>
</dependency>
```

Note: The CAS client library moved from `org.jasig.cas.client` to `org.apereo.cas.client` in newer versions.

### Alternative: Basic Configuration

If you encounter dependency issues with the Apereo CAS client, you can use the basic configuration by setting:

```properties
spring.profiles.active=basic
```

This will activate `BemanningSecurityConfigBasic` which only requires the Spring Security CAS module.

## Testing the Security Configuration

A test controller is available to verify the security setup. Enable it by setting:

```properties
security.test.enabled=true
```

### Test Endpoints

- `GET /security-test/public` - Public endpoint (no authentication required)
- `GET /security-test/authenticated` - Requires authentication
- `GET /security-test/admin` - Requires ADMIN role

These endpoints return JSON with authentication status and user information.

## Development Notes

- The configuration uses property placeholders for flexibility across environments
- REST endpoints are handled differently to avoid CAS redirects for API calls
- Single Sign-Out is configured to work with CAS server
- Session management is configured for security best practices
- CSRF protection is disabled for REST endpoints but enabled for web interface