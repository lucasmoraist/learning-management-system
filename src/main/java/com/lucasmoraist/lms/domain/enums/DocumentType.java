package com.lucasmoraist.lms.domain.enums;

public enum DocumentType {
    CPF,
    CNPJ;

    public static DocumentType findDocumentByValue(String value) {
        if (isValidCPF(value)) {
            return CPF;
        } else if (isValidCNPJ(value)) {
            return CNPJ;
        } else {
            throw new IllegalArgumentException("Document invalid: " + value);
        }
    }

    private static final int[] WEIGHT_CPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] WEIGHT_CNPJ = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private static boolean isValidCPF(String cpf) {
        if (cpf == null) return false;

        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        int digit1 = calculateDigit(cpf.substring(0, 9), WEIGHT_CPF);
        int digit2 = calculateDigit(cpf.substring(0, 9) + digit1, WEIGHT_CPF);

        return cpf.equals(cpf.substring(0, 9) + digit1 + digit2);
    }

    public static boolean isValidCNPJ(String cnpj) {
        if (cnpj == null) return false;

        cnpj = cnpj.replaceAll("\\D", "");

        if (cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}")) return false;

        int digit1 = calculateDigit(cnpj.substring(0, 12), WEIGHT_CNPJ);
        int digit2 = calculateDigit(cnpj.substring(0, 12) + digit1, WEIGHT_CNPJ);

        return cnpj.equals(cnpj.substring(0, 12) + digit1 + digit2);
    }

    private static int calculateDigit(String str, int[] weight) {
        int sum = 0;
        int offset = weight.length - str.length();
        for (int i = str.length() - 1; i >= 0; i--) {
            int digit = str.charAt(i) - '0';
            sum += digit * weight[offset + i];
        }
        sum = 11 - sum % 11;
        return sum > 9 ? 0 : sum;
    }

}
