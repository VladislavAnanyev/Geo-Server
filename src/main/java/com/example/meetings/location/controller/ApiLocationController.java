package com.example.meetings.location.controller;

import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.location.facade.LocationFacade;
import com.example.meetings.location.model.dto.LocationDTO;
import com.example.meetings.location.model.dto.LocationRequest;
import com.example.meetings.location.model.dto.LocationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1")
public class ApiLocationController {

    private final LocationFacade locationFacade;

    @PostMapping(path = "/create")
    public LocationResponse createLocation(@RequestBody LocationRequest request, @AuthenticationPrincipal AuthUserDetails authUserDetails) {
        LocationDTO location = locationFacade.createLocation(request, authUserDetails);
        return new LocationResponse().setLocations(List.of(location));
    }

    @GetMapping("/locations")
    public LocationResponse getLocations() {
        return new LocationResponse().setLocations(locationFacade.getLocations());
    }

}
