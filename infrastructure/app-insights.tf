resource "azurerm_application_insights" "appinsights" {
  name                = "${var.product}-${var.component}-appinsights-${var.env}"
  location            = var.location
  resource_group_name = azurerm_resource_group.civil_sdt_rg.name
  application_type    = "web"

  tags                = var.common_tags

  lifecycle {
    ignore_changes = [
      # Ignore changes to appinsights as otherwise upgrading to the Azure provider 2.x
      # destroys and re-creates this appinsights instance
      application_type,
    ]
  }
}

resource "azurerm_key_vault_secret" "appinsights-connection-string" {
  name         = "civil-sdt-appinsights-connection-string"
  value        = azurerm_application_insights.appinsights.connection_string
  key_vault_id = module.civil_sdt_key_vault.key_vault_id
}
