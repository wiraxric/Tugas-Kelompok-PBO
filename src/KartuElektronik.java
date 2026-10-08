public abstract class KartuElektronik implements Pembayaran {
    private String nomorKartu;
    private double saldo;
    private String tid;

    public KartuElektronik(String nomorKartu, double saldo, String tid) {
        this.nomorKartu = nomorKartu;
        this.saldo = saldo;
        this.tid = tid;
    }
    public KartuElektronik() { this("-", 0, "-"); }

    public void setNomorKartu(String nomorKartu) { this.nomorKartu = nomorKartu; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
    public void setTid(String tid) { this.tid = tid; }
    public String getNomorKartu() { return nomorKartu; }
    public String getTid() { return tid; }

    @Override
    public void bayar(double jumlah) throws SaldoTidakCukupException {
        if (saldo < jumlah) {
            throw new SaldoTidakCukupException("Saldo tidak cukup!", jumlah - saldo);
        }
        saldo -= jumlah;
    }

    @Override
    public double cekSaldo() { return saldo; }

    @Override
    public abstract String getNamaMetode();
}
