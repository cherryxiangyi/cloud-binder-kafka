package com.amway.dms.springbinder.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity implements Serializable {
    Integer aff;
    Long abo;

}

