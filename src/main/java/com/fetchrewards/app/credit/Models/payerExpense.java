package com.fetchrewards.app.credit.Models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class payerExpense {
    private String payer;
    private int points;
}
