package com.linovhr;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiService {

    private static final String API_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private final String apiKey;

    public GeminiService(String apiKey) {
        this.apiKey = apiKey;
    }

    public String analyzeCommand(String userInput) {
        try {
            String prompt = """
            Kamu adalah agen HR perusahaan yang bertugas mengubah perintah bahasa Indonesia menjadi JSON terstruktur.
            Output HARUS berupa JSON valid dan tidak boleh mengandung tanda ``` .
            Jika tidak memahami maksud perintah, kembalikan {"aksi": "tidak_dikenali"}.

            Berikut format keluaran yang diharapkan berdasarkan jenis perintah:

            1️⃣ **Apply Cuti**
            {"aksi": "apply_cuti", "nama": "Budi", "tipe": "tahunan", "mulai": "2025-10-03", "selesai": "2025-10-05"}

            2️⃣ **Jadwalkan Review Performa**
            {"aksi": "jadwalkan_review", "nama": "Rina", "reviewer": "Santi", "tanggal": "2025-11-07"}

            3️⃣ **Cek Status Cuti**
            {"aksi": "cek_status_cuti", "nama": "Budi"}

            4️⃣ **Cari Rekan Kerja**
            {"aksi": "cari_rekan", "nama": "Rina"}

            5️⃣ **Ajukan Laporan Pengeluaran Cuti**
            - Contoh input:
            "tolong buat laporan pengeluaran cuti  sebesar 5 kategori Tahunan"
            - Output JSON:
            {"aksi": "submit_expense", "nama": "Budi", "kategori": "operational", "jumlah": 5}

            ⚠️ Penting:
            - Semua tanggal dalam format YYYY-MM-DD.
            - Tolong gunakan tanggal yang real 
            - Gunakan kapitalisasi nama yang benar (contoh: Budi, Rina, Santi).
            - Jangan tambahkan komentar, tanda ``` atau teks lain di luar JSON.

            Sekarang ubah input berikut menjadi JSON:
            "%s"
            """.formatted(userInput);

            String jsonBody = """
            {
            "contents": [
                {
                "parts": [{ "text": "%s" }]
                }
            ]
            }
            """.formatted(prompt.replace("\"", "\\\""));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

}