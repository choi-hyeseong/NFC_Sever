package com.comet.nfc_sever.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WebResponse<T> {

    private String message;
    private T data;

}
