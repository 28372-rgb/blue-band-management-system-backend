package kigali.blueband.rw.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.blueband.rw.domain.Distributor;
import kigali.blueband.rw.domain.Retailer;
import kigali.blueband.rw.domain.SupplyOrder;
import kigali.blueband.rw.domain.SupplyStatus;

@Repository
public interface SupplyOrderRepository extends JpaRepository<SupplyOrder, UUID> {

    // Supply orders of a status, earliest date first.
    List<SupplyOrder> findByStatusOrderByOrderDateAsc(SupplyStatus status);

    // Supply orders between two dates.
    List<SupplyOrder> findByOrderDateBetweenOrderByOrderDateAsc(LocalDate start, LocalDate end);

    // Checks if the distributor has an order on that date that is not cancelled.
    boolean existsByDistributorAndOrderDateAndStatusNot(Distributor distributor, LocalDate orderDate, SupplyStatus status);

    // Checks if the distributor has supply orders.
    boolean existsByDistributor(Distributor distributor);

    // Checks if the retailer has supply orders.
    boolean existsByRetailer(Retailer retailer);

    // Number of supply orders of a date.
    long countByOrderDate(LocalDate orderDate);

    // Number of supply orders of a status.
    long countByStatus(SupplyStatus status);

    // Number of supply orders of each status.
    @Query("SELECT o.status, COUNT(o) FROM SupplyOrder o GROUP BY o.status")
    List<Object[]> countSupplyOrdersByStatus();

    // Cancels the orders of a distributor for a day, except the delivered ones.
    @Modifying
    @Query("UPDATE SupplyOrder o SET o.status = kigali.blueband.rw.domain.SupplyStatus.CANCELLED WHERE o.distributor.id = :distributorId AND o.orderDate = :date AND o.status <> kigali.blueband.rw.domain.SupplyStatus.DELIVERED")
    int cancelDistributorOrdersForDay(@Param("distributorId") UUID distributorId, @Param("date") LocalDate date);
}
