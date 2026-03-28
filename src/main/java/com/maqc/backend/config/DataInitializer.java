package com.maqc.backend.config;

import com.maqc.backend.model.Property;
import com.maqc.backend.model.User;
import com.maqc.backend.repository.PropertyRepository;
import com.maqc.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

        private final PropertyRepository propertyRepository;
        private final UserRepository userRepository;

        @Override
        public void run(String... args) {
                if (propertyRepository.count() == 0) {
                        saveMockUsers();
                        saveCoreMockData();
                        saveAdditionalMockData();
                }
        }

        private void saveMockUsers() {
                if (userRepository.count() == 0) {
                        User admin = User.builder()
                                        .email("admin@maqc.com")
                                        .password(BCrypt.hashpw("admin123", BCrypt.gensalt()))
                                        .firstName("Admin")
                                        .lastName("User")
                                        .role(User.Role.ADMIN)
                                        .planType(User.PlanType.PRO)
                                        .phoneNumber("514-123-4567")
                                        .build();
                        admin.setAgency("MAQC Real Estate");
                        admin.setBio("System Administrator");

                        User agent1 = User.builder()
                                        .email("agent1@maqc.com")
                                        .password(BCrypt.hashpw("agent123", BCrypt.gensalt()))
                                        .firstName("Jean")
                                        .lastName("Dupont")
                                        .role(User.Role.USER)
                                        .planType(User.PlanType.PRO)
                                        .phoneNumber("514-234-5678")
                                        .build();
                        agent1.setAgency("MAQC Real Estate");
                        agent1.setBio("Experienced real estate agent specializing in luxury properties");

                        User agent2 = User.builder()
                                        .email("agent2@maqc.com")
                                        .password(BCrypt.hashpw("agent123", BCrypt.gensalt()))
                                        .firstName("Marie")
                                        .lastName("Martin")
                                        .role(User.Role.USER)
                                        .planType(User.PlanType.BASIC)
                                        .phoneNumber("514-345-6789")
                                        .build();
                        agent2.setAgency("MAQC Real Estate");
                        agent2.setBio("Residential property expert");

                        User agent3 = User.builder()
                                        .email("agent3@maqc.com")
                                        .password(BCrypt.hashpw("agent123", BCrypt.gensalt()))
                                        .firstName("Pierre")
                                        .lastName("Lambert")
                                        .role(User.Role.USER)
                                        .planType(User.PlanType.PLUS)
                                        .phoneNumber("514-456-7890")
                                        .build();
                        agent3.setAgency("MAQC Real Estate");
                        agent3.setBio("Commercial and industrial properties specialist");

                        userRepository.saveAll(Arrays.asList(admin, agent1, agent2, agent3));
                }
        }

        private void saveCoreMockData() {
                List<User> users = userRepository.findAll();
                User agent1 = users.get(0);
                User agent2 = users.get(1);
                User agent3 = users.get(2);

                List<Property> coreProperties = Arrays.asList(
                                // Montreal area - Houses - assigned to agent1
                                createProperty("Triplex à vendre", "1994-1998, boulevard De Maisonneuve, Montreal",
                                                1198000, 2, 1, 5, 1995, true, false, true, 36000.0, 12000.0,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&q=80&w=800",
                                                agent1, null, Property.PropertyStatus.APPROVED),
                                createProperty("Maison de plain-pied", "4530 Rue de la Roche, Montreal", 849000, 3, 2,
                                                6, 1985, true, false, true, 30000.0, 10000.0,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&q=80&w=800",
                                                agent1, null, Property.PropertyStatus.APPROVED),
                                // Verification Data: Same Cap Rate (3.0%), Different Prices
                                createProperty("Propriété Test A (High Price)", "Test Address A", 1000000, 4, 3,
                                                8, 2010, true, true, true, 50000.0, 20000.0,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1600566753190-17f0bb2a6c3e?auto=format&fit=crop&q=80&w=800",
                                                agent2, null, Property.PropertyStatus.APPROVED),
                                createProperty("Propriété Test B (Low Price)", "Test Address B", 500000, 3, 2,
                                                6, 2015, true, false, true, 25000.0, 10000.0,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1600585154526-990dcee01e7e?auto=format&fit=crop&q=80&w=800",
                                                agent2, null, Property.PropertyStatus.APPROVED),
                                createProperty("Maison à étages", "782 Rue Gariépy, Quebec City", 925000, 4, 3, 8, 1990,
                                                true, false, false, 35000.0, 11000.0,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1570129477492-45c003edd2be?auto=format&fit=crop&q=80&w=800",
                                                agent2, null, Property.PropertyStatus.APPROVED),
                                createProperty("Maison contemporaine", "2345 Rue Sherbrooke E., Montreal", 1250000, 4,
                                                3, 7, 2010, true, true, true, 45000.0, 15000.0,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1480074568708-e7b720bb3f09?auto=format&fit=crop&q=80&w=800",
                                                agent2, null, Property.PropertyStatus.APPROVED),
                                // Montreal area - Condos/Apartments - assigned to agent1
                                createProperty("Condo à vendre", "1200 Rue Saint-Alexandre, apt. 402, Montreal", 549000,
                                                1, 1, 2, 2015, false, false, false, 24000.0, 8000.0,
                                                Property.PropertyType.CONDO, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&q=80&w=800",
                                                agent1, null, Property.PropertyStatus.APPROVED),
                                createProperty("Magnifique Loft", "101 Rue de la Commune E., Montreal", 725000, 1, 1,
                                                2, 2018, false, false, false, 30000.0, 10000.0,
                                                Property.PropertyType.CONDO, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1560448204-61dc36dc98c8?auto=format&fit=crop&q=80&w=800",
                                                agent1, null, Property.PropertyStatus.PENDING),
                                createProperty("Pied-à-terre Moderne", "1500 Rue University, Montreal", 399000, 1, 1,
                                                2, 2020, false, false, false, 18000.0, 6000.0,
                                                Property.PropertyType.CONDO, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&q=80&w=800",
                                                agent2, null, Property.PropertyStatus.APPROVED),
                                createProperty("Condo de Luxe", "1100 Rue de la Montagne, Montreal", 899000, 2, 2,
                                                4, 2016, false, true, false, 38000.0, 12000.0,
                                                Property.PropertyType.CONDO, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1512915920307-446866dd391f?auto=format&fit=crop&q=80&w=800",
                                                agent2, null, Property.PropertyStatus.PENDING),
                                // Montreal area - House
                                createProperty("Maison jumelée", "3450 Avenue du Parc, Montreal", 1350000, 5, 3,
                                                8, 2005, true, true, true, 42000.0, 14000.0,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&q=80&w=800",
                                                agent3, null, Property.PropertyStatus.APPROVED),
                                // Sherbrooke area
                                createProperty("Villa d'Exception", "12 Shoreline Dr, Sherbrooke", 4200000, 6, 5,
                                                10, 2015, true, true, true, 60000.0, 20000.0,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1613490493576-7fde63acd811?auto=format&fit=crop&q=80&w=800",
                                                agent3, null, Property.PropertyStatus.APPROVED),
                                // Laval area
                                createProperty("Maison spacieuse à Laval", "1230 Boulevard Saint-Martin, Laval", 750000,
                                                4, 2, 6, 1998, true, false, true, 32000.0, 11000.0,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1600596542815-27b88e54e75b?auto=format&fit=crop&q=80&w=800",
                                                agent1, null, Property.PropertyStatus.APPROVED),
                                // Westmount
                                createProperty("Luxury Home Westmount", "4500 Avenue de Westmount, Westmount", 2500000,
                                                5, 4, 9, 2012, true, true, true, 55000.0, 18000.0,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?auto=format&fit=crop&q=80&w=800",
                                                agent2, null, Property.PropertyStatus.APPROVED),
                                // Brossard
                                createProperty("Condo moderne Brossard", "5000 Rue de la Seigneurie, Brossard", 425000,
                                                2, 1, 3, 2019, false, false, false, null, null,
                                                Property.PropertyType.CONDO, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?auto=format&fit=crop&q=80&w=800",
                                                agent3, null, Property.PropertyStatus.PENDING),
                                // Longueuil
                                createProperty("Maison familiale Longueuil", "8900 Rue Saint-Laurent, Longueuil",
                                                625000, 3, 2, 5, 1980, true, false, true, 28000.0, 9000.0,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&q=80&w=800",
                                                agent2, null, Property.PropertyStatus.APPROVED),
                                // Lévis
                                createProperty("Maison avec vue", "450 Rue de la Rivière, Lévis", 550000, 3, 2,
                                                5, 1975, true, false, true, 25000.0, 8000.0,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1600047509807-ba8f99d2cdde?auto=format&fit=crop&q=80&w=800",
                                                agent3, null, Property.PropertyStatus.APPROVED),
                                // Plex/Commercial examples
                                createProperty("Triplex à vendre", "Lots des Érables, Montreal", 350000, 0, 0, 0, 1980,
                                                false, false, false, 42000.0, 8000.0,
                                                Property.PropertyType.PLEX, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&q=80&w=800",
                                                agent1, 5.5, Property.PropertyStatus.APPROVED),
                                createProperty("Duplex investissement", "Avenue du Parc, Montreal", 425000, 0, 0, 0,
                                                1950, false, false, false, 38000.0, 7000.0,
                                                Property.PropertyType.PLEX, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&q=80&w=800",
                                                agent2, 6.2, Property.PropertyStatus.PENDING),
                                createProperty("Triplex revenue", "Rue Saint-Laurent, Montreal", 525000, 0, 0, 0, 1965,
                                                false, false, false, 45000.0, 8500.0,
                                                Property.PropertyType.PLEX, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1600596542815-27b88e54e75b?auto=format&fit=crop&q=80&w=800",
                                                agent3, 4.8, Property.PropertyStatus.APPROVED),
                                // Commercial properties with different business types
                                createProperty("Restaurant d'élite", "Vieux-Montréal, Quebec", 850000, 0, 0, 0, 2015,
                                                true, false, false, 95000.0, 28000.0,
                                                Property.PropertyType.COMMERCIAL, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&q=80&w=800",
                                                agent1, null, Property.PropertyStatus.APPROVED,
                                                Property.BusinessType.RESTAURANT),
                                createProperty("Bureau médical", "Complexe santé, Montreal", 650000, 0, 0, 0, 2018,
                                                false, false, false, 72000.0, 18000.0,
                                                Property.PropertyType.COMMERCIAL, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1507361174552-0949874c9308?auto=format&fit=crop&q=80&w=800",
                                                agent2, null, Property.PropertyStatus.APPROVED,
                                                Property.BusinessType.MEDICAL),
                                createProperty("Espace commercial", "Centre commercial Place Versailles, Laval",
                                                1200000, 0, 0, 0, 2000, true, false, false, 85000.0, 25000.0,
                                                Property.PropertyType.COMMERCIAL, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&q=80&w=800",
                                                agent3, null, Property.PropertyStatus.APPROVED,
                                                Property.BusinessType.RETAIL));
                propertyRepository.saveAll(coreProperties);
        }

        private void saveAdditionalMockData() {
                List<User> users = userRepository.findAll();
                User agent1 = users.get(0);
                User agent2 = users.get(1);
                User agent3 = users.get(2);

                for (int i = 11; i <= 40; i++) {
                        Property property = new Property();
                        property.setTitle("Property " + i);
                        property.setDescription(
                                        "Luxury residence with modern amenities and beautiful architectural details.");
                        property.setPrice(new BigDecimal(400000 + (i * 25000)));
                        // Use only the three city types: montreal, quebec, sherbrooke
                        String[] cities = { "montreal", "quebec city", "sherbrooke", "laval", "westmount",
                                        "longueuil", "lévis", "sherbrooke" };
                        String city = cities[i % 8];
                        // Set area based on city
                        property.setArea(getAreaFromCity(city));
                        property.setAddress(i + " Rue de l'Innovation, " + capitalize(city));
                        property.setBedrooms(i % 4 + 1);
                        property.setBathrooms(i % 2 + 1);
                        property.setRooms(i % 4 + 3); // rooms = bedrooms + common areas
                        property.setYearBuilt(1990 + (i % 30)); // random year between 1990-2020
                        // Random outdoor features
                        property.setHasTerrace(i % 3 == 0);
                        property.setHasPool(i % 5 == 0);
                        property.setHasYard(i % 2 == 0);
                        // Determine property type and listing type
                        Property.PropertyType type = i % 2 == 0 ? Property.PropertyType.HOUSE
                                        : Property.PropertyType.CONDO;
                        property.setType(type);
                        boolean isForRent = i % 5 == 0;
                        property.setListingType(
                                        isForRent ? Property.ListingType.FOR_RENT : Property.ListingType.FOR_SALE);
                        // Set investment fields for FOR_SALE properties (to show cap rate)
                        if (!isForRent) {
                                // Calculate reasonable annual rent based on property price (0.5-0.8% of price)
                                double annualRent = property.getPrice().doubleValue() * (0.005 + (i % 30) * 0.0001);
                                double annualExpenses = annualRent * (0.2 + (i % 10) * 0.02); // 20-40% of rent
                                property.setAnnualRent(annualRent);
                                property.setAnnualExpenses(annualExpenses);
                        }
                        property.setImageUrls(Arrays.asList(
                                        "https://images.unsplash.com/photo-1600566753376-12c8ab7fb75b?auto=format&fit=crop&q=80&w=800"));
                        // Assign to random agent
                        User assignedAgent = i % 3 == 0 ? agent1 : (i % 3 == 1 ? agent2 : agent3);
                        property.setAgent(assignedAgent);
                        // Set status: 2/3 approved, 1/3 pending
                        property.setStatus(i % 3 == 0 ? Property.PropertyStatus.PENDING
                                        : Property.PropertyStatus.APPROVED);
                        // Set publishDate to create variety for sorting
                        property.setPublishDate(java.time.LocalDateTime.now().minusDays((long) (i * 2)));
                        propertyRepository.save(property);
                }
        }

        private Property createProperty(String title, String address, double price, int beds, int baths, int rooms,
                        Integer yearBuilt,
                        Boolean hasTerrace, Boolean hasPool, Boolean hasYard, Double annualRent, Double annualExpenses,
                        Property.PropertyType type, Property.ListingType listingType, String imageUrl, User user,
                        Double yield, Property.PropertyStatus status, Property.BusinessType businessType) {
                Property p = new Property();
                p.setTitle(title);
                p.setAddress(address);
                // Extract city from address (last part after comma) and set area based on it
                String city = extractCityFromAddress(address);
                p.setArea(getAreaFromCity(city));
                p.setPrice(new BigDecimal(price));
                p.setBedrooms(beds);
                p.setBathrooms(baths);
                p.setRooms(rooms);
                p.setYearBuilt(yearBuilt);
                p.setHasTerrace(hasTerrace);
                p.setHasPool(hasPool);
                p.setHasYard(hasYard);
                p.setAnnualRent(annualRent);
                p.setAnnualExpenses(annualExpenses);
                p.setType(type);
                p.setBusinessType(businessType);
                p.setListingType(listingType);
                p.setImageUrls(Arrays.asList(imageUrl));
                p.setDescription("A beautiful property with excellent features.");
                p.setAgent(user);
                p.setStatus(status != null ? status : Property.PropertyStatus.PENDING);
                if (yield != null) {
                        p.setYield(yield);
                }
                // Set publishDate to current time minus some random days to create variety
                p.setPublishDate(java.time.LocalDateTime.now().minusDays((long) (Math.random() * 30)));
                return p;
        }

        // Overloaded method for backward compatibility
        private Property createProperty(String title, String address, double price, int beds, int baths, int rooms,
                        Integer yearBuilt,
                        Boolean hasTerrace, Boolean hasPool, Boolean hasYard, Double annualRent, Double annualExpenses,
                        Property.PropertyType type, Property.ListingType listingType, String imageUrl, User user,
                        Double yield, Property.PropertyStatus status) {
                return createProperty(title, address, price, beds, baths, rooms, yearBuilt, hasTerrace, hasPool,
                                hasYard, annualRent, annualExpenses, type, listingType, imageUrl, user, yield, status,
                                null);
        }

        private String extractCityFromAddress(String address) {
                if (address == null)
                        return null;
                String[] parts = address.split(",");
                if (parts.length > 0) {
                        return parts[parts.length - 1].trim();
                }
                return address;
        }

        private String getAreaFromCity(String city) {
                if (city == null)
                        return null;
                String lowerCity = city.toLowerCase();
                switch (lowerCity) {
                        case "montreal":
                        case "laval":
                        case "westmount":
                        case "brossard":
                        case "longueuil":
                                return "Montreal Region";
                        case "quebec city":
                        case "lévis":
                                return "Quebec City Region";
                        case "sherbrooke":
                                return "Sherbrooke Region       ";
                        default:
                                return city;
                }
        }

        private String capitalize(String str) {
                if (str == null || str.isEmpty())
                        return str;
                return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
}
