package edu.ucsb.cs156.organic.entities;

import lombok.*;

import jakarta.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity(name = "school")
public class School {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String abbrev;
    private String name;
    private String termRegex;
    private String termDescription;
    private String termError;
}
