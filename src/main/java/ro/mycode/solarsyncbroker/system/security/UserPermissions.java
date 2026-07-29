package ro.mycode.solarsyncbroker.system.security;


import lombok.Getter;
@Getter
public enum UserPermissions {
    HOUSE_VIEW("house:view"),
    HOUSE_MANAGE("house:manage"),
    BATTERY_VIEW("battery:view"),
    BATTERY_MANAGE("battery:manage");

    private String permission;
    UserPermissions(String permission) {
        this.permission = permission;
    }





}
