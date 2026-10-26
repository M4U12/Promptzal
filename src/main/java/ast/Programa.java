package ast;

import java.util.List;

public class Programa {

    private List<Directiva> directivas;
    private List<Agente> agentes;
    private List<String> ejecuciones; // Nombres de los agentes a ejecutar
    private List<String> exportaciones; // Nombres de los agentes a exportar

    public Programa(List<Directiva> directivas, List<Agente> agentes, List<String> ejecuciones, List<String> exportaciones) {
        this.directivas = directivas;
        this.agentes = agentes;
        this.ejecuciones = ejecuciones;
        this.exportaciones = exportaciones;
    }

    public List<Directiva> getDirectivas() {
        return directivas;
    }

    public List<Agente> getAgentes() {
        return agentes;
    }

    public List<String> getEjecuciones() {
        return ejecuciones;
    }

    public List<String> getExportaciones() {
        return exportaciones;
    }
}
