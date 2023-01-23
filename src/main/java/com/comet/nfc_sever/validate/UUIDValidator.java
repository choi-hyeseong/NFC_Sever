package com.comet.nfc_sever.validate;

import com.comet.nfc_sever.dto.NfcUserRequestDto;
import com.comet.nfc_sever.service.EncryptService;
import com.comet.nfc_sever.service.NfcUserService;
import com.comet.nfc_sever.util.StringUtil;
import com.comet.nfc_sever.validate.annotation.UUIDValidate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class UUIDValidator implements ConstraintValidator<UUIDValidate, NfcUserRequestDto> {

    private NfcUserService service;
    private EncryptService encryptService;

    @Override
    public boolean isValid(NfcUserRequestDto value, ConstraintValidatorContext context) {
        String id = value.getId();
        if (StringUtil.isUUID(id)) {
            addConstraint(context, "UUID must be encrypted");
            return false;
        }

        String decryptStr = encryptService.decrypt(id);
        if (decryptStr == null || !StringUtil.isUUID(decryptStr)) {
            addConstraint(context, "Not Valid Encryption.");
            return false;
        }

        UUID uuid = UUID.fromString(decryptStr);
        if (service.isUserExist(uuid)) {
            addConstraint(context, "That UUID already exists.");
            return false;
        }
        return true;

    }

    private void addConstraint(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
