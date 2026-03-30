package org.example.td5_spring_boo.entity;

import org.example.td5_spring_boo.enums.MovementTypeEnum;

import java.time.Instant;

public record StockMovement(int id, StockValue value, MovementTypeEnum type, Instant creationDatetime) {
}
