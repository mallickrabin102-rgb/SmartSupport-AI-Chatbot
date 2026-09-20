package com.smartsupport.repository;
import com.smartsupport.entity.ReturnRequest; import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest,Long>{ Optional<ReturnRequest> findByOrder_OrderNumberIgnoreCase(String orderNumber); }
