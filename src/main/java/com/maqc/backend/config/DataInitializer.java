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
                        // saveMockUsers();
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
                                                1198000, 2, 1,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&q=80&w=800",
                                                agent1),
                                createProperty("Maison de plain-pied", "4530 Rue de la Roche, Montreal", 849000, 3, 2,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1568605114967-8130f3a36994?auto=format&fit=crop&q=80&w=800",
                                                agent1),
                                createProperty("Maison à étages", "782 Rue Gariépy, Quebec City", 925000, 4, 3,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1570129477492-45c003edd2be?auto=format&fit=crop&q=80&w=800",
                                                agent2),
                                createProperty("Maison contemporaine", "2345 Rue Sherbrooke E., Montreal", 1250000, 4,
                                                3,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1480074568708-e7b720bb3f09?auto=format&fit=crop&q=80&w=800",
                                                agent2),
                                // Montreal area - Condos/Apartments - assigned to agent1
                                createProperty("Condo à vendre", "1200 Rue Saint-Alexandre, apt. 402, Montreal", 549000,
                                                1, 1, Property.PropertyType.CONDO, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&q=80&w=800",
                                                agent1),
                                createProperty("Magnifique Loft", "101 Rue de la Commune E., Montreal", 725000, 1, 1,
                                                Property.PropertyType.CONDO, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1560448204-61dc36dc98c8?auto=format&fit=crop&q=80&w=800",
                                                agent1),
                                createProperty("Pied-à-terre Moderne", "1500 Rue University, Montreal", 399000, 1, 1,
                                                Property.PropertyType.CONDO, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&q=80&w=800",
                                                agent2),
                                createProperty("Condo de Luxe", "1100 Rue de la Montagne, Montreal", 899000, 2, 2,
                                                Property.PropertyType.CONDO, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1512915920307-446866dd391f?auto=format&fit=crop&q=80&w=800",
                                                agent2),
                                // Montreal area - House
                                createProperty("Maison jumelée", "3450 Avenue du Parc, Montreal", 1350000, 5, 3,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&q=80&w=800",
                                                agent3),
                                // Sherbrooke area
                                createProperty("Villa d'Exception", "12 Shoreline Dr, Sherbrooke", 4200000, 6, 5,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1613490493576-7fde63acd811?auto=format&fit=crop&q=80&w=800",
                                                agent3),
                                // Laval area
                                createProperty("Maison spacieuse à Laval", "1230 Boulevard Saint-Martin, Laval", 750000,
                                                4, 2,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1600596542815-27b88e54e75b?auto=format&fit=crop&q=80&w=800",
                                                agent1),
                                // Westmount
                                createProperty("Luxury Home Westmount", "4500 Avenue de Westmount, Westmount", 2500000,
                                                5, 4,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?auto=format&fit=crop&q=80&w=800",
                                                agent2),
                                // Brossard
                                createProperty("Condo moderne Brossard", "5000 Rue de la Seigneurie, Brossard", 425000,
                                                2, 1,
                                                Property.PropertyType.CONDO, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?auto=format&fit=crop&q=80&w=800",
                                                agent3),
                                // Longueuil
                                createProperty("Maison familiale Longueuil", "8900 Rue Saint-Laurent, Longueuil",
                                                625000, 3, 2,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1600585154526-990dced4db0d?auto=format&fit=crop&q=80&w=800",
                                                agent2),
                                // Lévis
                                createProperty("Maison avec vue", "450 Rue de la Rivière, Lévis", 550000, 3, 2,
                                                Property.PropertyType.HOUSE, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1600047509807-ba8f99d2cdde?auto=format&fit=crop&q=80&w=800",
                                                agent3),
                                // Plex/Commercial examples
                                createProperty("Triplex à vendre", "Lots des Érables, Montreal", 350000, 0, 0,
                                                Property.PropertyType.PLEX, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&q=80&w=800",
                                                agent1),
                                createProperty("Espace commercial", "Centre commercial Place Versailles, Laval",
                                                1200000, 0, 0,
                                                Property.PropertyType.COMMERCIAL, Property.ListingType.FOR_SALE,
                                                "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&q=80&w=800",
                                                agent2));
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
                        // Alternate between HOUSE and CONDO (PLEX and COMMERCIAL also available)
                        property.setType(i % 2 == 0 ? Property.PropertyType.HOUSE : Property.PropertyType.CONDO);
                        property.setListingType(
                                        i % 5 == 0 ? Property.ListingType.FOR_RENT : Property.ListingType.FOR_SALE);
                        property.setImageUrls(Arrays.asList(
                                        "https://images.unsplash.com/photo-1600566753376-12c8ab7fb75b?auto=format&fit=crop&q=80&w=800"));
                        // Assign to random agent
                        User assignedAgent = i % 3 == 0 ? agent1 : (i % 3 == 1 ? agent2 : agent3);
                        property.setAgent(assignedAgent);
                        propertyRepository.save(property);
                }
        }

        private Property createProperty(String title, String address, double price, int beds, int baths,
                        Property.PropertyType type, Property.ListingType listingType, String imageUrl, User user) {
                Property p = new Property();
                p.setTitle(title);
                p.setAddress(address);
                // Extract city from address (last part after comma) and set area based on it
                String city = extractCityFromAddress(address);
                p.setArea(getAreaFromCity(city));
                p.setPrice(new BigDecimal(price));
                p.setBedrooms(beds);
                p.setBathrooms(baths);
                p.setType(type);
                p.setListingType(listingType);
                p.setImageUrls(Arrays.asList(imageUrl));
                p.setDescription("A beautiful property with excellent features.");
                p.setAgent(user);
                return p;
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
