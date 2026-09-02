package ro.mycode.solarsyncbroker.battery.command.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "command_executions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "house_id", nullable = false)
    private Long houseId;

    @Column(name = "command_type", nullable = false)
    private String commandType;

    @Column(name = "target_soc", nullable = false)
    private Integer targetSoc;

    @Column(nullable = false)
    private String status;

    @Column(name = "executed_by", nullable = false)
    private String executedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}