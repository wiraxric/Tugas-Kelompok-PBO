import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class App {
    static final String FILE_DATA = "transaksi.txt";
    static final DateTimeFormatter FMT_INPUT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        LokasiParkir lokasi = new LokasiParkir("CREATIVE BOX BINTARO");
        Gerbang gerbangMasuk = new Gerbang("M01", "MASUK");
        Gerbang gerbangKeluar = new Gerbang("F01", "KELUAR");

        try {
            lokasi.bacaDariFile(FILE_DATA);
            System.out.println("Data dimuat dari " + FILE_DATA + " : " + lokasi.getDaftarTransaksi().size() + " transaksi");
        } catch (IOException e) {
            System.out.println("Gagal membaca file: " + e.getMessage());
        }

        int pilih = -1;
        while (pilih != 0) {
            System.out.println();
            System.out.println("===== PARKIR " + lokasi.getNamaLokasi() + " =====");
            System.out.println("1. Kendaraan masuk");
            System.out.println("2. Kendaraan keluar & bayar");
            System.out.println("3. Lihat semua transaksi");
            System.out.println("4. Cetak ulang struk");
            System.out.println("5. Total pendapatan");
            System.out.println("0. Simpan & keluar");
            System.out.print("Pilih menu: ");
            try {
                pilih = Integer.parseInt(in.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Input harus angka!");
                continue;
            }
            switch (pilih) {
                case 1: menuMasuk(lokasi, gerbangMasuk); break;
                case 2: menuKeluar(lokasi, gerbangKeluar); break;
                case 3: menuLihat(lokasi); break;
                case 4: menuCetak(lokasi); break;
                case 5: System.out.println("Total pendapatan: Rp " + TransaksiParkir.rupiah(lokasi.getTotalPendapatan())); break;
                case 0:
                    try {
                        lokasi.simpanKeFile(FILE_DATA);
                        System.out.println("Data disimpan ke " + FILE_DATA + ". Sampai jumpa!");
                    } catch (IOException e) {
                        System.out.println("Gagal menyimpan: " + e.getMessage());
                    }
                    break;
                default: System.out.println("Menu tidak tersedia! Pilih 0-5.");
            }
        }
        in.close();
    }

    // plat nomor Indonesia: 1-2 huruf, 1-4 angka, 0-3 huruf (contoh: B 1234 XYZ)
    // spasi dirapikan otomatis, jadi "b1234xyz" juga diterima menjadi "B 1234 XYZ"
    static String rapikanPlat(String input) {
        String s = input.trim().toUpperCase().replaceAll("\\s+", "");
        if (!s.matches("[A-Z]{1,2}\\d{1,4}[A-Z]{0,3}")) return null;
        return s.replaceAll("^([A-Z]{1,2})(\\d{1,4})([A-Z]{0,3})$", "$1 $2 $3").trim();
    }

    static LocalDateTime sekarang() {
        return LocalDateTime.now().withNano(0); // waktu otomatis dari jam komputer
    }

    static void menuMasuk(LokasiParkir lokasi, Gerbang masuk) {
        String j = "";
        while (!j.equals("1") && !j.equals("2")) {
            System.out.print("Jenis kendaraan (1=Motor, 2=Mobil): ");
            j = in.nextLine().trim();
            if (!j.equals("1") && !j.equals("2")) System.out.println("Pilih 1 atau 2!");
        }
        String plat = null;
        while (plat == null) {
            System.out.print("Plat nomor: ");
            String input = in.nextLine();
            if (input.isBlank()) {
                System.out.println("Plat nomor tidak boleh kosong!");
                continue;
            }
            plat = rapikanPlat(input);
            if (plat == null) System.out.println("Format plat tidak valid! Contoh: B 1234 XYZ");
        }
        // kendaraan yang masih di dalam tidak boleh masuk lagi
        TransaksiParkir aktif = lokasi.cariYangMasihParkir(plat);
        if (aktif != null) {
            System.out.println("Kendaraan " + plat + " masih parkir (ID " + aktif.getIdTransaksi() + "), tiket tidak dibuat.");
            return;
        }
        Kendaraan k = j.equals("2") ? new Mobil(plat) : new Motor(plat);
        LocalDateTime w = sekarang();
        String id = lokasi.buatIdBaru();
        lokasi.tambahTransaksi(new TransaksiParkir(id, k, masuk, w));
        System.out.println("Tiket dibuat -> ID: " + id + " | " + k.getJenis() + " " + plat + " | gerbang " + masuk.getKodeGerbang());
        System.out.println("Waktu masuk : " + w.format(FMT_INPUT));
    }

    static void menuKeluar(LokasiParkir lokasi, Gerbang keluar) {
        if (lokasi.getDaftarTransaksi().isEmpty()) { System.out.println("Belum ada transaksi."); return; }
        System.out.print("ID transaksi: ");
        TransaksiParkir t = lokasi.cariTransaksi(in.nextLine().trim());
        if (t == null) { System.out.println("ID tidak ditemukan!"); return; }
        if (t.sudahBayar()) { System.out.println("Transaksi ini sudah dibayar."); return; }
        try {
            t.keluar(keluar, sekarang());
            System.out.println("Waktu keluar: " + t.getWaktuKeluar().format(FMT_INPUT));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage()); return;
        }
        var d = t.hitungDurasi();
        System.out.println("Durasi : " + d.toHours() + " jam " + d.toMinutesPart() + " menit " + d.toSecondsPart() + " detik");
        System.out.println("Biaya  : Rp " + TransaksiParkir.rupiah(t.hitungBiaya()));

        String m = "";
        while (!m.equals("1") && !m.equals("2")) {
            System.out.print("Metode bayar (1=Flazz, 2=eMoney): ");
            m = in.nextLine().trim();
            if (!m.equals("1") && !m.equals("2")) System.out.println("Pilih 1 atau 2!");
        }
        String no = "";
        while (!no.matches("\\d{16}")) {
            System.out.print("Nomor kartu (16 digit): ");
            no = in.nextLine().trim().replace(" ", "");
            if (!no.matches("\\d{16}")) System.out.println("Nomor kartu harus 16 digit angka!");
        }
        double saldo = -1;
        while (saldo < 0) {
            System.out.print("Saldo kartu: ");
            String s = in.nextLine().trim();
            try {
                saldo = Long.parseLong(s); // rupiah: angka bulat, tanpa titik/koma
                if (saldo < 0) System.out.println("Saldo tidak boleh negatif!");
            } catch (NumberFormatException e) {
                System.out.println("Saldo harus angka bulat! Contoh: 81000");
            }
        }
        // TID = kode mesin pembaca kartu (diambil dari struk asli)
        Pembayaran p = m.equals("2") ? new EMoney(no, saldo, "46110200") : new Flazz(no, saldo, "ESP07186");
        try {
            t.prosesBayar(p);
            System.out.println("Pembayaran berhasil!\n");
            System.out.println(t.cetakStruk());
        } catch (SaldoTidakCukupException e) {
            System.out.println(e.getMessage() + " Kurang Rp " + TransaksiParkir.rupiah(e.getKekurangan()));
            System.out.println("Transaksi belum lunas, silakan ulangi menu 2 dengan kartu lain.");
        }
    }

    static void menuLihat(LokasiParkir lokasi) {
        if (lokasi.getDaftarTransaksi().isEmpty()) { System.out.println("Belum ada transaksi."); return; }
        System.out.printf("%-5s %-6s %-10s %-20s %-8s %s%n", "ID", "Jenis", "Plat", "Masuk", "Biaya", "Status");
        for (TransaksiParkir t : lokasi.getDaftarTransaksi()) {
            String status = t.sudahBayar() ? "LUNAS (" + t.getPembayaran().getNamaMetode() + ")" : "MASIH PARKIR";
            String biaya = t.sudahBayar() ? TransaksiParkir.rupiah(t.getBiaya()) : "-";
            System.out.printf("%-5s %-6s %-10s %-20s %-8s %s%n", t.getIdTransaksi(), t.getKendaraan().getJenis(),
                    t.getKendaraan().getPlatNomor(), t.getWaktuMasuk().format(FMT_INPUT), biaya, status);
        }
    }

    static void menuCetak(LokasiParkir lokasi) {
        System.out.print("ID transaksi: ");
        TransaksiParkir t = lokasi.cariTransaksi(in.nextLine().trim());
        if (t == null) System.out.println("ID tidak ditemukan!");
        else if (!t.sudahBayar()) System.out.println("Belum bayar, struk belum bisa dicetak.");
        else System.out.println(t.cetakStruk());
    }
}
