package kigali.blueband.rw.service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import kigali.blueband.rw.domain.SupplyStatus;
import kigali.blueband.rw.repository.DistributorRepository;
import kigali.blueband.rw.repository.RetailerRepository;
import kigali.blueband.rw.repository.StockBatchRepository;
import kigali.blueband.rw.repository.SupplyOrderRepository;

@Service
public class DashboardService {

    private final RetailerRepository retailerRepo;
    private final DistributorRepository distributorRepo;
    private final SupplyOrderRepository supplyOrderRepo;
    private final StockBatchRepository stockBatchRepo;
    private final WarehouseService warehouseService;

    public DashboardService(
            RetailerRepository retailerRepo,
            DistributorRepository distributorRepo,
            SupplyOrderRepository supplyOrderRepo,
            StockBatchRepository stockBatchRepo,
            WarehouseService warehouseService) {
        this.retailerRepo = retailerRepo;
        this.distributorRepo = distributorRepo;
        this.supplyOrderRepo = supplyOrderRepo;
        this.stockBatchRepo = stockBatchRepo;
        this.warehouseService = warehouseService;
    }

    // Builds the system's summary metrics for the dashboard.
    public Map<String, Long> getSummary() {
        Map<String, Long> summary = new LinkedHashMap<>();

        summary.put("retailers", retailerRepo.count());
        summary.put("distributors", distributorRepo.count());

        summary.put("freeWarehouses", (long) warehouseService.getFreeWarehouses().size());

        summary.put("ordersToday", supplyOrderRepo.countByOrderDate(LocalDate.now()));
        summary.put("placed", supplyOrderRepo.countByStatus(SupplyStatus.PLACED));
        summary.put("stockBatches", stockBatchRepo.count());

        return summary;
    }
}
