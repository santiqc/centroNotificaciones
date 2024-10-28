package com._tcapital.centronotificaciones.presentation;

import com._tcapital.centronotificaciones.application.Dto.ArchivoDto;
import com._tcapital.centronotificaciones.application.ProcesamientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/procesamiento")
@Tag(name = "Procesamiento", description = "API para el procesamiento de archivos")
public class ProcesamientoController {
    private final ProcesamientoService procesamientoService;

    public ProcesamientoController(ProcesamientoService procesamientoService) {
        this.procesamientoService = procesamientoService;
    }

    @GetMapping("/archivos")
    @Operation(
            summary = "Buscar archivos por ID de historial",
            description = "Devuelve una lista de archivos PDF encontrados para un ID de historial específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivos encontrados"),
            @ApiResponse(responseCode = "204", description = "No hay archivos para el ID especificado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<ArchivoDto>> buscarArchivosPorIdHistory(
            @Parameter(description = "ID del historial para buscar los archivos", required = true)
            @RequestParam Long idHistory) {
        List<ArchivoDto> archivos = procesamientoService.buscarArchivosPorIdHistory(idHistory);
        if (archivos.isEmpty()) {
            return ResponseEntity.noContent().build(); // Si no hay archivos, devuelve un 204
        }
        return ResponseEntity.ok(archivos);
    }


}
