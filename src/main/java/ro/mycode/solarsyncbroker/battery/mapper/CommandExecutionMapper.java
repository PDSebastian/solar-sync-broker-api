package ro.mycode.solarsyncbroker.battery.mapper;


import ro.mycode.solarsyncbroker.battery.command.model.CommandExecution;
import ro.mycode.solarsyncbroker.battery.dtos.CommandExecutionResponse;

public class CommandExecutionMapper {
    public static CommandExecutionResponse commandExecutionToResponse(CommandExecution entity) {
        if (entity == null) {
            return null;
        }
        return new CommandExecutionResponse(
                entity.getId(),
                entity.getHouseId(),
                entity.getCommandType(),
                entity.getTargetSoc(),
                entity.getStatus(),
                entity.getExecutedBy(),
                entity.getCreatedAt()
        );
    }
}