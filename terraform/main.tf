terraform {
  required_version = ">= 1.5.0"

  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 3.1"
    }
  }
}

provider "kubernetes" {
  config_path = var.kubeconfig_path
}

resource "kubernetes_namespace_v1" "medical_record" {
  metadata {
    name = "medical-record"
  }
}

resource "kubernetes_secret_v1" "auth_secret" {
  metadata {
    name      = "auth-secret"
    namespace = kubernetes_namespace_v1.medical_record.metadata[0].name
  }

  data = {
    JWT_SECRET = base64encode(var.jwt_secret)
  }

  type = "Opaque"
}

resource "kubernetes_secret_v1" "users_secret" {
  metadata {
    name      = "users-secret"
    namespace = kubernetes_namespace_v1.medical_record.metadata[0].name
  }

  data = {
    USERS_DB_PASSWORD = base64encode(var.users_db_password)
  }

  type = "Opaque"
}

resource "kubernetes_secret_v1" "appointments_secret" {
  metadata {
    name      = "appointments-secret"
    namespace = kubernetes_namespace_v1.medical_record.metadata[0].name
  }

  data = {
    APPOINTMENTS_DB_PASSWORD = base64encode(var.appointments_db_password)
  }

  type = "Opaque"
}
