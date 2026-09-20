package com.smartsupport.repository;
import com.smartsupport.entity.Payment; import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface PaymentRepository extends JpaRepository<Payment,Long>{ Optional<Payment> findByOrder_OrderNumberIgnoreCase(String orderNumber); }
