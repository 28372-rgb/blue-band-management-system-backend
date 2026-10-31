package kigali.blueband.rw.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.blueband.rw.domain.StockBatch;
import kigali.blueband.rw.domain.SupplyOrder;

@Repository
public interface StockBatchRepository extends JpaRepository<StockBatch, UUID> {

    // Stock batches of a retailer, newest first.
    @Query("SELECT b FROM StockBatch b WHERE b.supplyOrder.retailer.shopCode = :shopCode ORDER BY b.receivedDate DESC")
    List<StockBatch> findHistoryByShopCode(@Param("shopCode") String shopCode);

    // Checks if the supply order has stock batches.
    boolean existsBySupplyOrder(SupplyOrder supplyOrder);
}
