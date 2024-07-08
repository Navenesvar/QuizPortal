package com.naven.quizzer.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.naven.quizzer.domain.SignUp;

@Repository
public interface SignUpRepo extends JpaRepository<SignUp, Integer> {
    List<SignUp> findAll();
    @SuppressWarnings("unchecked")
    SignUp save(SignUp signUp);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    SignUp findByUsername(String username);
}


