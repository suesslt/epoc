package com.jore.epoc.repositories;

import org.springframework.data.repository.CrudRepository;

import com.jore.epoc.bo.UserInCompanyRole;

public interface UserInCompanyRoleRepository extends CrudRepository<UserInCompanyRole, Integer> {
    Iterable<UserInCompanyRole> findByIsInvitationRequired(boolean isInvitationRequired);
}
