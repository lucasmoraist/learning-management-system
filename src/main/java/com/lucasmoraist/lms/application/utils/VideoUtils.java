package com.lucasmoraist.lms.application.utils;

import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@UtilityClass
public class VideoUtils {

    public static Integer getVideoDurationInSeconds(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[8];

            // Varre o InputStream procurando pelo atom 'mvhd'
            while (is.read(header) == 8) {
                int boxSize = ByteBuffer.wrap(header, 0, 4).getInt();
                String boxType = new String(header, 4, 4);

                if (boxType.equals("moov") || boxType.equals("trak")) {
                    // São containers de outros blocos, continuamos a ler o conteúdo interno deles
                    continue;
                }

                if (boxType.equals("mvhd")) {
                    // Encontrámos o Movie Header Box!
                    // Lemos os metadados do cabeçalho mvhd
                    byte[] mvhdData = new byte[20];
                    if (is.read(mvhdData) != 20) {
                        break;
                    }

                    // O primeiro byte representa a versão do atom 'mvhd'
                    byte version = mvhdData[0];

                    long timescale;
                    long duration;

                    if (version == 1) {
                        // Versão 1: Usa 64-bits (8 bytes) para timescale e duration
                        byte[] extendedData = new byte[20];
                        if (is.read(extendedData) != 20) break;

                        timescale = ByteBuffer.wrap(extendedData, 4, 4).getInt() & 0xFFFFFFFFL;
                        duration = ByteBuffer.wrap(extendedData, 8, 8).getLong();
                    } else {
                        // Versão 0: Usa 32-bits (4 bytes) para timescale e duration
                        timescale = ByteBuffer.wrap(mvhdData, 12, 4).getInt() & 0xFFFFFFFFL;
                        duration = ByteBuffer.wrap(mvhdData, 16, 4).getInt() & 0xFFFFFFFFL;
                    }

                    if (timescale > 0) {
                        return Math.toIntExact(duration / timescale);
                    }
                    break;
                }

                // Se não for o bloco que queremos, saltamos os bytes restantes dele no stream
                if (boxSize > 8) {
                    long skipped = is.skip(boxSize - 8);
                    // Garante que o skip pulou o tamanho correto
                    while (skipped < (boxSize - 8)) {
                        long nextSkip = is.skip((boxSize - 8) - skipped);
                        if (nextSkip <= 0) break;
                        skipped += nextSkip;
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível ler os metadados do vídeo nativamente", e);
        }

        // Retorna 0 caso não consiga mapear (ex: arquivo que não é MP4 válido)
        return 0;
    }

}
