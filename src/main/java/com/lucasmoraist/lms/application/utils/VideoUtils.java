package com.lucasmoraist.lms.application.utils;

import com.lucasmoraist.lms.domain.exceptions.VideoMetadataException;
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

            while (is.read(header) == 8) {
                int boxSize = ByteBuffer.wrap(header, 0, 4).getInt();
                String boxType = new String(header, 4, 4);

                if (boxType.equals("moov") || boxType.equals("trak")) {
                    continue;
                }

                if (boxType.equals("mvhd")) {
                    byte[] mvhdData = new byte[20];
                    if (is.read(mvhdData) < 20) {
                        break;
                    }

                    int timescale = ByteBuffer.wrap(mvhdData, 12, 4).getInt();
                    long duration = ByteBuffer.wrap(mvhdData, 16, 4).getInt() & 0xffffffffL;

                    if (timescale > 0) {
                        return (int) (duration / timescale);
                    }
                } else {
                    long skipped = is.skip(boxSize - 8);
                    while (skipped < (boxSize - 8)) {
                        long nextSkip = is.skip((boxSize - 8) - skipped);
                        if (nextSkip <= 0) {
                            break;
                        }
                        skipped += nextSkip;
                    }
                }
            }
        } catch (IOException e) {
            throw new VideoMetadataException("Could not read video metadata", e);
        }

        return 0;
    }

}
