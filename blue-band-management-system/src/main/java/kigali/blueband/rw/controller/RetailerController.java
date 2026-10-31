package kigali.blueband.rw.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.blueband.rw.domain.Retailer;
import kigali.blueband.rw.service.RetailerService;

@RestController
@RequestMapping(value = "/api/retailers")
public class RetailerController {

    @Autowired
    private RetailerService retailerService;

    // Saves a retailer if its shop code is new.
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveRetailer(@RequestBody Retailer retailer) {
        String returnedMessage = retailerService.saveRetailer(retailer);

        if (returnedMessage.contains("already exists")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.CREATED);
    }

    // Returns all retailers.
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllRetailers() {
        List<Retailer> retailers = retailerService.getAllRetailers();
        return new ResponseEntity<>(retailers, HttpStatus.OK);
    }

    // Finds the retailers of an owner, ignoring letter case.
    @GetMapping(value = "/by-owner-name", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRetailersByOwnerName(@RequestParam String ownerName) {
        List<Retailer> retailers = retailerService.getRetailersByOwnerName(ownerName);
        return new ResponseEntity<>(retailers, HttpStatus.OK);
    }

    // Returns the retailers that ordered from a distributor, 404 if it does not exist.
    @GetMapping(value = "/of-distributor/{distributorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRetailersOfDistributor(@PathVariable UUID distributorId) {
        List<Retailer> retailers = retailerService.getRetailersOfDistributor(distributorId);

        if (retailers == null) {
            return new ResponseEntity<>("The distributor with that id does not exist",
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(retailers, HttpStatus.OK);
    }

    // Finds a retailer by its id.
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRetailerById(@PathVariable UUID id) {
        Optional<Retailer> retailer = retailerService.getRetailerById(id);

        if (retailer.isEmpty()) {
            return new ResponseEntity<>("The retailer with that id does not exist",
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(retailer.get(), HttpStatus.OK);
    }

    // Updates the shop name, owner name and phone of a retailer.
    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateRetailer(
            @PathVariable UUID id, @RequestBody Retailer retailer) {

        String returnedMessage = retailerService.updateRetailer(id, retailer);

        if (returnedMessage.contains("does not exist")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
    }

    // Deletes a retailer that has no supply orders.
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteRetailer(@PathVariable UUID id) {
        String returnedMessage = retailerService.deleteRetailer(id);

        if (returnedMessage.contains("does not exist")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        } else if (returnedMessage.contains("delete them first")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
    }
}
