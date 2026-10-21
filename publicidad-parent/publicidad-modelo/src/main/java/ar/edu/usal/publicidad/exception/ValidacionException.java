package ar.edu.usal.publicidad.exception;

// Este error lo lanzo desde los SERVICE cuando un dato no cumple una regla, ejemplo DNI incorrecto o legajo vacio

// Es distinta de DAOException: aca no fallo la base, el dato esta mal
public class ValidacionException extends PublicidadException {

    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}