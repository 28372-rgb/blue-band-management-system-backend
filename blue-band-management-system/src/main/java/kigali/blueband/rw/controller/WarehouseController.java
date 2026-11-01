package kigali.blueband.rw.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kigali.blueband.rw.domain.Warehouse;
import kigali.blueband.rw.service.WarehouseService;

@RestController
@RequestMapping(value = "/api/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    // Saves a warehouse if its number is not used yet.
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveWarehouse(@RequestBody Warehouse warehouse) {
        String returnedMessage = warehouseService.saveWarehouse(warehouse);

        if (returnedMessage.contains("already exists")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.CREATED);
    }

    // Returns all warehouses.
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllWarehouses() {
        List<Warehouse> warehouses = warehouseService.getAllWarehouses();
        return new ResponseEntity<>(warehouses, HttpStatus.OK);
    }

    // Returns the warehouses that no distributor occupies.
    @GetMapping(value = "/free", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getFreeWarehouses() {
        List<Warehouse> warehouses = warehouseService.getFreeWarehouses();
        return new ResponseEntity<>(warehouses, HttpStatus.OK);
    }

    // Finds a warehouse by its id.
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getWarehouseById(@PathVariable UUID id) {
        Optional<Warehouse> warehouse = warehouseService.getWarehouseById(id);

        if (warehouse.isEmpty()) {
            return new ResponseEntity<>("The warehouse with that id does not exist",
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(warehouse.get(), HttpStatus.OK);
    }

    // Updates the number and the name of a warehouse.
    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateWarehouse(
            @PathVariable UUID id, @RequestBody Warehouse warehouse) {

        String returnedMessage = warehouseService.updateWarehouse(id, warehouse);

        if (returnedMessage.contains("does not exist")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        } else if (returnedMessage.contains("already exists")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
    }

    // Deletes a warehouse that is not occupied.
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteWarehouse(@PathVariable UUID id) {
        String returnedMessage = warehouseService.deleteWarehouse(id);

        if (returnedMessage.contains("does not exist")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        } else if (returnedMessage.contains("is occupied")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
    }
}
