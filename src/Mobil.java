public class Mobil extends Kendaraan {
    // ASUMSI: belum ada struk mobil, angka ini contoh saja
    private double tarifJamPertama = 5000;
    private double tarifPerJam = 4000;

    public Mobil(String platNomor) { super(platNomor, "MOBIL"); }
    public Mobil() { this("-"); }

    public double getTarifJamPertama() { return tarifJamPertama; }
    public double getTarifPerJam() { return tarifPerJam; }

    @Override
    public double hitungTarif(long menit) {
        long jam = (long) Math.ceil(menit / 60.0);
        if (jam <= 1) return tarifJamPertama;
        return tarifJamPertama + (jam - 1) * tarifPerJam;
    }
}
