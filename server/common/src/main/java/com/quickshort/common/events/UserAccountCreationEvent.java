package com.quickshort.common.events;

import com.quickshort.common.payload.UserPayload;
import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountCreationEvent {
    private String key;
    private String message;
    private String status;
    private UserPayload userPayload;
}
