public class Motor extends Kendaraan {
    private double tarifJamPertama = 3000;      // dari struk: < 1 jam = Rp3.000
    private double tarifSampai4Jam = 5000;      // dari struk: 1 - 4 jam = Rp5.000
    private double tarifPerJamTambahan = 2000;  // ASUMSI kelompok: > 4 jam, +Rp2.000 per jam

    public Motor(String platNomor) { super(platNomor, "MOTOR"); }
    public Motor() { this("-"); }

    public double getTarifJamPertama() { return tarifJamPertama; }
    public double getTarifSampai4Jam() { return tarifSampai4Jam; }
    public double getTarifPerJamTambahan() { return tarifPerJamTambahan; }

    @Override
    public double hitungTarif(long menit) {
        if (menit < 60) return tarifJamPertama;
        if (menit <= 240) return tarifSampai4Jam;
        long jamLebih = (long) Math.ceil((menit - 240) / 60.0); // jam setelah 4 jam, dibulatkan ke atas
        return tarifSampai4Jam + jamLebih * tarifPerJamTambahan;
    }
}
