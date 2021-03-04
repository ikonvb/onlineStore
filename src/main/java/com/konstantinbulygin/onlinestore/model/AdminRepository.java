package com.konstantinbulygin.onlinestore.model;

import com.konstantinbulygin.onlinestore.model.data.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Admin findByUsername(String userName);
}
