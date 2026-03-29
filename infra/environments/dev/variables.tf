variable "project_id" {
  description = "GCP project id"
  type        = string
}

variable "region" {
  description = "GCP region"
  type        = string
  default     = "europe-central2"
}

variable "service_name" {
  description = "Cloud Run service name"
  type        = string
  default     = "medical-clinic-api"
}

variable "repository_id" {
  description = "Artifact Registry repository id"
  type        = string
  default     = "medical-clinic-repo"
}

variable "image_name" {
  description = "Container image name"
  type        = string
  default     = "backend"
}

variable "image_tag" {
  description = "Container image tag"
  type        = string
  default     = "latest"
}
