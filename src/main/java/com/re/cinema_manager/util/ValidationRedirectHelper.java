package com.re.cinema_manager.util;

import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ValidationRedirectHelper {

    private ValidationRedirectHelper() {
    }

    public static void flashFieldErrors(RedirectAttributes redirectAttributes,
                                        BindingResult bindingResult,
                                        String flashKey) {
        Map<String, String> errors = new LinkedHashMap<>();
        bindingResult.getFieldErrors().forEach(fe ->
                errors.put(fe.getField(), fe.getDefaultMessage()));
        redirectAttributes.addFlashAttribute(flashKey, errors);
    }
}
