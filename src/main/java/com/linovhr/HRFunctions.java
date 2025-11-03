package com.linovhr;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * PANDUAN UNTUK KANDIDAT:
 * Buatlah sebuah kelas yang mengimplementasikan interface `HRFunctions` ini.
 * Untuk proyek ini, implementasi "mock" sudah cukup. Artinya, fungsi-fungsi ini
 * tidak perlu melakukan logika bisnis yang kompleks, cukup mengembalikan/mencetak 
 * pesan konfirmasi bahwa fungsi berhasil dipanggil dengan parameter yang benar.
 */
public interface HRFunctions {

    /**
     * Memproses pengajuan cuti seorang karyawan.
     * @return String berisi pesan konfirmasi.
     */
    String applyForLeave(String employeeName, String leaveType, LocalDate startDate, LocalDate endDate);

    /**
     * Menjadwalkan sesi review performa.
     * @return String berisi pesan konfirmasi.
     */
    String schedulePerformanceReview(String employeeName, String reviewerName, LocalDate reviewDate);

    /**
     * Memeriksa status pengajuan cuti terakhir dari seorang karyawan.
     * Implementasi mock bisa mencari dari file leave_requests.csv.
     * @return String berisi status cuti.
     */
    String checkLeaveRequestStatus(String employeeName);
    
    /**
     * Mengajukan laporan pengeluaran (expense report).
     * @return String berisi pesan konfirmasi.
     */
    String submitExpenseReport(String employeeName, String category, double amount);

    /**
     * Mencari informasi dasar (non-sensitif) tentang rekan kerja.
     * Implementasi mock bisa mencari dari file employees.csv.
     * @return String berisi info kontak atau jabatan.
     */
    String lookupColleagueInfo(String colleagueName);
}

/**
 * CONTOH IMPLEMENTASI (dapat digunakan kandidat sebagai referensi)
 */
class MockHRFunctions implements HRFunctions {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");

    @Override
    public String applyForLeave(String employeeName, String leaveType, LocalDate startDate, LocalDate endDate) {
        return String.format(
            "KONFIRMASI: Pengajuan cuti untuk %s (jenis: %s) dari tanggal %s hingga %s telah dicatat.",
            employeeName, leaveType, startDate.format(formatter), endDate.format(formatter)
        );
    }

    @Override
    public String schedulePerformanceReview(String employeeName, String reviewerName, LocalDate reviewDate) {
        return String.format(
            "KONFIRMASI: Sesi review performa untuk %s dengan %s telah dijadwalkan pada %s.",
            employeeName, reviewerName, reviewDate.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"))
        );
    }

    @Override
    public String checkLeaveRequestStatus(String employeeName) {
        // Logika Mock: Secara acak mengembalikan salah satu status untuk demonstrasi.
        // Dalam implementasi nyata, ini akan mencari di leave_requests.csv.
        String[] statuses = {"Disetujui", "Ditolak", "Menunggu Persetujuan"};
        int randomIndex = (int) (Math.random() * statuses.length);
        return String.format(
            "INFO: Status pengajuan cuti terakhir untuk %s adalah: %s.",
            employeeName, statuses[randomIndex]
        );
    }
    
    @Override
    public String submitExpenseReport(String employeeName, String category, double amount) {
        return String.format(
            "KONFIRMASI: Laporan pengeluaran untuk %s sebesar Rp%,.2f (kategori: %s) telah diajukan untuk diproses.",
            employeeName, amount, category
        );
    }

    @Override
    public String lookupColleagueInfo(String colleagueName) {
        // Logika Mock: Mengembalikan data dummy.
        // Dalam implementasi nyata, ini akan mencari di employees.csv.
        return String.format(
            "INFO: Informasi untuk %s - Jabatan: Software Engineer, Email: %s@examplecorp.com.",
            colleagueName, colleagueName.toLowerCase().split(" ")[0]
        );
    }
}