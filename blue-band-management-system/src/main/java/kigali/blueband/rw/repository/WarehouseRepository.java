package kigali.blueband.rw.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kigali.blueband.rw.domain.Warehouse;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    // Finds a warehouse by its number.
    Optional<Warehouse> findByWarehouseNumber(int warehouseNumber);

    // Warehouses that no distributor uses.
    @Query("SELECT w FROM Warehouse w WHERE NOT EXISTS (SELECT d FROM Distributor d WHERE d.warehouse = w) ORDER BY w.warehouseNumber ASC")
    List<Warehouse> findFreeWarehouses();
}
