package com.thoughtmechanix.licensingservice.repository;

import com.thoughtmechanix.licensingservice.model.Organization;
import org.springframework.data.repository.CrudRepository;

public interface OrganizationRepository extends CrudRepository<Organization, String> {
}
