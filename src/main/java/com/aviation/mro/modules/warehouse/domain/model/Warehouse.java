package com.aviation.mro.modules.warehouse.domain.model;
import com.aviation.mro.modules.warehouse.domain.enums.WarehouseType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @NotBlank
    @Column(unique = true)
    private String code; // کد انبار (مثلاً: WH-MAIN, WH-SPARES)

    @Nationalized
    @NotBlank
    private String name;

    @Nationalized
    private String description;

    @Nationalized
    private String location; // موقعیت فیزیکی

    @Enumerated(EnumType.STRING)
    private WarehouseType type = WarehouseType.MAIN;

    private boolean active = true;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StorageLocation> storageLocations = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Warehouse(String code, String name, String location) {
        this.code = code;
        this.name = name;
        this.location = location;
    }
}
