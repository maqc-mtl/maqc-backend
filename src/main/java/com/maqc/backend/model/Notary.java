package com.maqc.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "notaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String firm;
    private String address;
    private String phone;
    private String email;

    @ElementCollection
    private List<String> languages;

    private Double rating;
}
