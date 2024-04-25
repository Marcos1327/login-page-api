package com.estudos.loginauthapi.repositories;

import com.estudos.loginauthapi.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
