package com.example.meetings.subscription.controller;

import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.subscription.facade.SubscriptionFacade;
import com.example.meetings.subscription.model.dto.output.SubscriptionResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/v1/subscription")
@RequiredArgsConstructor
public class ApiSubscriptionController {

    private final SubscriptionFacade subscriptionFacade;

    @ApiOperation("Статус моей премиум-подписки")
    @GetMapping("/me")
    public SubscriptionResponse getMySubscription(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        return new SubscriptionResponse(subscriptionFacade.getMySubscription(authUser.getUserId()));
    }
}
