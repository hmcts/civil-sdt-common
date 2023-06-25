output "vaultName" {
  value = module.civil_sdt_key_vault.key_vault_name
}

output "vaultUri" {
  value = module.civil_sdt_key_vault.key_vault_uri
}

output "sb_primary_send_and_listen_connection_string" {
  value     = module.servicebus-namespace.primary_send_and_listen_connection_string
  sensitive = true
}

output "sb_primary_send_and_listen_shared_access_key" {
  value     = module.servicebus-namespace.primary_send_and_listen_shared_access_key
  sensitive = true
}
