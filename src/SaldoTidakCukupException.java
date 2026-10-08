public class SaldoTidakCukupException extends Exception {
    private static final long serialVersionUID = 1L;
    private double kekurangan;

    public SaldoTidakCukupException(String pesan, double kekurangan) {
        super(pesan);
        this.kekurangan = kekurangan;
    }
    public double getKekurangan() { return kekurangan; }
}
