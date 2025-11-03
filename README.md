# 🤖 HR Chat Agent – Intelligent HR Assistant (Java)

## 🧠 Deskripsi Proyek
**HR Chat Agent** adalah aplikasi berbasis Java yang berfungsi sebagai **asisten virtual HR (Human Resources)**.  
Agen ini mampu memahami perintah manusia dalam bahasa Indonesia, kemudian mengeksekusi berbagai fungsi HR seperti:
- Mengajukan cuti (`apply_cuti`)
- Menjadwalkan review performa (`jadwalkan_review`)
- Mengecek status cuti (`cek_status_cuti`)
- Mencari informasi rekan kerja (`cari_rekan`)
- Mengirim laporan pengeluaran/expense (dicatat di `leave_balances.csv`)

Aplikasi ini memanfaatkan **Gemini API (Google Generative Language API)** untuk menganalisis input pengguna dan mengubahnya menjadi perintah terstruktur berbentuk JSON.

---

## 📂 Struktur Proyek
HRChatAgent/
│
├── src/
│ └── main/
│ └── java/
│ └── com/
│ └── linovhr/
│ ├── HRChatAgent.java → Agen utama untuk menangani perintah
│ ├── HRFunctions.java → Berisi fungsi HR (apply cuti, cek status, dst)
│ ├── GeminiService.java → Modul koneksi & prompting ke Gemini API
│ ├── Utils/CSVUtils.java → Helper baca & tulis file CSV
│ └── Main.java → Entry point untuk menjalankan program
│
├── data/
│ ├── employees.csv → Data karyawan
│ ├── leave_balances.csv → Data saldo cuti & pengajuan
│
├── pom.xml → Konfigurasi Maven
└── README.md



---

## ⚙️ Fitur Utama
| Fitur | Deskripsi | Contoh Input |
|-------|------------|--------------|
| 🏖️ Apply Cuti | Mengajukan cuti berdasarkan nama & tanggal | `apply cuti tahunan buat budi dari 3 okt sampai 5 okt` |
| 🧾 Cek Status Cuti | Mengecek saldo cuti karyawan | `cek status cuti rina` |
| 📅 Jadwalkan Review | Menjadwalkan review performa antara karyawan & reviewer | `jadwalkan review performa untuk budi dengan pak arif minggu depan` |
| 👥 Cari Rekan | Menampilkan data rekan kerja dari `employees.csv` | `siapa manajer rina` |
| 💰 Submit Expense | Menyimpan laporan pengeluaran ke `leave_balances.csv` | `laporkan pengeluaran operational untuk budi sebesar 1 juta` |

---

## 🧩 Teknologi yang Digunakan
- **Java 17+**
- **Maven** untuk dependency management
- **Gemini API (Google AI)** untuk NLP dan pemahaman perintah
- **org.json** untuk parsing JSON
- **File I/O CSV** untuk penyimpanan data sederhana

---

## 🔑 Konfigurasi API Key Gemini
Buat akun di [Google AI Studio](https://aistudio.google.com/) lalu buat API key.  

Atau bisa langsung hardcode di konstruktor:
```java
String apiKey = "GEMINI"; // ganti dengan API key kamu
```
## 🧭 Cara Menjalankan Aplikasi
1️⃣ Clone repository
```bash
git clone https://github.com/Yohannes-Alexander/hr-agent.git
cd hr-chat-agent
```
2️⃣ Compile dan Jalankan Aplikasi
```bash
mvn clean compile exec:java
```
3️⃣ Contoh interaksi
```bash
> tolong apply cuti tahunan buat budi dari tgl 3 okt sampe 5 okt
✅ KONFIRMASI: Budi telah mengajukan cuti tahunan dari 2025-10-03 hingga 2025-10-05.
```

## 🧠 Arsitektur Sederhana
```bash
User Input
   ↓
GeminiService.analyzeCommand() → panggil Gemini API untuk ubah ke JSON
   ↓
HRChatAgent.handleUserInput()
   ↓
HRFunctions → jalankan aksi (apply cuti / cek status / submit expense)
   ↓
CSVUtils → baca/tulis data ke file .csv
   ↓
Balasan ke user
```
