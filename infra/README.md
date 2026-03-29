# Terraform setup

This folder contains Terraform configuration for GCP infrastructure used by the backend service.

## Scope (dev)

- API enablement: Cloud Run, Artifact Registry, Cloud Build
- Artifact Registry Docker repository
- Cloud Run runtime service account and IAM binding
- Cloud Run service and public invoker policy

## Usage

1. Go to environment folder:

   ```powershell
   cd infra/environments/dev
   ```

2. Create variables file:

   ```powershell
   copy terraform.tfvars.example terraform.tfvars
   ```

3. Initialize and inspect:

   ```powershell
   terraform init
   terraform plan
   ```

4. Apply:

   ```powershell
   terraform apply
   ```

## Import existing resources

If resources already exist from manual deployment, import them first to avoid recreation.

```powershell
terraform import google_artifact_registry_repository.repo "projects/medclinic-platform-gcp/locations/europe-central2/repositories/medical-clinic-repo"
terraform import google_service_account.runtime "projects/medclinic-platform-gcp/serviceAccounts/medical-clinic-run-sa@medclinic-platform-gcp.iam.gserviceaccount.com"
terraform import google_cloud_run_v2_service.api "projects/medclinic-platform-gcp/locations/europe-central2/services/medical-clinic-api"
```
