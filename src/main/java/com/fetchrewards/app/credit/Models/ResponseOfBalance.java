package com.fetchrewards.app.credit.Models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import java.util.HashMap;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ResponseOfBalance {
    private HashMap<String, Integer> balance;
}
