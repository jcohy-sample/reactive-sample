package com.jcohy.sample.reactive.chapter_06.security;

import reactor.core.publisher.Mono;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:12:36
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1")
public class SecuredProfileController {

    private final ProfileService profileService;

    public SecuredProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @RequestMapping("/profiles")
    public Mono<Profile> getProfile() {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> profileService.getByUser(auth.getName()));
    }
}
