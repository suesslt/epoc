package com.jore.epoc.repositories;

import org.springframework.data.repository.CrudRepository;

import com.jore.epoc.bo.user.UserInCompanyRole;

public interface UserInCompanyRoleRepository extends CrudRepository<UserInCompanyRole, Long> {
    Iterable<UserInCompanyRole> findByIsInvitationRequired(boolean isInvitationRequired);
}
