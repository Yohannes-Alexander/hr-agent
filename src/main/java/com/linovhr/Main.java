package com.linovhr;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String apiKey = "GEMINI"; // ganti dengan API key kamu

        HRFunctions hr = new RealHRFunctions();
        GeminiService gemini = new GeminiService(apiKey);
        HRChatAgent agent = new HRChatAgent(hr, gemini);

        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Agen HR Cerdas ===");
        System.out.println("Ketik 'exit' untuk keluar.");

        while (true) {
            System.out.print("\nAnda: ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) break;

            String response = agent.handleUserInput(input);
            System.out.println("Agen HR: " + response);
        }

        scanner.close();
    }
}