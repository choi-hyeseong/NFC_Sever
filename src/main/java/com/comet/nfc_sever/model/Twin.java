package com.comet.nfc_sever.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Twin<F, S> {

    private F first;
    private S second;
}
