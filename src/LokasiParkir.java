import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class LokasiParkir {
    private String namaLokasi;
    private ArrayList<TransaksiParkir> daftarTransaksi;

    public LokasiParkir(String namaLokasi) {
        this.namaLokasi = namaLokasi;
        this.daftarTransaksi = new ArrayList<>();
    }

    public void tambahTransaksi(TransaksiParkir t) { daftarTransaksi.add(t); }

    public TransaksiParkir cariTransaksi(String id) {
        for (TransaksiParkir t : daftarTransaksi) {
            if (t.getIdTransaksi().equalsIgnoreCase(id)) return t;
        }
        return null;
    }

    public double getTotalPendapatan() {
        double total = 0;
        for (TransaksiParkir t : daftarTransaksi) {
            if (t.sudahBayar()) total += t.getBiaya();
        }
        return total;
    }

    // kendaraan dengan plat ini yang belum bayar (masih di dalam)
    public TransaksiParkir cariYangMasihParkir(String plat) {
        for (TransaksiParkir t : daftarTransaksi) {
            if (!t.sudahBayar() && t.getKendaraan().getPlatNomor().equalsIgnoreCase(plat)) return t;
        }
        return null;
    }

    // ID lanjut dari nomor terbesar, supaya tidak bentrok dengan data di file
    public String buatIdBaru() {
        int max = 0;
        for (TransaksiParkir t : daftarTransaksi) {
            max = Math.max(max, Integer.parseInt(t.getIdTransaksi().substring(1)));
        }
        return String.format("T%03d", max + 1);
    }

    public void simpanKeFile(String namaFile) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(namaFile))) {
            pw.println("id;jenis;plat;gerbangMasuk;waktuMasuk;gerbangKeluar;waktuKeluar;metode;noKartu;saldo;tid;biaya");
            for (TransaksiParkir t : daftarTransaksi) pw.println(t.toTxt());
        }
    }

    public void bacaDariFile(String namaFile) throws IOException {
        File f = new File(namaFile);
        if (!f.exists()) return; // pertama kali jalan: file belum ada
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            br.readLine(); // lewati header
            String baris;
            int noBaris = 1;
            while ((baris = br.readLine()) != null) {
                noBaris++;
                if (baris.isBlank()) continue;
                // baris yang rusak/anomali tidak dimasukkan ke sistem, cukup dilewati
                try {
                    daftarTransaksi.add(parseBaris(baris));
                } catch (RuntimeException e) {
                    System.out.println("Baris " + noBaris + " di " + namaFile + " dilewati (data tidak valid): " + e.getMessage());
                }
            }
        }
    }

    private TransaksiParkir parseBaris(String baris) {
        String[] d = baris.split(";", -1);
        if (d.length != 12) throw new IllegalArgumentException("jumlah kolom harus 12");
        if (!d[0].matches("T\\d{3,}")) throw new IllegalArgumentException("ID " + d[0]);
        if (cariTransaksi(d[0]) != null) throw new IllegalArgumentException("ID " + d[0] + " dobel");

        Kendaraan k;
        if (d[1].equals("MOTOR")) k = new Motor(d[2]);
        else if (d[1].equals("MOBIL")) k = new Mobil(d[2]);
        else throw new IllegalArgumentException("jenis kendaraan " + d[1]);
        if (d[2].isBlank()) throw new IllegalArgumentException("plat kosong");

        TransaksiParkir t = new TransaksiParkir(d[0], k, new Gerbang(d[3], "MASUK"), LocalDateTime.parse(d[4]));
        if (!d[6].equals("-")) t.keluar(new Gerbang(d[5], "KELUAR"), LocalDateTime.parse(d[6]));
        if (!d[7].equals("-")) {
            if (d[6].equals("-")) throw new IllegalArgumentException("sudah bayar tapi belum keluar");
            if (!d[8].matches("\\d{16}")) throw new IllegalArgumentException("nomor kartu " + d[8]);
            double saldo = Long.parseLong(d[9]);
            if (saldo < 0) throw new IllegalArgumentException("saldo negatif");
            Pembayaran p;
            if (d[7].equals("Flazz")) p = new Flazz(d[8], saldo, d[10]);
            else if (d[7].equals("eMoney")) p = new EMoney(d[8], saldo, d[10]);
            else throw new IllegalArgumentException("metode bayar " + d[7]);
            t.setPembayaran(p);
        }
        double biaya = Long.parseLong(d[11]);
        if (biaya < 0) throw new IllegalArgumentException("biaya negatif");
        t.setBiaya(biaya);
        return t;
    }

    public String getNamaLokasi() { return namaLokasi; }
    public ArrayList<TransaksiParkir> getDaftarTransaksi() { return daftarTransaksi; }
}
