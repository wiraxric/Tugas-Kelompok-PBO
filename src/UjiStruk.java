import java.time.LocalDateTime;

// Program uji: memasukkan jam masuk & keluar dari 8 struk asli,
// lalu membandingkan biaya hasil program dengan biaya di struk.
public class UjiStruk {
    public static void main(String[] args) {
        String[][] data = {
            {"16:35:38", "17:30:25", "3000"}, {"16:24:28", "17:54:41", "5000"},
            {"17:49:39", "18:21:31", "3000"}, {"17:29:52", "18:24:05", "3000"},
            {"17:27:16", "18:24:25", "3000"}, {"16:57:30", "18:49:47", "5000"},
            {"18:29:18", "19:04:14", "3000"}, {"15:59:19", "19:34:56", "5000"},
        };
        int cocok = 0;
        System.out.println("No  Masuk     Keluar    Durasi       Struk   Program  Hasil");
        for (int i = 0; i < data.length; i++) {
            TransaksiParkir t = new TransaksiParkir("U" + (i + 1), new Motor("UJI"),
                    new Gerbang("M01", "MASUK"), LocalDateTime.parse("2026-09-21T" + data[i][0]));
            t.keluar(new Gerbang("F01", "KELUAR"), LocalDateTime.parse("2026-09-21T" + data[i][1]));
            var d = t.hitungDurasi();
            double biaya = t.hitungBiaya();
            boolean ok = biaya == Double.parseDouble(data[i][2]);
            if (ok) cocok++;
            System.out.printf("%-3d %-9s %-9s %dj %02dm %02dd   %-7s %-8s %s%n", i + 1, data[i][0], data[i][1],
                    d.toHours(), d.toMinutesPart(), d.toSecondsPart(),
                    TransaksiParkir.rupiah(Double.parseDouble(data[i][2])), TransaksiParkir.rupiah(biaya), ok ? "COCOK" : "BEDA");
        }
        System.out.println("Cocok: " + cocok + " dari " + data.length + " struk");

        System.out.println();
        System.out.println("Parkir lebih dari 4 jam (tidak ada di struk, pakai asumsi +Rp2.000/jam):");
        Motor m = new Motor("UJI");
        long[] menit = {240, 241, 300, 301, 480};
        for (long x : menit) {
            System.out.printf("  %3d menit (%dj %02dm) -> Rp %s%n", x, x / 60, x % 60, TransaksiParkir.rupiah(m.hitungTarif(x)));
        }
    }
}
