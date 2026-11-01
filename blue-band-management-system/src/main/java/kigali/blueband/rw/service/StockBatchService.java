package kigali.blueband.rw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kigali.blueband.rw.domain.StockBatch;
import kigali.blueband.rw.domain.SupplyOrder;
import kigali.blueband.rw.domain.SupplyStatus;
import kigali.blueband.rw.repository.StockBatchRepository;
import kigali.blueband.rw.repository.SupplyOrderRepository;

@Service
public class StockBatchService {

    private final StockBatchRepository stockBatchRepo;
    private final SupplyOrderRepository supplyOrderRepo;

    public StockBatchService(
            StockBatchRepository stockBatchRepo,
            SupplyOrderRepository supplyOrderRepo) {
        this.stockBatchRepo = stockBatchRepo;
        this.supplyOrderRepo = supplyOrderRepo;
    }

    // Saves a stock batch for a delivered supply order.
    public String saveStockBatch(UUID supplyOrderId, StockBatch stockBatch) {
        SupplyOrder supplyOrder = supplyOrderRepo.findById(supplyOrderId).orElse(null);

        if (supplyOrder == null) {
            return "The supply order with that id does not exist";
        }

        if (supplyOrder.getStatus() != SupplyStatus.DELIVERED) {
            return "Stock batches are only for DELIVERED supply orders";
        }

        stockBatch.setReceivedDate(LocalDate.now());
        stockBatch.setSupplyOrder(supplyOrder);
        stockBatchRepo.save(stockBatch);

        return "Stock batch saved successfully";
    }

    // Returns all stock batches.
    public List<StockBatch> getAllStockBatches() {
        return stockBatchRepo.findAll();
    }

    // Returns a retailer's stock batches, newest first.
    public List<StockBatch> getStockBatchesByRetailer(String shopCode) {
        return stockBatchRepo.findHistoryByShopCode(shopCode);
    }

    // Deletes a stock batch.
    public String deleteStockBatch(UUID id) {
        if (!stockBatchRepo.existsById(id)) {
            return "The stock batch with that id does not exist";
        }

        stockBatchRepo.deleteById(id);
        return "Stock batch deleted successfully";
    }
}
