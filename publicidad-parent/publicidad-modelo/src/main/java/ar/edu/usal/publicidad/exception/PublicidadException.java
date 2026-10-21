package ar.edu.usal.publicidad.exception;

public class PublicidadException extends Exception {

    // Constructor para un error con solo el mensaje
    public PublicidadException(String mensaje) {
        super(mensaje);
    }

    // Constructor para un error que tambien guarda la causa original (ej SQLException)
    public PublicidadException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}