package kigali.blueband.rw.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.blueband.rw.domain.StockBatch;
import kigali.blueband.rw.service.StockBatchService;

@RestController
@RequestMapping(value = "/api/stock-batches")
public class StockBatchController {

    @Autowired
    private StockBatchService stockBatchService;

    // Saves a stock batch for a delivered supply order.
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveStockBatch(
            @RequestParam UUID supplyOrderId, @RequestBody StockBatch stockBatch) {

        String returnedMessage = stockBatchService.saveStockBatch(supplyOrderId, stockBatch);

        if (returnedMessage.equals("The supply order with that id does not exist")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        } else if (returnedMessage.equals("Stock batches are only for DELIVERED supply orders")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.CREATED);
    }

    // Returns all stock batches.
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllStockBatches() {
        List<StockBatch> stockBatches = stockBatchService.getAllStockBatches();
        return new ResponseEntity<>(stockBatches, HttpStatus.OK);
    }

    // Returns the stock batches of a retailer, newest first.
    @GetMapping(value = "/retailer/{shopCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getStockBatchesByRetailer(@PathVariable String shopCode) {
        List<StockBatch> stockBatches = stockBatchService.getStockBatchesByRetailer(shopCode);
        return new ResponseEntity<>(stockBatches, HttpStatus.OK);
    }

    // Deletes a stock batch.
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteStockBatch(@PathVariable UUID id) {
        String returnedMessage = stockBatchService.deleteStockBatch(id);

        if (returnedMessage.equals("The stock batch with that id does not exist")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
    }
}
