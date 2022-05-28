package ru.netology.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardInfo {
        private String numberCard;
        private String month;
        private String year;
        private String ownerCard;
        private String cvc;

}
