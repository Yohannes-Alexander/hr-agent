package com.linovhr;

import  com.linovhr.utils.CSVUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class RealHRFunctions implements HRFunctions {

    private static final String DATA_DIR =  "data/"; 
    private static final String EMP_FILE = DATA_DIR + "employees.csv";
    private static final String LEAVE_BAL_FILE = DATA_DIR + "leave_balances.csv";
    private static final String LEAVE_REQ_FILE = DATA_DIR + "leave_requests.csv";
    private static final String REVIEW_FILE = DATA_DIR + "performance_reviews.csv";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));

    @Override
    public String applyForLeave(String employeeName, String leaveType, LocalDate startDate, LocalDate endDate) {
        List<Map<String, String>> employees = CSVUtils.readCSV(EMP_FILE);
        Optional<Map<String, String>> emp = employees.stream()
            .filter(e -> e.get("nama") != null &&
                        e.get("nama").toLowerCase().contains(employeeName.toLowerCase()))
            .findFirst();


        if (emp.isEmpty()) {
            return "Karyawan bernama " + employeeName + " tidak ditemukan.";
        }

        Map<String, String> employee = emp.get();
        String idKaryawan = employee.get("id");

        // 🔹 Buat ID request baru otomatis berdasarkan jumlah baris di CSV
        List<Map<String, String>> existingRequests = CSVUtils.readCSV(LEAVE_REQ_FILE);
        String newId = String.format("LR%03d", existingRequests.size() + 1);

        // 🔹 Status default = "Menunggu Persetujuan"
        String status = "Menunggu Persetujuan";

        // 🔹 Format baris baru untuk CSV
        String newLine = String.join(",",
                newId,
                idKaryawan,
                leaveType,
                startDate.toString(),
                endDate.toString(),
                status
        );

        // 🔹 Simpan ke file leave_requests.csv (folder data)
        CSVUtils.appendToCSV(LEAVE_REQ_FILE, newLine);

        // 🔹 Pesan ke user
        String message = String.format(
            "KONFIRMASI: Pengajuan cuti %s (%s) dari %s hingga %s telah dicatat dan menunggu persetujuan.",
            employeeName, leaveType, startDate.format(formatter), endDate.format(formatter)
        );

        return message;
    }


    @Override
    public String schedulePerformanceReview(String employeeName, String reviewerName, LocalDate reviewDate) {
        List<Map<String, String>> employees = CSVUtils.readCSV(EMP_FILE);

        List<Map<String, String>> employeeMatches = employees.stream()
                .filter(e -> e.get("nama").toLowerCase().contains(employeeName.toLowerCase()))
                .toList();

        List<Map<String, String>> reviewerMatches = employees.stream()
                .filter(e -> e.get("nama").toLowerCase().contains(reviewerName.toLowerCase()))
                .toList();

        if (employeeMatches.isEmpty()) {
            return "Karyawan bernama " + employeeName + " tidak ditemukan di data.";
        }
        if (employeeMatches.size() > 1) {
            String names = employeeMatches.stream().map(e -> e.get("nama")).collect(Collectors.joining(", "));
            return "Ditemukan beberapa karyawan dengan nama mirip: " + names + ". Mohon sebutkan nama lengkap karyawan.";
        }

        if (reviewerMatches.isEmpty()) {
            return "Reviewer bernama " + reviewerName + " tidak ditemukan di data.";
        }
        if (reviewerMatches.size() > 1) {
            String names = reviewerMatches.stream().map(e -> e.get("nama")).collect(Collectors.joining(", "));
            return "Ditemukan beberapa reviewer dengan nama mirip: " + names + ". Mohon sebutkan nama lengkap reviewer.";
        }

        Map<String, String> emp = employeeMatches.get(0);
        Map<String, String> rev = reviewerMatches.get(0);

        String empName = emp.get("nama");
        String revName = rev.get("nama");
        String empId = emp.get("id");
        String revId = rev.get("id");

        // ✅ Format tanggal konsisten yyyy-MM-dd
        String dateForCsv = reviewDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String formattedDate = reviewDate.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", new Locale("id", "ID")));

        // Generate ID baru
        String reviewId = "PR" + String.format("%02d", new Random().nextInt(90) + 10);

        // ✅ Tulis data baru dengan newline aman
        String newLine = String.format("%s,%s,%s,%s,%d,%s",
                reviewId, empId, revId, dateForCsv, 0, "Terjadwal");
        CSVUtils.appendToCSV(REVIEW_FILE, newLine);

        return String.format(
                "KONFIRMASI: Review performa untuk %s bersama %s telah dijadwalkan pada %s.",
                empName, revName, formattedDate
        );
    }


    @Override
    public String checkLeaveRequestStatus(String employeeName) {
        List<Map<String, String>> employees = CSVUtils.readCSV(EMP_FILE);
        List<Map<String, String>> leaveReq = CSVUtils.readCSV(LEAVE_REQ_FILE);

        // Cari semua nama yang mirip (LIKE)
        List<Map<String, String>> matches = employees.stream()
            .filter(e -> e.get("nama").toLowerCase().contains(employeeName.toLowerCase()))
            .toList();

        if (matches.isEmpty()) {
            return "Karyawan bernama " + employeeName + " tidak ditemukan.";
        }

        if (matches.size() > 1) {
            // Kalau ada beberapa nama mirip, tampilkan semua untuk disambiguasi
            String names = matches.stream()
                    .map(e -> e.get("nama"))
                    .collect(Collectors.joining(", "));
            return "Ditemukan beberapa karyawan dengan nama mirip: " + names + ". Mohon sebutkan nama lengkap.";
        }

        // Ambil karyawan yang cocok
        Map<String, String> emp = matches.get(0);
        String empId = emp.get("id");
        String empName = emp.get("nama");

        // Cari pengajuan cuti terakhir berdasarkan tanggal mulai
        Optional<Map<String, String>> latestLeave = leaveReq.stream()
            .filter(lr -> lr.get("id_karyawan").equals(empId))
            .max(Comparator.comparing(lr -> lr.get("tanggal_mulai")));

        if (latestLeave.isEmpty()) {
            return "Tidak ada pengajuan cuti ditemukan untuk " + empName + ".";
        }

        Map<String, String> lr = latestLeave.get();

        return String.format(
            "INFO: Status pengajuan cuti terakhir %s (%s %s–%s) adalah: %s.",
            empName,
            lr.get("tipe_cuti"),
            lr.get("tanggal_mulai"),
            lr.get("tanggal_selesai"),
            lr.get("status_request")
        );
    }

    @Override
    public String submitExpenseReport(String employeeName, String category, double amount) {
        // 🔹 Baca data karyawan
        List<Map<String, String>> employees = CSVUtils.readCSV(EMP_FILE);
        Optional<Map<String, String>> emp = employees.stream()
            .filter(e -> e.get("nama") != null &&
                        e.get("nama").toLowerCase().contains(employeeName.toLowerCase()))
            .findFirst();

        if (emp.isEmpty()) {
            return "Karyawan bernama " + employeeName + " tidak ditemukan.";
        }

        Map<String, String> employee = emp.get();
        String idKaryawan = employee.get("id");

        // 🔹 Baca leave balance CSV
        List<Map<String, String>> balances = CSVUtils.readCSV(LEAVE_BAL_FILE);

        // 🔹 Tambah baris baru
        String newLine = String.join(",",
                idKaryawan,
                category,              // kategori dianggap sebagai tipe_cuti
                String.valueOf((int) amount) // isi jumlah atau hari cuti (misal sisa_hari)
        );

        // 🔹 Simpan ke file leave_balances.csv
        CSVUtils.appendToCSV(LEAVE_BAL_FILE, newLine);

        // 🔹 Pesan konfirmasi
        return String.format(
            "KONFIRMASI: %s telah mengajukan laporan pengeluaran kategori '%s' sebesar '%s' " +
            "Data telah dicatat di leave_balances.csv.",
            employeeName, category, amount
        );
    }

    @Override
    public String lookupColleagueInfo(String colleagueName) {
        List<Map<String, String>> employees = CSVUtils.readCSV(EMP_FILE);

        // Cari semua nama yang mengandung teks pencarian (case-insensitive)
        List<Map<String, String>> matches = employees.stream()
            .filter(e -> e.get("nama").toLowerCase().contains(colleagueName.toLowerCase()))
            .toList();

        if (matches.isEmpty()) {
            return "Karyawan dengan nama " + colleagueName + " tidak ditemukan.";
        }

        if (matches.size() > 1) {
            // Jika lebih dari satu cocok, beri tahu pengguna
            String names = matches.stream()
                    .map(e -> e.get("nama"))
                    .collect(Collectors.joining(", "));
            return "Ditemukan beberapa karyawan dengan nama mirip: " + names + ". Mohon sebutkan nama lengkap.";
        }

        // Ambil data karyawan pertama yang cocok
        Map<String, String> emp = matches.get(0);

        return String.format(
            "INFO: %s - Jabatan: %s, Departemen: %s, Email: %s.",
            emp.get("nama"), emp.get("jabatan"), emp.get("departemen"), emp.get("email")
        );
    }
}