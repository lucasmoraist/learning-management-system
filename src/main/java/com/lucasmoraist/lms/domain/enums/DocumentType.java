package com.lucasmoraist.lms.domain.enums;

public enum DocumentType {
    CPF("CPF"),
    RG("RG"),
    CNPJ("CNPJ");

    private final String value;

    DocumentType(String value) {
        this.value = value;
    }

    public String findDocumentByValue(String value) {
        final String cpfRegex = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}";
        final String rgRegex = "\\d{2}\\.\\d{3}\\.\\d{3}-\\d{1}";
        final String cnpjRegex = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}";

        if (value.matches(cpfRegex)) {
            return CPF.value;
        } else if (value.matches(rgRegex)) {
            return RG.value;
        } else if (value.matches(cnpjRegex)) {
            return CNPJ.value;
        } else {
            throw new IllegalArgumentException("Invalid document format: " + value);
        }
    }

}
