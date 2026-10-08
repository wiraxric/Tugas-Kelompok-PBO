import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TransaksiParkir {
    private String idTransaksi;
    private Kendaraan kendaraan;
    private Gerbang gerbangMasuk;
    private Gerbang gerbangKeluar;
    private LocalDateTime waktuMasuk;
    private LocalDateTime waktuKeluar;
    private Pembayaran pembayaran;
    private double biaya;

    private static final DateTimeFormatter FMT_STRUK =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss", Locale.ENGLISH);

    public TransaksiParkir(String id, Kendaraan k, Gerbang masuk, LocalDateTime waktuMasuk) {
        this.idTransaksi = id;
        this.kendaraan = k;
        this.gerbangMasuk = masuk;
        this.waktuMasuk = waktuMasuk;
    }
    public TransaksiParkir() { }

    public void keluar(Gerbang keluar, LocalDateTime waktuKeluar) {
        if (waktuKeluar.isBefore(waktuMasuk)) {
            throw new IllegalArgumentException("Waktu keluar tidak boleh sebelum waktu masuk!");
        }
        this.gerbangKeluar = keluar;
        this.waktuKeluar = waktuKeluar;
    }

    public Duration hitungDurasi() {
        return Duration.between(waktuMasuk, waktuKeluar);
    }

    public double hitungBiaya() {
        biaya = kendaraan.hitungTarif(hitungDurasi().toMinutes()); // polymorphism
        return biaya;
    }

    public void prosesBayar(Pembayaran p) throws SaldoTidakCukupException {
        p.bayar(biaya);          // kalau saldo kurang -> exception, pembayaran tidak dicatat
        this.pembayaran = p;
    }

    public boolean sudahBayar() { return pembayaran != null; }

    public static String rupiah(double x) {
        return String.format(Locale.forLanguageTag("id-ID"), "%,.0f", x);
    }

    private static String samarkan(String no) {
        if (no.length() <= 8) return no;
        return no.substring(0, 4) + "*".repeat(no.length() - 8) + no.substring(no.length() - 4);
    }

    public String cetakStruk() {
        Duration d = hitungDurasi();
        KartuElektronik kartu = (KartuElektronik) pembayaran;
        String m = kartu.getNamaMetode();
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("           CREATIVE BOX BINTARO\n");
        sb.append("==========================================\n");
        sb.append(kendaraan.getJenis()).append("\n");
        sb.append(kendaraan.getPlatNomor()).append("\n");
        sb.append("In       : ").append(waktuMasuk.format(FMT_STRUK)).append(" - ").append(gerbangMasuk.getKodeGerbang()).append("\n");
        sb.append("         : ").append(waktuKeluar.format(FMT_STRUK)).append(" - ").append(gerbangKeluar.getKodeGerbang()).append("\n");
        sb.append("Duration : ").append(d.toHours()).append(" hours ").append(d.toMinutesPart())
          .append(" minutes ").append(d.toSecondsPart()).append(" seconds\n\n");
        sb.append("Biaya Parkir : Rp ").append(rupiah(biaya)).append("\n\n");
        sb.append(m).append(" Balance : Rp ").append(rupiah(kartu.cekSaldo())).append("\n");
        sb.append(m).append(" CardNo  : ").append(samarkan(kartu.getNomorKartu())).append("\n");
        sb.append(m).append(" TID     : ").append(kartu.getTid()).append("\n\n");
        sb.append("              Tarif Parkir\n");
        sb.append("          Sudah Termasuk Pajak\n");
        sb.append("==========================================");
        return sb.toString();
    }

    // satu baris teks (dipisah ;) untuk disimpan ke transaksi.txt
    public String toTxt() {
        String gK = gerbangKeluar == null ? "-" : gerbangKeluar.getKodeGerbang();
        String wK = waktuKeluar == null ? "-" : waktuKeluar.toString();
        String metode = "-", no = "-", saldo = "-", tid = "-";
        if (pembayaran != null) {
            KartuElektronik k = (KartuElektronik) pembayaran;
            metode = k.getNamaMetode(); no = k.getNomorKartu();
            saldo = String.valueOf((long) k.cekSaldo()); tid = k.getTid();
        }
        return String.join(";", idTransaksi, kendaraan.getJenis(), kendaraan.getPlatNomor(),
                gerbangMasuk.getKodeGerbang(), waktuMasuk.toString(), gK, wK,
                metode, no, saldo, tid, String.valueOf((long) biaya));
    }

    // setter untuk memulihkan data dari file
    public void setPembayaran(Pembayaran p) { this.pembayaran = p; }
    public void setBiaya(double b) { this.biaya = b; }

    public String getIdTransaksi() { return idTransaksi; }
    public Kendaraan getKendaraan() { return kendaraan; }
    public LocalDateTime getWaktuMasuk() { return waktuMasuk; }
    public LocalDateTime getWaktuKeluar() { return waktuKeluar; }
    public Pembayaran getPembayaran() { return pembayaran; }
    public double getBiaya() { return biaya; }
}
