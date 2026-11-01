package kigali.blueband.rw.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

import kigali.blueband.rw.domain.Distributor;
import kigali.blueband.rw.service.DistributorService;

@RestController
@RequestMapping(value = "/api/distributors")
public class DistributorController {

    private final DistributorService distributorService;

    public DistributorController(DistributorService distributorService) {
        this.distributorService = distributorService;
    }

    // Saves a distributor and links its warehouse and product lines.
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveDistributor(@RequestBody Distributor distributor) {
        String returnedMessage = distributorService.saveDistributor(distributor);

        if (returnedMessage.contains("does not exist")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        } else if (returnedMessage.equals("That warehouse is taken by another distributor")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.CREATED);
    }

    // Returns all distributors.
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllDistributors() {
        List<Distributor> distributors = distributorService.getAllDistributors();
        return new ResponseEntity<>(distributors, HttpStatus.OK);
    }

    // Finds the distributors that hold a product line.
    @GetMapping(value = "/by-product-line", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDistributorsByProductLine(@RequestParam String name) {
        List<Distributor> distributors = distributorService.getDistributorsByProductLine(name);
        return new ResponseEntity<>(distributors, HttpStatus.OK);
    }

    // Finds a distributor by its id.
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDistributorById(@PathVariable UUID id) {
        Optional<Distributor> distributor = distributorService.getDistributorById(id);

        if (distributor.isEmpty()) {
            return new ResponseEntity<>("The distributor with that id does not exist",
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(distributor.get(), HttpStatus.OK);
    }

    // Updates the company name, contact name and phone of a distributor.
    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateDistributor(
            @PathVariable UUID id, @RequestBody Distributor distributor) {

        String returnedMessage = distributorService.updateDistributor(id, distributor);

        if (returnedMessage.contains("does not exist")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
    }

    // Deletes a distributor that has no supply orders.
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteDistributor(@PathVariable UUID id) {
        String returnedMessage = distributorService.deleteDistributor(id);

        if (returnedMessage.contains("does not exist")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        } else if (returnedMessage.contains("delete them first")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
    }

    // Adds one product line to a distributor and keeps the others.
    @PutMapping(value = "/{distributorId}/product-lines/{productLineId}")
    public ResponseEntity<?> addProductLine(
            @PathVariable UUID distributorId,
            @PathVariable UUID productLineId) {

        String returnedMessage = distributorService.addProductLine(distributorId, productLineId);

        if (returnedMessage.equals("The distributor with that id does not exist")
                || returnedMessage.equals("That product line does not exist")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        } else if (returnedMessage.equals("The distributor already has that product line")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
    }

    // Moves a distributor to another warehouse if it is free.
    @PatchMapping(value = "/{distributorId}/warehouse")
    public ResponseEntity<?> changeWarehouse(
            @PathVariable UUID distributorId,
            @RequestParam int warehouseNumber) {

        String returnedMessage = distributorService.changeWarehouse(distributorId, warehouseNumber);

        if (returnedMessage.equals("The distributor with that id does not exist")
                || returnedMessage.equals("The warehouse with that warehouseNumber does not exist")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        } else if (returnedMessage.equals("That warehouse is taken by another distributor")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
    }
}
