package com.linovhr.utils;

import java.io.*;
import java.util.*;

public class CSVUtils {

    private static final String BASE_PATH = "src/main/java/com/linovhr/";

    /** Baca CSV dan ubah menjadi List<Map> */
    public static List<Map<String, String>> readCSV(String fileName) {
        List<Map<String, String>> data = new ArrayList<>();
        File file = new File(BASE_PATH + fileName);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String headerLine = br.readLine();
            if (headerLine == null) return data;

            String[] headers = headerLine.split("\t|,");
            String line;

            while ((line = br.readLine()) != null) {
                String[] values = line.split("\t|,");
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < Math.min(headers.length, values.length); i++) {
                    row.put(headers[i].trim(), values[i].trim());
                }
                data.add(row);
            }

        } catch (IOException e) {
            System.err.println("❌ Gagal membaca file: " + file.getAbsolutePath());
            e.printStackTrace();
        }

        return data;
    }

public static void appendToCSV(String fileName, String newLine) {
    File file = new File(BASE_PATH + fileName);
    boolean needsNewline = true;

    try {
        // Cek apakah file sudah punya newline di akhir
        if (file.exists() && file.length() > 0) {
            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                raf.seek(file.length() - 1);
                byte lastByte = raf.readByte();
                if (lastByte == '\n' || lastByte == '\r') {
                    needsNewline = false;
                }
            }
        }

        // Tambahkan data baru ke file tanpa newline ganda
        try (FileWriter fw = new FileWriter(file, true)) {
            String cleanLine = newLine.replaceAll("[\\r\\n]+$", ""); // buang newline
            if (needsNewline) fw.write(System.lineSeparator());
            fw.write(cleanLine);
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}


    /** Overwrite seluruh file (opsional, kalau kamu butuh update penuh) */
    public static void overwriteCSV(String fileName, List<String> lines) {
        File file = new File(BASE_PATH + fileName);

        try (FileWriter writer = new FileWriter(file, false)) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            System.err.println("❌ Gagal menulis ulang file: " + file.getAbsolutePath());
            e.printStackTrace();
        }
    }
}
