package kigali.blueband.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import kigali.blueband.rw.domain.Distributor;
import kigali.blueband.rw.domain.ProductLine;
import kigali.blueband.rw.repository.ProductLineRepository;

@Service
public class ProductLineService {

    private final ProductLineRepository productLineRepo;

    public ProductLineService(ProductLineRepository productLineRepo) {
        this.productLineRepo = productLineRepo;
    }

    // Saves a new product line.
    public String saveProductLine(ProductLine productLine) {
        productLineRepo.save(productLine);
        return "Product line saved successfully";
    }

    // Returns all product lines.
    public List<ProductLine> getAllProductLines() {
        return productLineRepo.findAll();
    }

    // Finds a product line by its id.
    public Optional<ProductLine> getProductLineById(UUID id) {
        return productLineRepo.findById(id);
    }

    // Returns the product lines that no distributor holds.
    public List<ProductLine> getUnusedProductLines() {
        return productLineRepo.findUnusedProductLines();
    }

    // Updates the name of a product line.
    public String updateProductLine(UUID id, ProductLine updated) {
        ProductLine productLine = productLineRepo.findById(id).orElse(null);

        if (productLine == null) {
            return "The product line with that id does not exist";
        }

        productLine.setName(updated.getName());
        productLineRepo.save(productLine);
        return "Product line updated successfully";
    }

    // Deletes a product line after unlinking it from its distributors.
    @Transactional
    public String deleteProductLine(UUID id) {
        ProductLine productLine = productLineRepo.findById(id).orElse(null);

        if (productLine == null) {
            return "The product line with that id does not exist";
        }

        for (Distributor distributor : productLine.getDistributors()) {
            distributor.getProductLines().remove(productLine);
        }

        productLineRepo.delete(productLine);
        return "Product line deleted successfully";
    }
}
