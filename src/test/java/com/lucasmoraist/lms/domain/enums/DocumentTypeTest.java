package com.lucasmoraist.lms.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentTypeTest {

    static Stream<Arguments> provideValidDocuments() {
        return Stream.of(
                Arguments.of("86100550865", DocumentType.CPF),
                Arguments.of("12345678000195", DocumentType.CNPJ)
        );
    }

    @ParameterizedTest(name = "value: {0} -> document: {1}")
    @MethodSource("provideValidDocuments")
    void case01(String document, DocumentType expectedType) {
        DocumentType actualType = DocumentType.findDocumentByValue(document);
        assertEquals(expectedType, actualType);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid document")
    void case02() {
        String invalidDocument = "invalid-document";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> DocumentType.findDocumentByValue(invalidDocument));

        assertEquals("Document invalid: " + invalidDocument, exception.getMessage());
    }

}