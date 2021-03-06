package com.zavala.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zavala.dscatalog.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	//busca no banco um usuário X e-mail
	User findByEmail(String email);
}
