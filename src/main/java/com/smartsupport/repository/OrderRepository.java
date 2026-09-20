package com.smartsupport.repository;
import com.smartsupport.entity.Order; import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface OrderRepository extends JpaRepository<Order,Long>{ Optional<Order> findByOrderNumberIgnoreCase(String orderNumber); }
