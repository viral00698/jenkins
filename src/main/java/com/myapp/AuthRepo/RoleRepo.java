package com.myapp.AuthRepo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myapp.AuthModel.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long>{

	List<Role> findByUserId(Long userId);
}
