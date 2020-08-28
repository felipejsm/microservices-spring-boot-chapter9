package com.thoughtmechanix.organizationservice.controllers;


import com.thoughtmechanix.organizationservice.model.Organizations;
import com.thoughtmechanix.organizationservice.services.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping(value="v1/organizations")
public class OrganizationServiceController {
    private static final Logger log = LoggerFactory.getLogger(OrganizationServiceController.class);
    @Autowired
    private OrganizationService orgService;


    @RequestMapping(value="/{organizationId}",method = RequestMethod.GET)
    public Organizations getOrganization( @PathVariable("organizationId") String organizationId) {
        log.info("heeeello");
        return orgService.getOrg(organizationId);
    }

    @RequestMapping(value="/{organizationId}",method = RequestMethod.PUT)
    public void updateOrganization( @PathVariable("organizationId") String orgId, @RequestBody Organizations org) {
        orgService.updateOrg( org );
    }

    @RequestMapping(value="/{organizationId}",method = RequestMethod.POST)
    public void saveOrganization(@RequestBody Organizations org) {
       orgService.saveOrg( org );
    }

    @RequestMapping(value="/{organizationId}",method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrganization( @PathVariable("orgId") String orgId,  @RequestBody Organizations org) {
        orgService.deleteOrg( org );
    }
}
