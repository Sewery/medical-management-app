locals {
  image_uri = "${var.region}-docker.pkg.dev/${var.project_id}/${var.repository_id}/${var.image_name}:${var.image_tag}"
}

resource "google_project_service" "run_api" {
  service = "run.googleapis.com"
}

resource "google_project_service" "artifactregistry_api" {
  service = "artifactregistry.googleapis.com"
}

resource "google_project_service" "cloudbuild_api" {
  service = "cloudbuild.googleapis.com"
}

resource "google_artifact_registry_repository" "repo" {
  location      = var.region
  repository_id = var.repository_id
  description   = "Docker repository for medical clinic backend"
  format        = "DOCKER"

  depends_on = [google_project_service.artifactregistry_api]
}

resource "google_service_account" "runtime" {
  account_id   = "medical-clinic-run-sa"
  display_name = "Medical Clinic Cloud Run runtime SA"
}

resource "google_project_iam_member" "runtime_artifact_reader" {
  project = var.project_id
  role    = "roles/artifactregistry.reader"
  member  = "serviceAccount:${google_service_account.runtime.email}"
}

resource "google_cloud_run_v2_service" "api" {
  name     = var.service_name
  location = var.region
  ingress  = "INGRESS_TRAFFIC_ALL"

  template {
    service_account = google_service_account.runtime.email

    scaling {
      min_instance_count = 0
      max_instance_count = 2
    }

    containers {
      image = local.image_uri

      ports {
        container_port = 8080
      }

      env {
        name  = "PORT"
        value = "8080"
      }
    }
  }

  depends_on = [
    google_project_service.run_api,
    google_artifact_registry_repository.repo
  ]
}

resource "google_cloud_run_v2_service_iam_member" "public_invoker" {
  location = var.region
  name     = google_cloud_run_v2_service.api.name
  role     = "roles/run.invoker"
  member   = "allUsers"
}
