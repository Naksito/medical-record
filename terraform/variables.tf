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

variable "admin_email" {
  description = "Initial administrator email"
  type        = string
}

variable "admin_password" {
  description = "Initial administrator password"
  type        = string
  sensitive   = true
}

variable "postgres_user" {
  description = "PostgreSQL application user"
  type        = string
}

variable "users_db_password" {
  description = "Shared PostgreSQL password used by both application services"
  type        = string
  sensitive   = true
}
