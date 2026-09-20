package com.smartsupport.repository;
import com.smartsupport.entity.Product; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface ProductRepository extends JpaRepository<Product,Long>{ List<Product> findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(String name,String brand); }
