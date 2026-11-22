package ma.cabinetmedical.cabinet;

public class CabinetNotFoundException extends RuntimeException {
    public CabinetNotFoundException() {
        super("Cabinet introuvable");
    }
}
