package com.amway.dms.springbinder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement
public class AutoRenewalRequest implements Serializable {
    @NotNull()
    private Long iboNum;

    @NotNull()
    private Integer affNum;
}
