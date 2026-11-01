package kigali.blueband.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kigali.blueband.rw.domain.Warehouse;
import kigali.blueband.rw.repository.DistributorRepository;
import kigali.blueband.rw.repository.WarehouseRepository;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepo;
    private final DistributorRepository distributorRepo;

    public WarehouseService(
            WarehouseRepository warehouseRepo,
            DistributorRepository distributorRepo) {
        this.warehouseRepo = warehouseRepo;
        this.distributorRepo = distributorRepo;
    }

    // Saves a warehouse if its number is not used yet.
    public String saveWarehouse(Warehouse warehouse) {
        if (warehouseRepo.findByWarehouseNumber(warehouse.getWarehouseNumber()).isPresent()) {
            return "Warehouse with that warehouseNumber already exists";
        }

        warehouseRepo.save(warehouse);
        return "Warehouse saved successfully";
    }

    // Returns all warehouses.
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepo.findAll();
    }

    // Finds a warehouse by its id.
    public Optional<Warehouse> getWarehouseById(UUID id) {
        return warehouseRepo.findById(id);
    }

    // Returns warehouses that are not assigned to a distributor.
    public List<Warehouse> getFreeWarehouses() {
        return warehouseRepo.findFreeWarehouses();
    }

    // Updates the number and the name of a warehouse.
    public String updateWarehouse(UUID id, Warehouse updated) {
        Warehouse warehouse = warehouseRepo.findById(id).orElse(null);

        if (warehouse == null) {
            return "The warehouse with that id does not exist";
        }

        Warehouse sameNumber =
                warehouseRepo.findByWarehouseNumber(updated.getWarehouseNumber()).orElse(null);

        if (sameNumber != null && !sameNumber.getId().equals(id)) {
            return "Warehouse with that warehouseNumber already exists";
        }

        warehouse.setWarehouseNumber(updated.getWarehouseNumber());
        warehouse.setName(updated.getName());
        warehouseRepo.save(warehouse);
        return "Warehouse updated successfully";
    }

    // Deletes a warehouse that is not occupied.
    public String deleteWarehouse(UUID id) {
        Warehouse warehouse = warehouseRepo.findById(id).orElse(null);

        if (warehouse == null) {
            return "The warehouse with that id does not exist";
        }

        if (distributorRepo.existsByWarehouse(warehouse)) {
            return "That warehouse is occupied by a distributor, move the distributor first";
        }

        warehouseRepo.deleteById(id);
        return "Warehouse deleted successfully";
    }
}
