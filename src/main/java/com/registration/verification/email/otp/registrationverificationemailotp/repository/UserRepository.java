package com.registration.verification.email.otp.registrationverificationemailotp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.registration.verification.email.otp.registrationverificationemailotp.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

	@Query("SELECT u FROM User u WHERE u.emailOrMobileno = ?1")
	User findByEmailOrMobileno(String emailOrMobileno);
	
	@Transactional
    @Modifying
    @Query("UPDATE User a " +
            "SET a.enabled = TRUE WHERE a.emailOrMobileno = ?1")
    int enableAppUser(String email);
	
	@Transactional
    @Modifying
    @Query("UPDATE User a " +
            "SET a.enabled = TRUE WHERE a.emailOrMobileno = ?1")
    int enableAppUserMobileNo(String mobileNumber);
}
