package com.thoughtmechanix.organizationservice.services;

import com.thoughtmechanix.organizationservice.events.source.SimpleSourceBean;
import com.thoughtmechanix.organizationservice.model.Organizations;
import com.thoughtmechanix.organizationservice.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationService {
    @Autowired
    private OrganizationRepository orgRepository;

    @Autowired
    private SimpleSourceBean simpleSourceBean;

    public Organizations getOrg(String organizationId) {
        Optional<Organizations> org = orgRepository.findById(organizationId);
        List<Organizations> organizationList = orgRepository.findAll();
        Organizations organization = new Organizations();
        if(org.isPresent()) {
            organization = org.get();
        }
        return organization;
    }

    public void saveOrg(Organizations org){
        org.setId( UUID.randomUUID().toString());

        orgRepository.save(org);
        simpleSourceBean.publishOrgChange("SAVE", org.getId());
    }

    public void updateOrg(Organizations org){
        orgRepository.save(org);
    }

    public void deleteOrg(Organizations org){
        orgRepository.delete(org);
    }
}
