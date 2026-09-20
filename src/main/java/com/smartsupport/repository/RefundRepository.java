package com.smartsupport.repository;
import com.smartsupport.entity.Refund; import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface RefundRepository extends JpaRepository<Refund,Long>{ Optional<Refund> findByOrder_OrderNumberIgnoreCase(String orderNumber); }
