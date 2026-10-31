package kigali.blueband.rw.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.blueband.rw.domain.Retailer;

@Repository
public interface RetailerRepository extends JpaRepository<Retailer, UUID> {

    // Retailers of an owner, sorted by shop name.
    List<Retailer> findByOwnerNameIgnoreCaseOrderByShopNameAsc(String ownerName);

    // Finds a retailer by its shop code.
    Optional<Retailer> findByShopCode(String shopCode);

    // Retailers that have at least one order with the distributor.
    @Query("SELECT DISTINCT o.retailer FROM SupplyOrder o WHERE o.distributor.id = :distributorId")
    List<Retailer> findRetailersByDistributorId(@Param("distributorId") UUID distributorId);
}
