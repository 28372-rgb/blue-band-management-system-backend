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

import kigali.blueband.rw.domain.ProductLine;
import kigali.blueband.rw.service.ProductLineService;

@RestController
@RequestMapping(value = "/api/product-lines")
public class ProductLineController {

    private final ProductLineService productLineService;

    public ProductLineController(ProductLineService productLineService) {
        this.productLineService = productLineService;
    }

    // Saves a new product line.
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveProductLine(@RequestBody ProductLine productLine) {
        String returnedMessage = productLineService.saveProductLine(productLine);
        return new ResponseEntity<>(returnedMessage, HttpStatus.CREATED);
    }

    // Returns all product lines.
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllProductLines() {
        List<ProductLine> productLines = productLineService.getAllProductLines();
        return new ResponseEntity<>(productLines, HttpStatus.OK);
    }

    // Returns the product lines that no distributor holds.
    @GetMapping(value = "/unused", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUnusedProductLines() {
        List<ProductLine> productLines = productLineService.getUnusedProductLines();
        return new ResponseEntity<>(productLines, HttpStatus.OK);
    }

    // Finds a product line by its id.
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductLineById(@PathVariable UUID id) {
        Optional<ProductLine> productLine = productLineService.getProductLineById(id);

        if (productLine.isEmpty()) {
            return new ResponseEntity<>("The product line with that id does not exist",
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(productLine.get(), HttpStatus.OK);
    }

    // Updates the name of a product line.
    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProductLine(
            @PathVariable UUID id, @RequestBody ProductLine productLine) {

        String returnedMessage = productLineService.updateProductLine(id, productLine);

        if (returnedMessage.contains("does not exist")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
    }

    // Deletes a product line and removes it from the distributors first.
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteProductLine(@PathVariable UUID id) {
        String returnedMessage = productLineService.deleteProductLine(id);

        if (returnedMessage.contains("does not exist")) {
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
    }
}
