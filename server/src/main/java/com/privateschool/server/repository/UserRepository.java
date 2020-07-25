package com.privateschool.server.repository;

import com.privateschool.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
