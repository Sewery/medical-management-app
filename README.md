# Medical Clinic Management System

Full-stack medical clinic application with a production-style DevOps setup on GCP.

The system supports doctor, patient, consulting room, schedule, and visit management. The backend is deployed to Google Cloud Run, infrastructure is managed with Terraform, and deployments are automated with GitHub Actions.

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

Use this command to print the current live backend URL:

```bash
gcloud run services describe medical-clinic-api --region europe-central2 --format="value(status.url)"
```

## Core Features

- Doctor management with detailed profiles and medical specializations
- Patient registry with personal and address data
- Consulting room and equipment tracking
- Schedule management with availability checks and conflict prevention
- Visit booking and cancellation

## Local Development

### Prerequisites

- JDK 21+
- Node.js + npm
- Docker (optional, for container tests)

### Backend

From `backend-spring-boot`:

```bash
# Linux/macOS
./gradlew clean test bootRun

# Windows (PowerShell)
.\gradlew.bat clean test bootRun
```

Backend runs on `http://localhost:8080`.

### Frontend

From `frontend`:

```bash
npm install
npm start
```

Frontend runs on `http://localhost:3000`.

Optional local API target override:

```bash
REACT_APP_API_BASE_URL=http://localhost:8080
```

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
2. Builds Docker image
3. Pushes image to Artifact Registry
4. Deploys to Cloud Run
5. Routes traffic to the latest revision and cleans up old revisions

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

- Required GCP APIs (Run, Artifact Registry, Cloud Build)
- Artifact Registry repository
- Cloud Run runtime service account and IAM binding
- Cloud Run service and public invoker IAM

Quick start:

```bash
cd infra/environments/dev
terraform init
terraform plan
terraform apply
```

If resources already exist from manual setup, import them first (see `infra/README.md`).
