package com.quickshort.workspace.service;

import com.quickshort.common.payload.UserPayload;
import com.quickshort.workspace.models.User;

public interface UserService {
    User createUser(UserPayload payload);
}
