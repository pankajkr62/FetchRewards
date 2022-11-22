package com.fetchrewards.app.credit.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Transaction {
    private String payer;
    private int points;
    private Date timestamp;
}
