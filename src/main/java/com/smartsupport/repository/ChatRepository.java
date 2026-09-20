package com.smartsupport.repository;

import com.smartsupport.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findTop20ByOrderByCreatedAtDesc();
}
