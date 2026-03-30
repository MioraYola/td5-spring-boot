package org.example.td5_spring_boo.DTO;

import org.example.td5_spring_boo.enums.UnitTypeEnum;

public record StockValueDTO (
        UnitTypeEnum unit,
        double quantity
){}


