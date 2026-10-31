package kigali.blueband.rw.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kigali.blueband.rw.domain.ProductLine;

@Repository
public interface ProductLineRepository extends JpaRepository<ProductLine, UUID> {

    // Product lines with no distributor.
    @Query("SELECT p FROM ProductLine p WHERE p.distributors IS EMPTY ORDER BY p.name ASC")
    List<ProductLine> findUnusedProductLines();
}
