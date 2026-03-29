output "service_url" {
  description = "Public URL of Cloud Run service"
  value       = google_cloud_run_v2_service.api.uri
}

output "container_image" {
  description = "Configured image URI"
  value       = local.image_uri
}
