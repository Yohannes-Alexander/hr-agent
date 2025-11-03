package com.linovhr;

import org.json.JSONObject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HRChatAgent {

    private final HRFunctions hrFunctions;
    private final GeminiService geminiService;

    public HRChatAgent(HRFunctions hrFunctions, GeminiService geminiService) {
        this.hrFunctions = hrFunctions;
        this.geminiService = geminiService;
    }

    public String handleUserInput(String input) {
        // Panggil Gemini API untuk analisis
        String geminiResponse = geminiService.analyzeCommand(input);
        geminiResponse = geminiResponse.trim();
        if (geminiResponse.startsWith("\"") && geminiResponse.endsWith("\"")) {
            geminiResponse = geminiResponse.substring(1, geminiResponse.length() - 1);
        }        

        // Parsing hasil JSON Gemini
        try {
            JSONObject root = new JSONObject(geminiResponse);
            // Ambil konten teks dari model
            String text = root.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");
            text = text.replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();
            JSONObject result = new JSONObject(text);

            String aksi = result.optString("aksi");
            switch (aksi) {
                case "apply_cuti":
                    return hrFunctions.applyForLeave(
                            result.getString("nama"),
                            result.getString("tipe"),
                            LocalDate.parse(result.getString("mulai")),
                            LocalDate.parse(result.getString("selesai"))
                    );

                case "jadwalkan_review":
                    return hrFunctions.schedulePerformanceReview(
                            result.getString("nama"),
                            result.getString("reviewer"),
                            LocalDate.parse(result.getString("tanggal"))
                    );

                case "cek_status_cuti":
                    return hrFunctions.checkLeaveRequestStatus(result.getString("nama"));

                case "cari_rekan":
                    return hrFunctions.lookupColleagueInfo(result.getString("nama"));

                case "submit_expense":
                    return hrFunctions.submitExpenseReport(
                            result.getString("nama"),
                            result.getString("kategori"),
                            result.getDouble("jumlah")
                    );                    

                default:
                    return "Maaf, saya belum memahami perintah tersebut.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Terjadi kesalahan saat memproses input: " + e.getMessage();
        }
    }
}