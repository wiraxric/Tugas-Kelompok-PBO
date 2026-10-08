public class Flazz extends KartuElektronik {
    public Flazz(String nomorKartu, double saldo, String tid) { super(nomorKartu, saldo, tid); }
    public Flazz() { super(); }

    @Override
    public String getNamaMetode() { return "Flazz"; }
}
