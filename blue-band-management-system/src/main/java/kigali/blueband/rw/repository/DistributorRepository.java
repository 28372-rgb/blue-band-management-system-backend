package kigali.blueband.rw.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.blueband.rw.domain.Distributor;
import kigali.blueband.rw.domain.Warehouse;

@Repository
public interface DistributorRepository extends JpaRepository<Distributor, UUID> {

    // Distributors that hold a product line, name matched without letter case.
    @Query("SELECT DISTINCT d FROM Distributor d JOIN d.productLines p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY d.companyName ASC")
    List<Distributor> findByProductLineName(@Param("name") String name);

    // Checks if another distributor already uses the warehouse.
    boolean existsByWarehouseAndIdNot(Warehouse warehouse, UUID id);

    // Checks if any distributor uses the warehouse.
    boolean existsByWarehouse(Warehouse warehouse);
}
