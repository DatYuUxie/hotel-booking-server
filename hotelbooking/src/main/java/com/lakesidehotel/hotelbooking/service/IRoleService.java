package com.lakesidehotel.hotelbooking.service;

import com.lakesidehotel.hotelbooking.model.Role;
import com.lakesidehotel.hotelbooking.model.User;

import java.util.List;

public interface IRoleService {
    List<Role> getRoles();
    Role createRole(Role theRole);
    void deleteRole(Long roleId);
    Role findByName(String name);
    User removeUserFromRole(Long userId, Long roleId);
    User assignRoleToUser(Long userId, Long roleId);
    Role removeAllUsersFromRole(Long roleId);
}
