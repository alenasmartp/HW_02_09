package de.telran.myshop.HW_02_09;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CardException extends IllegalArgumentException {
    private String text;
    private Long id;
}
