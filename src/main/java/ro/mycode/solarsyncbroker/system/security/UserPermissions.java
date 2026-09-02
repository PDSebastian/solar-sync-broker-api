package ro.mycode.solarsyncbroker.system.security;

import lombok.Getter;

@Getter
public enum UserPermissions {
    HOUSE_VIEW,
    HOUSE_MANAGE,
    BATTERY_VIEW,
    BATTERY_MANAGE,
    BATTERY_CONFIG,
    BATTERY_COMMAND
}