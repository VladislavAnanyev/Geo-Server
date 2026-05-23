package com.example.meetings.subscription.controller;

import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.subscription.facade.SubscriptionFacade;
import com.example.meetings.subscription.model.dto.input.GrantSubscriptionRequest;
import com.example.meetings.subscription.model.dto.output.SubscriptionResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class ApiAdminSubscriptionController {

    private final SubscriptionFacade subscriptionFacade;

    @ApiOperation("Выдать или продлить премиум-подписку пользователю")
    @PostMapping("/users/{userId}/subscription")
    public SubscriptionResponse grantSubscription(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                                  @PathVariable Long userId,
                                                  @RequestBody @Valid GrantSubscriptionRequest request) {
        return new SubscriptionResponse(
                subscriptionFacade.grantSubscription(authUser.getUserId(), userId, request)
        );
    }
}
