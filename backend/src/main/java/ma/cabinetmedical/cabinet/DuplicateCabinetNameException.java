package ma.cabinetmedical.cabinet;

public class DuplicateCabinetNameException extends RuntimeException {
    public DuplicateCabinetNameException() {
        super("Un cabinet utilise déjà ce nom");
    }
}
