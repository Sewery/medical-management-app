# Medical Clinic Management System

Full-stack medical clinic application with a production-style DevOps setup on GCP.

The system supports doctor, patient, consulting room, schedule, and visit management. The backend is deployed to Google Cloud Run, infrastructure is managed with Terraform, and deployments are automated with GitHub Actions.

This repository is primarily backend-focused (Spring Boot + domain modules), with a React frontend used as an API client.

## Architecture Summary

- Frontend: React + Tailwind CSS
- Backend: Spring Boot 3 (Java 21)
- Data layer (local dev): H2 + Spring Data JPA
- Containerization: Docker
- Cloud runtime: Google Cloud Run
- Container registry: Artifact Registry
- Infrastructure as Code: Terraform
- CI/CD: GitHub Actions with OIDC authentication to GCP

## Live Deployment

- Platform: Google Cloud Run
- Region: europe-central2
- Service: `medical-clinic-api`
- OpenAPI JSON: `/v3/api-docs`
- Swagger UI: `/swagger-ui/index.html`

Note: the backend endpoint is resolved dynamically during CI/CD (instead of being hardcoded in the repository), so the exact URL may change between deployments.

## Core Features

- Doctor management with detailed profiles and medical specializations
- Patient registry with personal and address data
- Consulting room and equipment tracking
- Schedule management with availability checks and conflict prevention
- Visit booking and cancellation

## Backend Engineering Notes

- Layered backend structure: `presentation`, `application`, `domain`, `infrastructure`
- DTO validation with Bean Validation (`@Valid`, constraints in request models)
- Centralized REST error mapping via global exception handler
- Repository-based persistence with Spring Data JPA
- Local data lifecycle helpers for development and testing (`DatabaseInitializer`, `DatabaseClean`)

## Local Development Notes

### Prerequisites

- JDK 21+
- Node.js + npm
- Docker (optional, for container tests)

### Backend

- Module: `backend-spring-boot`
- Build tool: Gradle Wrapper
- Default local API port: `8080`

### Frontend

- Module: `frontend`
- Tooling: React Scripts + npm
- Default local UI port: `3000`
- API base can be overridden with `REACT_APP_API_BASE_URL`

## API Overview

- `/doctors` - CRUD operations for medical staff
- `/patients` - patient records management
- `/consulting-room` - room equipment and room data
- `/schedules` - shifts and availability checks
- `/visits` - visit availability, booking, cancellation

## Database Utilities (Local Only)

The project includes helper tools intended for local/dev usage:

- `DatabaseInitializer` - clears and seeds sample data
- `DatabaseClean` - wipes data from repositories

These tools should not be used against production data.

## CI/CD Pipeline

### Backend workflow

Workflow file: `.github/workflows/backend-cloudrun.yml`

On push to `main` (backend changes), the pipeline:

1. Runs backend tests
2. Builds Docker image in GitHub Actions runner
3. Pushes image to Artifact Registry
4. Deploys to Cloud Run
5. Routes traffic to the latest ready revision

Revision cleanup is configured for this project to minimize retained inactive revisions. In production systems, keeping at least one rollback revision is usually recommended.

Authentication uses GitHub OIDC and Workload Identity Federation (no long-lived JSON key in repo).

Required GitHub repository secrets:

- `GCP_PROJECT_ID`
- `GCP_SERVICE_ACCOUNT_EMAIL`
- `GCP_WORKLOAD_IDENTITY_PROVIDER`

### Frontend workflow

Workflow file: `.github/workflows/frontend-pages.yml`

The frontend workflow runs after successful completion of the backend workflow and can also be triggered manually.

During build, it authenticates to GCP via OIDC, resolves the current backend Cloud Run service URL, and injects it as `REACT_APP_API_BASE_URL`.

This keeps frontend and backend deployments consistent without manually maintaining a separate frontend API URL variable.

## Infrastructure as Code

Terraform configuration is in `infra/environments/dev`.

Managed resources include:

- Required GCP APIs (Run, Artifact Registry)
- Artifact Registry repository
- Cloud Run runtime service account and IAM binding
- Cloud Run service and public invoker IAM

For implementation and execution details (including import flow for existing resources), see `infra/README.md`.
