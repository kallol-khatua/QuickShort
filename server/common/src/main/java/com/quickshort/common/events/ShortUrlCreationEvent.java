package com.quickshort.common.events;

import com.quickshort.common.payload.ShortUrlPayload;
import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ShortUrlCreationEvent {
    private String key;
    private String message;
    private String status;
    private ShortUrlPayload shortUrlPayload;
}
