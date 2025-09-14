package com.mwu.profileservice.repository;

import com.mwu.profileservice.entity.Address;
import com.mwu.profileservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserId(UserProfile userId);
}
