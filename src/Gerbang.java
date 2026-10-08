public class Gerbang {
    private String kodeGerbang;
    private String jenis; // MASUK / KELUAR

    public Gerbang(String kodeGerbang, String jenis) {
        this.kodeGerbang = kodeGerbang;
        this.jenis = jenis;
    }
    public Gerbang() { this("-", "-"); }

    public void setKodeGerbang(String kodeGerbang) { this.kodeGerbang = kodeGerbang; }
    public void setJenis(String jenis) { this.jenis = jenis; }
    public String getKodeGerbang() { return kodeGerbang; }
    public String getJenis() { return jenis; }
}
