package kigali.blueband.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kigali.blueband.rw.domain.Retailer;
import kigali.blueband.rw.repository.DistributorRepository;
import kigali.blueband.rw.repository.RetailerRepository;
import kigali.blueband.rw.repository.SupplyOrderRepository;

@Service
public class RetailerService {

    private final RetailerRepository retailerRepo;
    private final DistributorRepository distributorRepo;
    private final SupplyOrderRepository supplyOrderRepo;

    public RetailerService(
            RetailerRepository retailerRepo,
            DistributorRepository distributorRepo,
            SupplyOrderRepository supplyOrderRepo) {
        this.retailerRepo = retailerRepo;
        this.distributorRepo = distributorRepo;
        this.supplyOrderRepo = supplyOrderRepo;
    }

    // Saves a retailer if its shop code is new.
    public String saveRetailer(Retailer retailer) {
        if (retailerRepo.findByShopCode(retailer.getShopCode()).isPresent()) {
            return "Retailer with that shopCode already exists";
        }

        retailerRepo.save(retailer);
        return "Retailer saved successfully";
    }

    // Returns all retailers.
    public List<Retailer> getAllRetailers() {
        return retailerRepo.findAll();
    }

    // Finds a retailer by its id.
    public Optional<Retailer> getRetailerById(UUID id) {
        return retailerRepo.findById(id);
    }

    // Returns an owner's retailers, ignoring letter case.
    public List<Retailer> getRetailersByOwnerName(String ownerName) {
        return retailerRepo.findByOwnerNameIgnoreCaseOrderByShopNameAsc(ownerName);
    }

    // Returns the retailers that ordered from a distributor.
    public List<Retailer> getRetailersOfDistributor(UUID distributorId) {
        if (!distributorRepo.existsById(distributorId)) {
            return null;
        }

        return retailerRepo.findRetailersByDistributorId(distributorId);
    }

    // Updates the shop name, owner name and phone of a retailer.
    public String updateRetailer(UUID id, Retailer updated) {
        Retailer retailer = retailerRepo.findById(id).orElse(null);

        if (retailer == null) {
            return "The retailer with that id does not exist";
        }

        retailer.setShopName(updated.getShopName());
        retailer.setOwnerName(updated.getOwnerName());
        retailer.setPhone(updated.getPhone());
        retailerRepo.save(retailer);
        return "Retailer updated successfully";
    }

    // Deletes a retailer that has no supply orders.
    public String deleteRetailer(UUID id) {
        Retailer retailer = retailerRepo.findById(id).orElse(null);

        if (retailer == null) {
            return "The retailer with that id does not exist";
        }

        if (supplyOrderRepo.existsByRetailer(retailer)) {
            return "That retailer has supply orders, delete them first";
        }

        retailerRepo.deleteById(id);
        return "Retailer deleted successfully";
    }
}
