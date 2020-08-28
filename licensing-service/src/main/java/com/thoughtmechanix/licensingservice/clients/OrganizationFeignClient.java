package com.thoughtmechanix.licensingservice.clients;

import com.thoughtmechanix.licensingservice.model.Organization;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("organizationservice")
public interface OrganizationFeignClient {
    @GetMapping("/v1/organizations/{organizationId}")
    Organization getOrganization(@PathVariable("organizationId") String organizationId);
}
