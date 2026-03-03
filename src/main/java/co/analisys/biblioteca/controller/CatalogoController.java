package co.analisys.biblioteca.controller;

import co.analisys.biblioteca.model.Libro;
import co.analisys.biblioteca.model.LibroId;
import co.analisys.biblioteca.service.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/libros")
@Tag(name = "Catálogo", description = "API para la gestión del catálogo de libros")
public class CatalogoController {
    private final CatalogoService catalogoService;

    @Autowired
    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @Operation(summary = "Obtener libro por ID", description = "Retorna la información completa de un libro dado su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro encontrado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT requerido"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Rol insuficiente"),
            @ApiResponse(responseCode = "500", description = "Libro no encontrado")
    })
    @GetMapping("/{id}")
    public Libro obtenerLibro(
            @Parameter(description = "ID del libro a consultar", required = true, example = "1") @PathVariable String id) {
        return catalogoService.obtenerLibro(new LibroId(id));
    }

    @Operation(summary = "Verificar disponibilidad de libro", description = "Retorna true si el libro está disponible para préstamo, false en caso contrario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta de disponibilidad exitosa"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT requerido"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Rol insuficiente"),
            @ApiResponse(responseCode = "500", description = "Libro no encontrado")
    })
    @GetMapping("/{id}/disponible")
    public boolean isLibroDisponible(
            @Parameter(description = "ID del libro a verificar", required = true, example = "1") @PathVariable String id) {
        Libro libro = catalogoService.obtenerLibro(new LibroId(id));
        return libro != null && libro.isDisponible();
    }

    @Operation(summary = "Actualizar disponibilidad de libro", description = "Cambia el estado de disponibilidad de un libro. Requiere rol ADMIN o BIBLIOTECARIO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT requerido"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Se requiere rol ADMIN o BIBLIOTECARIO"),
            @ApiResponse(responseCode = "500", description = "Libro no encontrado")
    })
    @PutMapping("/{id}/disponibilidad")
    public void actualizarDisponibilidad(
            @Parameter(description = "ID del libro a actualizar", required = true, example = "1") @PathVariable String id,
            @Parameter(description = "Nuevo estado de disponibilidad (true = disponible, false = no disponible)", required = true) @RequestBody boolean disponible) {
        catalogoService.actualizarDisponibilidad(new LibroId(id), disponible);
    }

    @Operation(summary = "Buscar libros por criterio", description = "Busca libros en el catálogo por título")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT requerido"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Rol insuficiente")
    })
    @GetMapping("/buscar")
    public List<Libro> buscarLibros(
            @Parameter(description = "Criterio de búsqueda (título del libro)", required = true, example = "Cien años de soledad") @RequestParam String criterio) {
        return catalogoService.buscarLibros(criterio);
    }
}
