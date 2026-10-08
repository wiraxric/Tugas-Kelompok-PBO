public class EMoney extends KartuElektronik {
    public EMoney(String nomorKartu, double saldo, String tid) { super(nomorKartu, saldo, tid); }
    public EMoney() { super(); }

    @Override
    public String getNamaMetode() { return "eMoney"; }
}
