package business.SSFuncionarios;

import java.time.LocalDateTime;

public class Registo {
    private String idRegisto;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSaida;
    private Mecanico mecanico;

    public Registo(String idRegisto, LocalDateTime horaEntrada, LocalDateTime horaSaida, Mecanico mecanico) {
        this.idRegisto = idRegisto;
        this.horaEntrada = horaEntrada;
        this.horaSaida = horaSaida;
        this.mecanico = mecanico;
    }

    public String getIdRegisto() {
        return idRegisto;
    }

    public void setIdRegisto(String idRegisto) {
        this.idRegisto = idRegisto;
    }

    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalDateTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public LocalDateTime getHoraSaida() {
        return horaSaida;
    }

    public void setHoraSaida(LocalDateTime horaSaida) {
        this.horaSaida = horaSaida;
    }

    public Mecanico getMecanico() {
        return mecanico;
    }

    public void setMecanico(Mecanico mecanico) {
        this.mecanico = mecanico;
    }
}
