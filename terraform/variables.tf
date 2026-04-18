variable "jwt_secret" {
  description = "JWT secret"
  type        = string
  sensitive   = true
}

variable "users_db_password" {
  type = string
  sensitive = true
}

variable "appointments_db_password" {
  type = string
  sensitive = true
}
