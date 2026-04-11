package com.maqc.backend.controller;

import com.maqc.backend.model.*;
import com.maqc.backend.repository.*;
import com.maqc.backend.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PropertyService propertyService;
    private final NotaryRepository notaryRepository;
    private final InspectorRepository inspectorRepository;
    private final AgentRepository agentRepository;
    private final UserRepository userRepository;

    // Property Management
    @GetMapping("/properties/search")
    public ResponseEntity<Page<Property>> searchAllProperties(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "area", required = false) String area,
            @RequestParam(value = "type", required = false) Property.PropertyType type,
            @RequestParam(value = "businessType", required = false) Property.BusinessType businessType,
            @RequestParam(value = "listingType", required = false) Property.ListingType listingType,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "minBedrooms", required = false) Integer minBedrooms,
            @RequestParam(value = "minBathrooms", required = false) Integer minBathrooms,
            @RequestParam(value = "status", required = false) Property.PropertyStatus status,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(propertyService.searchProperties(
                keyword, area, type, businessType, listingType, minPrice, maxPrice, minBedrooms, minBathrooms, status,
                pageable));
    }

    @PostMapping("/properties/{id}/approve")
    public ResponseEntity<Property> approveProperty(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.approveProperty(id));
    }

    @PostMapping("/properties/{id}/refuse")
    public ResponseEntity<Property> refuseProperty(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.refuseProperty(id));
    }

    // Notary CRUD
    @GetMapping("/notaries")
    public List<Notary> getAllNotaries() {
        return notaryRepository.findAll();
    }

    @PostMapping("/notaries")
    public Notary createNotary(@RequestBody Notary notary) {
        return notaryRepository.save(notary);
    }

    @PutMapping("/notaries/{id}")
    public Notary updateNotary(@PathVariable Long id, @RequestBody Notary details) {
        Notary notary = notaryRepository.findById(id).orElseThrow();
        notary.setName(details.getName());
        notary.setFirm(details.getFirm());
        notary.setAddress(details.getAddress());
        notary.setPhone(details.getPhone());
        notary.setEmail(details.getEmail());
        notary.setLanguages(details.getLanguages());
        notary.setRating(details.getRating());
        return notaryRepository.save(notary);
    }

    @DeleteMapping("/notaries/{id}")
    public void deleteNotary(@PathVariable Long id) {
        notaryRepository.deleteById(id);
    }

    // Inspector CRUD
    @GetMapping("/inspectors")
    public List<Inspector> getAllInspectors() {
        return inspectorRepository.findAll();
    }

    @PostMapping("/inspectors")
    public Inspector createInspector(@RequestBody Inspector inspector) {
        return inspectorRepository.save(inspector);
    }

    @PutMapping("/inspectors/{id}")
    public Inspector updateInspector(@PathVariable Long id, @RequestBody Inspector details) {
        Inspector inspector = inspectorRepository.findById(id).orElseThrow();
        inspector.setName(details.getName());
        inspector.setFirm(details.getFirm());
        inspector.setAddress(details.getAddress());
        inspector.setPhone(details.getPhone());
        inspector.setEmail(details.getEmail());
        inspector.setLanguages(details.getLanguages());
        inspector.setRating(details.getRating());
        return inspectorRepository.save(inspector);
    }

    @DeleteMapping("/inspectors/{id}")
    public void deleteInspector(@PathVariable Long id) {
        inspectorRepository.deleteById(id);
    }

    // Agent CRUD
    @GetMapping("/agents")
    public List<Agent> getAllAgents() {
        return agentRepository.findAll();
    }

    @PostMapping("/agents")
    public Agent createAgent(@RequestBody Agent agent) {
        return agentRepository.save(agent);
    }

    @PutMapping("/agents/{id}")
    public Agent updateAgent(@PathVariable Long id, @RequestBody Agent details) {
        Agent agent = agentRepository.findById(id).orElseThrow();
        agent.setName(details.getName());
        agent.setAgency(details.getAgency());
        agent.setAddress(details.getAddress());
        agent.setPhone(details.getPhone());
        agent.setEmail(details.getEmail());
        agent.setLanguages(details.getLanguages());
        agent.setRating(details.getRating());
        return agentRepository.save(agent);
    }

    @DeleteMapping("/agents/{id}")
    public void deleteAgent(@PathVariable Long id) {
        agentRepository.deleteById(id);
    }

    // User Management
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }
}
