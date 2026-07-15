variable "kubeconfig_path" {
  description = "Path to the kubeconfig file used by Terraform"
  type        = string
  default     = "~/.kube/config"
}

variable "jwt_secret" {
  description = "JWT secret"
  type        = string
  sensitive   = true
}

variable "users_db_password" {
  type      = string
  sensitive = true
}

variable "appointments_db_password" {
  type      = string
  sensitive = true
}
