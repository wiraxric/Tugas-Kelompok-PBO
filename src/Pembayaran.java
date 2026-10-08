public interface Pembayaran {
    void bayar(double jumlah) throws SaldoTidakCukupException;
    double cekSaldo();
    String getNamaMetode();
}
