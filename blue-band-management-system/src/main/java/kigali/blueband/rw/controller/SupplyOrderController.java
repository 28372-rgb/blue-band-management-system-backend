package kigali.blueband.rw.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.blueband.rw.domain.SupplyOrder;
import kigali.blueband.rw.domain.SupplyStatus;
import kigali.blueband.rw.service.SupplyOrderService;

@RestController
@RequestMapping(value = "/api/supply-orders")
public class SupplyOrderController {

    private final SupplyOrderService supplyOrderService;

    public SupplyOrderController(SupplyOrderService supplyOrderService) {
        this.supplyOrderService = supplyOrderService;
    }

    // Saves a supply order after checking the date and the distributor.
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveSupplyOrder(@RequestBody SupplyOrder supplyOrder) {
        String returnedMessage = supplyOrderService.saveSupplyOrder(supplyOrder);

        if (returnedMessage.equals("Supply order date cannot be in the past")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.BAD_REQUEST);
        } else if (returnedMessage.contains("does not exist")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        } else if (returnedMessage.equals("Distributor already has a delivery on that date")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.CREATED);
    }

    // Returns all supply orders.
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllSupplyOrders() {
        List<SupplyOrder> supplyOrders = supplyOrderService.getAllSupplyOrders();
        return new ResponseEntity<>(supplyOrders, HttpStatus.OK);
    }

    // Returns the supply orders that have the given status.
    @GetMapping(value = "/by-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSupplyOrdersByStatus(@RequestParam SupplyStatus status) {
        List<SupplyOrder> supplyOrders = supplyOrderService.getSupplyOrdersByStatus(status);
        return new ResponseEntity<>(supplyOrders, HttpStatus.OK);
    }

    // Returns the supply orders between two dates.
    @GetMapping(value = "/between", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSupplyOrdersBetween(
            @RequestParam String start, @RequestParam String end) {

        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        List<SupplyOrder> supplyOrders =
                supplyOrderService.getSupplyOrdersBetween(startDate, endDate);
        return new ResponseEntity<>(supplyOrders, HttpStatus.OK);
    }

    // Shows how many supply orders each status has.
    @GetMapping(value = "/stats/by-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getStatsByStatus() {
        List<Object[]> stats = supplyOrderService.countSupplyOrdersByStatus();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    // Returns one page of supply orders.
    @GetMapping(value = "/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSupplyOrdersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "orderDate,asc") String sort) {

        String[] parts = sort.split(",");
        String property = parts[0];
        Sort.Direction direction = Sort.Direction.ASC;

        if (parts.length > 1 && parts[1].equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));
        Page<SupplyOrder> supplyOrders = supplyOrderService.getSupplyOrdersPage(pageable);

        return new ResponseEntity<>(supplyOrders, HttpStatus.OK);
    }

    // Finds a supply order by its id.
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSupplyOrderById(@PathVariable UUID id) {
        Optional<SupplyOrder> supplyOrder = supplyOrderService.getSupplyOrderById(id);

        if (supplyOrder.isEmpty()) {
            return new ResponseEntity<>("The supply order with that id does not exist",
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(supplyOrder.get(), HttpStatus.OK);
    }

    // Updates a supply order without changing its status.
    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateSupplyOrder(
            @PathVariable UUID id, @RequestBody SupplyOrder supplyOrder) {

        String returnedMessage = supplyOrderService.updateSupplyOrder(id, supplyOrder);

        if (returnedMessage.contains("does not exist")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
    }

    // Deletes a supply order that has no stock batches.
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteSupplyOrder(@PathVariable UUID id) {
        String returnedMessage = supplyOrderService.deleteSupplyOrder(id);

        if (returnedMessage.equals("The supply order with that id does not exist")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        } else if (returnedMessage.equals("That supply order has stock batches, delete them first")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
    }

    // Changes the status of a supply order.
    @PatchMapping(value = "/{id}/status")
    public ResponseEntity<?> changeSupplyOrderStatus(
            @PathVariable UUID id, @RequestParam SupplyStatus status) {

        String returnedMessage = supplyOrderService.changeStatus(id, status);

        if (returnedMessage.equals("The supply order with that id does not exist")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        } else if (returnedMessage.startsWith("Cannot change status")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
    }

    // Cancels a distributor's orders for a specific day.
    @PatchMapping(value = "/cancel-day")
    public ResponseEntity<?> cancelDay(
            @RequestParam UUID distributorId, @RequestParam String date) {

        LocalDate day = LocalDate.parse(date);

        int cancelled = supplyOrderService.cancelDistributorOrdersForDay(distributorId, day);

        return new ResponseEntity<>(cancelled + " supply orders cancelled", HttpStatus.OK);
    }
}
