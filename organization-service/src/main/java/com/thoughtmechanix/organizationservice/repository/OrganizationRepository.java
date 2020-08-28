package com.thoughtmechanix.organizationservice.repository;

import com.thoughtmechanix.organizationservice.model.Organizations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OrganizationRepository extends JpaRepository<Organizations,String> {
}
