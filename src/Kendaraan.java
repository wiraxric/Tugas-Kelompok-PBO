public abstract class Kendaraan {
    private String platNomor;
    private String jenis;

    public Kendaraan(String platNomor, String jenis) {
        this.platNomor = platNomor;
        this.jenis = jenis;
    }
    public Kendaraan() { this("-", "-"); }

    public void setPlatNomor(String platNomor) { this.platNomor = platNomor; }
    public void setJenis(String jenis) { this.jenis = jenis; }
    public String getPlatNomor() { return platNomor; }
    public String getJenis() { return jenis; }

    // abstract: tiap jenis kendaraan punya aturan tarif sendiri
    public abstract double hitungTarif(long menit);
}
