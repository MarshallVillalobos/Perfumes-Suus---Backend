package cl.suus.ms_usuarios.service;

import cl.suus.ms_usuarios.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class RunValidator {

    public String normalizarYValidar(String runRaw) {
        if (runRaw == null || runRaw.isBlank()) {
            throw new BadRequestException("El RUN es obligatorio");
        }

        String limpio = runRaw.replace(".", "").replace("-", "").trim().toUpperCase();
        if (limpio.length() < 7 || limpio.length() > 9 || !limpio.matches("^[0-9]+[0-9K]$")) {
            throw new BadRequestException("El RUN debe tener entre 7 y 9 caracteres válidos sin puntos ni guion");
        }

        String cuerpo = limpio.substring(0, limpio.length() - 1);
        char dvIngresado = limpio.charAt(limpio.length() - 1);
        char dvCalculado = calcularDv(cuerpo);

        if (dvIngresado != dvCalculado) {
            throw new BadRequestException("El RUN ingresado no tiene un dígito verificador válido");
        }

        return limpio;
    }

    private char calcularDv(String cuerpo) {
        int suma = 0;
        int multiplicador = 2;

        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(cuerpo.charAt(i)) * multiplicador;
            multiplicador = (multiplicador == 7) ? 2 : multiplicador + 1;
        }

        int resto = 11 - (suma % 11);
        if (resto == 11) return '0';
        if (resto == 10) return 'K';
        return (char) ('0' + resto);
    }
}