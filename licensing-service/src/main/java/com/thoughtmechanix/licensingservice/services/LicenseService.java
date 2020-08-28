package com.thoughtmechanix.licensingservice.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.thoughtmechanix.licensingservice.clients.OrganizationDiscoveryClient;
import com.thoughtmechanix.licensingservice.clients.OrganizationFeignClient;
import com.thoughtmechanix.licensingservice.clients.OrganizationRestTemplateClient;
import com.thoughtmechanix.licensingservice.config.ServiceConfig;
import com.thoughtmechanix.licensingservice.model.License;
import com.thoughtmechanix.licensingservice.model.Organization;
import com.thoughtmechanix.licensingservice.repository.LicenseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class LicenseService {
    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private ServiceConfig serviceConfig;


    @Autowired
    OrganizationFeignClient organizationFeignClient;

    @Autowired
    OrganizationRestTemplateClient organizationRestClient;

    @Autowired
    OrganizationDiscoveryClient organizationDiscoveryClient;
    private Organization retrieveOrgInfo(String organizationId, String clientType) {
            Organization organization = null;
            switch (clientType) {
                case "feign" :
                    System.out.println("I am using the feign client");
                    organization = organizationFeignClient.getOrganization(organizationId);
                    break;
                case "rest" :
                    System.out.println("I am using the rest client");
                    organization = organizationRestClient.getOrganization(organizationId);
                    break;
                case "discovery" :
                    System.out.println("I am using the discovery client");
                    organization = organizationDiscoveryClient.getOrganization(organizationId);
                    break;
                default:
                    organization = organizationRestClient.getOrganization(organizationId);
            }
        return organization;
    }
    public License getLicense(String organizationId, String licenseId, String clientType) {
        License license = this.licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
        Organization organization = retrieveOrgInfo(organizationId, clientType);
        return license
                .withOrganizationName( organization.getName())
                .withContactName( organization.getContactName())
                .withContactEmail( organization.getContactEmail() )
                .withContactPhone( organization.getContactPhone() )
                .withComment(this.serviceConfig.getExampleProperty());
    }
    private void randomlyRunLong() {
        Random rand = new Random();
        int randomNum = rand.nextInt((3-1)+1)+1;
        if(randomNum == 3) sleep();
    }
    private void sleep() {
        try {
            Thread.sleep(11000L);
        } catch(InterruptedException e) {
            e.printStackTrace();;
        }
    }
    @HystrixCommand(
        /*    commandProperties = {
                    @HystrixProperty(
                            name="execution.isolation.thread.timeoutInMilliseconds",
                            value ="12000"
                    )
            },*/
            fallbackMethod = "buildFallbackLicenseList",
            threadPoolKey = "licenseByOrgThreadPool",
            threadPoolProperties = {
                    @HystrixProperty(name="coreSize", value="30"),
                    @HystrixProperty(name="maxQueueSize", value="10")
            },
            commandProperties = {
                    @HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value="10"),
                    @HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value="75"),
                    @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds",value="7000"),
                    @HystrixProperty(name="metrics.rollingStats.timeInMilliseconds", value="15000"),
                    @HystrixProperty(name="metrics.rollingStats.numBuckets",value="5")
            }

    )
    @Cacheable("organization")
    public List<License> getLicensesByOrg(String organizationId) {
        randomlyRunLong();// método levará 11 segundos, extrapolando o 1s de tolerância
        return this.licenseRepository.findByOrganizationId(organizationId);
    }

    private List<License> buildFallbackLicenseList(String organizationId) {
        List<License> fallbackList = new ArrayList<>();
        License license = new License()
                .withId("0000000000000-00-000000")
                .withOrganizationId(organizationId)
                .withProductName("Sorry no licensing information currently available");
        fallbackList.add(license);
        return fallbackList;
    }

    public void saveLicense(License license) {
        license.withId(UUID.randomUUID().toString());
        licenseRepository.save(license);
    }

    public void updateLicense(License license){
        licenseRepository.save(license);
    }

    public void deleteLicense(License license){
        licenseRepository.delete(license);
    }
}
