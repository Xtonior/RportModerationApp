package kz.lab.moderationapp.model;

import org.apache.kafka.common.Uuid;

public record ClientData(Uuid cleintId, String userDepartment) {
    
}
