package kigali.blueband.rw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import kigali.blueband.rw.domain.Distributor;
import kigali.blueband.rw.domain.Retailer;
import kigali.blueband.rw.domain.SupplyOrder;
import kigali.blueband.rw.domain.SupplyStatus;
import kigali.blueband.rw.repository.DistributorRepository;
import kigali.blueband.rw.repository.RetailerRepository;
import kigali.blueband.rw.repository.StockBatchRepository;
import kigali.blueband.rw.repository.SupplyOrderRepository;

@Service
public class SupplyOrderService {

    @Autowired
    private SupplyOrderRepository supplyOrderRepo;

    @Autowired
    private RetailerRepository retailerRepo;

    @Autowired
    private DistributorRepository distributorRepo;

    @Autowired
    private StockBatchRepository stockBatchRepo;

    // Saves a supply order after checking the date and the distributor.
    public String saveSupplyOrder(SupplyOrder supplyOrder) {

        LocalDate date = supplyOrder.getOrderDate();

        if (date != null && date.isBefore(LocalDate.now())) {
            return "Supply order date cannot be in the past";
        }

        Retailer retailer = null;

        if (supplyOrder.getRetailerId() != null) {
            retailer = retailerRepo.findById(supplyOrder.getRetailerId()).orElse(null);

            if (retailer == null) {
                return "The retailer with that id does not exist";
            }
        }

        Distributor distributor = null;

        if (supplyOrder.getDistributorId() != null) {
            distributor = distributorRepo.findById(supplyOrder.getDistributorId()).orElse(null);

            if (distributor == null) {
                return "The distributor with that id does not exist";
            }

            if (date != null && supplyOrderRepo.existsByDistributorAndOrderDateAndStatusNot(
                    distributor, date, SupplyStatus.CANCELLED)) {
                return "Distributor already has a delivery on that date";
            }
        }

        supplyOrder.setRetailer(retailer);
        supplyOrder.setDistributor(distributor);

        supplyOrder.setStatus(SupplyStatus.PLACED);

        supplyOrderRepo.save(supplyOrder);
        return "Supply order saved successfully";
    }

    // Returns all supply orders.
    public List<SupplyOrder> getAllSupplyOrders() {
        return supplyOrderRepo.findAll();
    }

    // Finds a supply order by its id.
    public Optional<SupplyOrder> getSupplyOrderById(UUID id) {
        return supplyOrderRepo.findById(id);
    }

    // Returns the supply orders that have the given status.
    public List<SupplyOrder> getSupplyOrdersByStatus(SupplyStatus status) {
        return supplyOrderRepo.findByStatusOrderByOrderDateAsc(status);
    }

    // Returns the supply orders between two dates.
    public List<SupplyOrder> getSupplyOrdersBetween(LocalDate start, LocalDate end) {
        return supplyOrderRepo.findByOrderDateBetweenOrderByOrderDateAsc(start, end);
    }

    // Updates a supply order without changing its status.
    public String updateSupplyOrder(UUID id, SupplyOrder updated) {
        SupplyOrder supplyOrder = supplyOrderRepo.findById(id).orElse(null);

        if (supplyOrder == null) {
            return "The supply order with that id does not exist";
        }

        if (updated.getRetailerId() != null) {
            Retailer retailer = retailerRepo.findById(updated.getRetailerId()).orElse(null);

            if (retailer == null) {
                return "The retailer with that id does not exist";
            }

            supplyOrder.setRetailer(retailer);
        }

        if (updated.getDistributorId() != null) {
            Distributor distributor = distributorRepo.findById(updated.getDistributorId()).orElse(null);

            if (distributor == null) {
                return "The distributor with that id does not exist";
            }

            supplyOrder.setDistributor(distributor);
        }

        supplyOrder.setOrderDate(updated.getOrderDate());
        supplyOrder.setCartons(updated.getCartons());

        supplyOrderRepo.save(supplyOrder);
        return "Supply order updated successfully";
    }

    // Allows only the valid status changes.
    public String changeStatus(UUID id, SupplyStatus newStatus) {
        SupplyOrder supplyOrder = supplyOrderRepo.findById(id).orElse(null);

        if (supplyOrder == null) {
            return "The supply order with that id does not exist";
        }

        SupplyStatus current = supplyOrder.getStatus();
        boolean allowed = false;

        if (current == SupplyStatus.PLACED) {
            allowed = newStatus == SupplyStatus.DISPATCHED
                    || newStatus == SupplyStatus.CANCELLED;

        } else if (current == SupplyStatus.DISPATCHED) {
            allowed = newStatus == SupplyStatus.DELIVERED
                    || newStatus == SupplyStatus.CANCELLED;
        }

        if (!allowed) {
            return "Cannot change status from " + current + " to " + newStatus;
        }

        supplyOrder.setStatus(newStatus);
        supplyOrderRepo.save(supplyOrder);

        return "Supply order status updated successfully";
    }

    // Deletes a supply order that has no stock batches.
    public String deleteSupplyOrder(UUID id) {
        SupplyOrder supplyOrder = supplyOrderRepo.findById(id).orElse(null);

        if (supplyOrder == null) {
            return "The supply order with that id does not exist";
        }

        if (stockBatchRepo.existsBySupplyOrder(supplyOrder)) {
            return "That supply order has stock batches, delete them first";
        }

        supplyOrderRepo.deleteById(id);
        return "Supply order deleted successfully";
    }

    // Number of supply orders of each status.
    public List<Object[]> countSupplyOrdersByStatus() {
        return supplyOrderRepo.countSupplyOrdersByStatus();
    }

    // Cancels the orders of a distributor for a day, except the delivered ones.
    @Transactional
    public int cancelDistributorOrdersForDay(UUID distributorId, LocalDate date) {
        return supplyOrderRepo.cancelDistributorOrdersForDay(distributorId, date);
    }

    // Returns one page of supply orders.
    public Page<SupplyOrder> getSupplyOrdersPage(Pageable pageable) {
        return supplyOrderRepo.findAll(pageable);
    }
}
