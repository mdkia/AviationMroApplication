package com.aviation.mro.modules.repair.domain.enums;

public enum RepairPriority {
    CRITICAL,    // Grounded aircraft
    HIGH,        // AOG (Aircraft On Ground)
    MEDIUM,      // Scheduled maintenance
    LOW,          // Routine checks
    AOG
}
