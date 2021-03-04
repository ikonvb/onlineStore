package com.konstantinbulygin.onlinestore.model;


import com.konstantinbulygin.onlinestore.model.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String userName);

}
