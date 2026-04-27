package com.gamesUP.gamesUP.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "purchase", fetch = FetchType.LAZY)
    private List<PurchaseLine> lines = new ArrayList<PurchaseLine>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private boolean paid = false;

    @Column(nullable = false)
    private boolean delivered = false;

    @Column(nullable = false)
    private boolean archived = false;

    public Double getTotal() {
        Double total = 0D;
        for (PurchaseLine line : this.getLines()) {
            total += line.getPrice() * line.getQuantity();
        }
        return total;
    }
}
