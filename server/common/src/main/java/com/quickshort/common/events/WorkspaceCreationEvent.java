package com.quickshort.common.events;

import com.quickshort.common.payload.WorkspacePayload;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WorkspaceCreationEvent {
    private String key;
    private String message;
    private String status;
    private WorkspacePayload workspacePayload;
}
