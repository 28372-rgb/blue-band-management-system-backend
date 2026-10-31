package kigali.blueband.rw.service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.blueband.rw.domain.SupplyStatus;
import kigali.blueband.rw.repository.DistributorRepository;
import kigali.blueband.rw.repository.RetailerRepository;
import kigali.blueband.rw.repository.StockBatchRepository;
import kigali.blueband.rw.repository.SupplyOrderRepository;

@Service
public class DashboardService {

    @Autowired
    private RetailerRepository retailerRepo;

    @Autowired
    private DistributorRepository distributorRepo;

    @Autowired
    private SupplyOrderRepository supplyOrderRepo;

    @Autowired
    private StockBatchRepository stockBatchRepo;

    @Autowired
    private WarehouseService warehouseService;

    // Collects the main numbers of the system.
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
