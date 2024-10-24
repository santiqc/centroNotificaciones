package com._tcapital.centronotificaciones.application;


import com._tcapital.centronotificaciones.application.Dto.ArchivoDto;
import java.util.List;

public interface ProcesamientoService {
    List<ArchivoDto> buscarArchivosPorIdHistory(Long idHistory);
}
