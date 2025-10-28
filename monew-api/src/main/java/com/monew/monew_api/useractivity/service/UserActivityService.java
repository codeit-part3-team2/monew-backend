package com.monew.monew_api.useractivity.service;

import com.monew.monew_api.useractivity.dto.UserActivityDto;


public interface UserActivityService {
    UserActivityDto getUserActivity(String userId);
    UserActivityDto getUserActivitySingleQuery(String userId);
}