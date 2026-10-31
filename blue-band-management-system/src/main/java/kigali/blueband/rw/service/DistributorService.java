package kigali.blueband.rw.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import kigali.blueband.rw.domain.Distributor;
import kigali.blueband.rw.domain.ProductLine;
import kigali.blueband.rw.domain.Warehouse;
import kigali.blueband.rw.repository.DistributorRepository;
import kigali.blueband.rw.repository.ProductLineRepository;
import kigali.blueband.rw.repository.SupplyOrderRepository;
import kigali.blueband.rw.repository.WarehouseRepository;

@Service
public class DistributorService {

    @Autowired
    private DistributorRepository distributorRepo;

    @Autowired
    private WarehouseRepository warehouseRepo;

    @Autowired
    private ProductLineRepository productLineRepo;

    @Autowired
    private SupplyOrderRepository supplyOrderRepo;

    // Saves a distributor and links its warehouse and product lines.
    public String saveDistributor(Distributor distributor) {

        if (distributor.getWarehouseNumber() != null) {
            Warehouse warehouse = warehouseRepo
                    .findByWarehouseNumber(distributor.getWarehouseNumber()).orElse(null);

            if (warehouse == null) {
                return "The warehouse with that warehouseNumber does not exist";
            }

            if (distributorRepo.existsByWarehouse(warehouse)) {
                return "That warehouse is taken by another distributor";
            }

            distributor.setWarehouse(warehouse);
        }

        List<ProductLine> lines = new ArrayList<>();

        if (distributor.getProductLineIds() != null) {
            for (UUID productLineId : distributor.getProductLineIds()) {
                ProductLine productLine = productLineRepo.findById(productLineId).orElse(null);

                if (productLine == null) {
                    return "That product line does not exist";
                }

                lines.add(productLine);
            }
        }

        distributor.setProductLines(lines);
        distributorRepo.save(distributor);
        return "Distributor saved successfully";
    }

    // Returns all distributors.
    public List<Distributor> getAllDistributors() {
        return distributorRepo.findAll();
    }

    // Finds a distributor by its id.
    public Optional<Distributor> getDistributorById(UUID id) {
        return distributorRepo.findById(id);
    }

    // Finds the distributors that hold a product line.
    public List<Distributor> getDistributorsByProductLine(String name) {
        return distributorRepo.findByProductLineName(name);
    }

    // Updates the company name, contact name and phone of a distributor.
    public String updateDistributor(UUID id, Distributor updated) {
        Distributor distributor = distributorRepo.findById(id).orElse(null);

        if (distributor == null) {
            return "The distributor with that id does not exist";
        }

        distributor.setCompanyName(updated.getCompanyName());
        distributor.setContactName(updated.getContactName());
        distributor.setPhone(updated.getPhone());
        distributorRepo.save(distributor);
        return "Distributor updated successfully";
    }

    // Deletes a distributor that has no supply orders.
    public String deleteDistributor(UUID id) {
        Distributor distributor = distributorRepo.findById(id).orElse(null);

        if (distributor == null) {
            return "The distributor with that id does not exist";
        }

        if (supplyOrderRepo.existsByDistributor(distributor)) {
            return "That distributor has supply orders, delete them first";
        }

        distributorRepo.deleteById(id);
        return "Distributor deleted successfully";
    }

    // Adds one product line to a distributor and keeps the others.
    @Transactional
    public String addProductLine(UUID distributorId, UUID productLineId) {
        Distributor distributor = distributorRepo.findById(distributorId).orElse(null);

        if (distributor == null) {
            return "The distributor with that id does not exist";
        }

        ProductLine productLine = productLineRepo.findById(productLineId).orElse(null);

        if (productLine == null) {
            return "That product line does not exist";
        }

        boolean alreadyThere = distributor.getProductLines().stream()
                .anyMatch(line -> line.getId().equals(productLineId));

        if (alreadyThere) {
            return "The distributor already has that product line";
        }

        distributor.getProductLines().add(productLine);
        distributorRepo.save(distributor);

        return "Product line added to the distributor";
    }

    // Moves a distributor to another warehouse if it is free.
    public String changeWarehouse(UUID distributorId, int warehouseNumber) {
        Distributor distributor = distributorRepo.findById(distributorId).orElse(null);

        if (distributor == null) {
            return "The distributor with that id does not exist";
        }

        Warehouse warehouse = warehouseRepo.findByWarehouseNumber(warehouseNumber).orElse(null);

        if (warehouse == null) {
            return "The warehouse with that warehouseNumber does not exist";
        }

        if (distributorRepo.existsByWarehouseAndIdNot(warehouse, distributorId)) {
            return "That warehouse is taken by another distributor";
        }

        distributor.setWarehouse(warehouse);
        distributorRepo.save(distributor);

        return "Warehouse changed successfully";
    }
}
