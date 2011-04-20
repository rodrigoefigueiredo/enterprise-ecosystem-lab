# Identity Ecosystem

Enterprise IAM application built as a Java 6 modular monolith.

## Runtime

- Java 6
- Maven 3
- Tomcat 7
- PostgreSQL 9
- Spring Framework 3
- Spring MVC with JSP/JSTL
- Spring Security 3
- JPA 2 / Hibernate 3
- Flyway

The application is packaged as:

```text
identity-ecosystem.war
```

## Configuration

Required environment properties:

```text
IDENTITY_DB_URL
IDENTITY_DB_USERNAME
IDENTITY_DB_PASSWORD
IDENTITY_SESSION_TIMEOUT_MINUTES
```

Initial administrator bootstrap properties:

```text
IDENTITY_BOOTSTRAP_ADMIN_EMAIL
IDENTITY_BOOTSTRAP_ADMIN_PASSWORD
IDENTITY_BOOTSTRAP_ADMIN_DISPLAY_NAME
```

Bootstrap runs only when no `ROLE_ADMIN` exists. The password must contain at least 12 characters. After the first administrator is created, remove these bootstrap properties before restarting the application.

## Build

```text
mvn clean package
```

Deploy `target/identity-ecosystem.war` to Tomcat 7.

## First Administrator

1. Configure database properties.
2. Configure bootstrap administrator properties.
3. Start the application.
4. Confirm that the administrator can sign in.
5. Remove bootstrap administrator properties.
6. Restart the application.

## Web Flow

```text
/login
/me
/users/new
/logout
```

Only users with `ROLE_ADMIN` can access `/users/new` and create users.

New users receive `ROLE_USER`.

Users with `ROLE_USER` can sign in and access `/me`.

## SOAP

Initial SOAP endpoint:

```text
/ws
```

WSDL:

```text
/ws/identity.wsdl
```

Initial operations:

```text
CreateUser
ChangeUserState
GetCurrentUser
```

SOAP administration operations are protected with `ROLE_ADMIN`. `ChangeUserState` accepts `ACTIVE`, `SUSPENDED`, `LOCKED`, and `INACTIVE`. `GetCurrentUser` returns the authenticated user's public ID, e-mail, display name, status, and authorities.

## Audit

Current audited events include:

```text
USER_CREATED
USER_STATE_CHANGED
ADMIN_BOOTSTRAPPED
AUTHENTICATION_SUCCEEDED
AUTHENTICATION_FAILED
SESSION_CREATED
SESSION_REVOKED
LOGOUT_SUCCEEDED
```

Passwords, password hashes, cookies, and session identifiers must not be written to audit records or application logs.
