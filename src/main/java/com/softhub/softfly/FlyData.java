package com.softhub.softfly;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class FlyData {

    @Getter
    private UUID userId;
    @Getter
    @Setter
    private Boolean enabled;
    @Getter
    @Setter
    private Integer remainingTime;

    public FlyData(UUID userId, Boolean enabled, Integer remainingTime) {
        this.userId = userId;
        this.enabled = enabled;
        this.remainingTime = remainingTime;
    }

}
