package com.printlabel.service;

import com.printlabel.dto.OrdenDto;
import com.printlabel.exception.GlobalExceptionHandler.*;
import com.printlabel.model.*;
import com.printlabel.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final OrdenEstiloRepository ordenEstiloRepository;
    private final EstiloTallaRepository estiloTallaRepository;
    private final FabricaRepository fabricaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProgramaRepository programaRepository;
    private final TallaRepository tallaRepository;

    public OrdenService(OrdenRepository ordenRepository,
                        OrdenEstiloRepository ordenEstiloRepository,
                        EstiloTallaRepository estiloTallaRepository,
                        FabricaRepository fabricaRepository,
                        UsuarioRepository usuarioRepository,
                        ProgramaRepository programaRepository,
                        TallaRepository tallaRepository) {
        this.ordenRepository = ordenRepository;
        this.ordenEstiloRepository = ordenEstiloRepository;
        this.estiloTallaRepository = estiloTallaRepository;
        this.fabricaRepository = fabricaRepository;
        this.usuarioRepository = usuarioRepository;
        this.programaRepository = programaRepository;
        this.tallaRepository = tallaRepository;
    }

    // ---- CRUD ÓRDENES ----

    public List<OrdenDto.ListResponse> findAll(Integer idFabrica, String estatus) {
        Orden.Estatus estatusEnum = null;
        if (estatus != null) {
            try { estatusEnum = Orden.Estatus.valueOf(estatus); }
            catch (IllegalArgumentException e) { throw new BadRequestException("Estatus inválido: " + estatus); }
        }
        return ordenRepository.findWithFilters(idFabrica, estatusEnum)
                .stream().map(OrdenDto.ListResponse::new).collect(Collectors.toList());
    }

    public OrdenDto.Response findById(Integer id) {
        Orden orden = ordenRepository.findByIdWithEstilosAndTallas(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));
        return new OrdenDto.Response(orden);
    }

    public OrdenDto.Response create(OrdenDto.CreateRequest request) {
        Fabrica fabrica = fabricaRepository.findByIdFabricaAndActivoTrue(request.getIdFabrica())
                .orElseThrow(() -> new ResourceNotFoundException("Fábrica no encontrada: " + request.getIdFabrica()));

        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String folio = generarFolio();

        Orden orden = new Orden();
        orden.setFolio(folio);
        orden.setFabrica(fabrica);
        orden.setUsuario(usuario);
        orden.setFechaProgramacion(request.getFechaProgramacion());
        orden.setObservaciones(request.getObservaciones());
        orden.setEstatus(Orden.Estatus.borrador);

        return new OrdenDto.Response(ordenRepository.save(orden));
    }

    public OrdenDto.Response update(Integer id, OrdenDto.UpdateRequest request) {
        Orden orden = getOrdenOrThrow(id);
        if (orden.getEstatus() == Orden.Estatus.cerrada) {
            throw new BadRequestException("No se puede editar una orden cerrada");
        }

        Fabrica fabrica = fabricaRepository.findByIdFabricaAndActivoTrue(request.getIdFabrica())
                .orElseThrow(() -> new ResourceNotFoundException("Fábrica no encontrada: " + request.getIdFabrica()));

        orden.setFabrica(fabrica);
        orden.setFechaProgramacion(request.getFechaProgramacion());
        orden.setObservaciones(request.getObservaciones());

        return new OrdenDto.Response(ordenRepository.save(orden));
    }

    public void delete(Integer id) {
        Orden orden = getOrdenOrThrow(id);
        if (orden.getEstatus() != Orden.Estatus.borrador) {
            throw new BadRequestException("Solo se pueden eliminar órdenes en estado borrador");
        }
        ordenRepository.delete(orden);
    }

    public OrdenDto.Response cerrarOrden(Integer id) {
        Orden orden = getOrdenOrThrow(id);
        if (orden.getEstatus() != Orden.Estatus.borrador) {
            throw new BadRequestException("Solo se pueden cerrar órdenes en estado borrador");
        }
        orden.setEstatus(Orden.Estatus.cerrada);
        return new OrdenDto.Response(ordenRepository.save(orden));
    }

    // ---- CSV ----

    public String generarCsv(Integer id) {
        Orden orden = getOrdenOrThrow(id);

        List<EstiloTalla> filas = estiloTallaRepository.findForCsvByOrdenId(id);

        if (filas.isEmpty()) {
            throw new BadRequestException("La orden no tiene datos para generar el CSV");
        }

        StringBuilder csv = new StringBuilder("PROGRAMA,TALLA,CM\n");

        for (EstiloTalla et : filas) {
            String programa = et.getOrdenEstilo().getPrograma().getClave();
            String talla = et.getTalla().getNumeroTalla().toPlainString();
            String cm = et.getTalla().getCentimetros().toPlainString();

            // Por cada par → una fila
            for (int i = 0; i < et.getCantidadPares(); i++) {
                csv.append(programa).append(",")
                   .append(talla).append(",")
                   .append(cm).append("\n");
            }
        }

        // Marcar como exportada
        if (orden.getEstatus() == Orden.Estatus.cerrada) {
            orden.setEstatus(Orden.Estatus.exportada);
            ordenRepository.save(orden);
        }

        return csv.toString();
    }

    // ---- ESTILOS ----

    public List<OrdenDto.EstiloResponse> findEstilos(Integer idOrden) {
        getOrdenOrThrow(idOrden);
        return ordenEstiloRepository.findByOrdenIdWithDetails(idOrden)
                .stream().map(OrdenDto.EstiloResponse::new).collect(Collectors.toList());
    }

    public OrdenDto.EstiloResponse agregarEstilo(Integer idOrden, OrdenDto.AgregarEstiloRequest request) {
        Orden orden = getOrdenOrThrow(idOrden);
        if (orden.getEstatus() != Orden.Estatus.borrador) {
            throw new BadRequestException("No se pueden agregar estilos a una orden que no está en borrador");
        }

        Programa programa = programaRepository.findByIdProgramaAndActivoTrue(request.getIdPrograma())
                .orElseThrow(() -> new ResourceNotFoundException("Programa no encontrado: " + request.getIdPrograma()));

        Integer maxFila = ordenEstiloRepository.findMaxOrdenFila(idOrden);
        int nuevaFila = (maxFila == null ? 0 : maxFila) + 1;

        OrdenEstilo estilo = new OrdenEstilo();
        estilo.setOrden(orden);
        estilo.setPrograma(programa);
        estilo.setOrdenFila(request.getOrdenFila() != null ? request.getOrdenFila() : nuevaFila);

        return new OrdenDto.EstiloResponse(ordenEstiloRepository.save(estilo));
    }

    public OrdenDto.EstiloResponse updateEstilo(Integer idOrden, Integer idEstilo, OrdenDto.UpdateEstiloRequest request) {
        OrdenEstilo estilo = ordenEstiloRepository.findByIdEstiloAndOrden_IdOrden(idEstilo, idOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Estilo no encontrado"));
        estilo.setOrdenFila(request.getOrdenFila());
        return new OrdenDto.EstiloResponse(ordenEstiloRepository.save(estilo));
    }

    public void deleteEstilo(Integer idOrden, Integer idEstilo) {
        OrdenEstilo estilo = ordenEstiloRepository.findByIdEstiloAndOrden_IdOrden(idEstilo, idOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Estilo no encontrado"));
        ordenEstiloRepository.delete(estilo);
    }

    // ---- TALLAS POR ESTILO (batch upsert) ----

    public List<OrdenDto.EstiloTallaResponse> upsertTallas(Integer idOrden, Integer idEstilo,
                                                            OrdenDto.UpsertTallasRequest request) {
        OrdenEstilo estilo = ordenEstiloRepository.findByIdEstiloAndOrden_IdOrden(idEstilo, idOrden)
                .orElseThrow(() -> new ResourceNotFoundException("Estilo no encontrado"));

        for (OrdenDto.TallaItem item : request.getTallas()) {
            Talla talla = tallaRepository.findByIdTallaAndActivoTrue(item.getIdTalla())
                    .orElseThrow(() -> new ResourceNotFoundException("Talla no encontrada: " + item.getIdTalla()));

            Optional<EstiloTalla> existing = estiloTallaRepository
                    .findByOrdenEstilo_IdEstiloAndTalla_IdTalla(idEstilo, item.getIdTalla());

            if (item.getCantidadPares() == 0 && existing.isPresent()) {
                // Si es 0 y ya existe → eliminar (no persistir ceros)
                estiloTallaRepository.delete(existing.get());
            } else if (item.getCantidadPares() > 0) {
                EstiloTalla et = existing.orElse(new EstiloTalla());
                et.setOrdenEstilo(estilo);
                et.setTalla(talla);
                et.setCantidadPares(item.getCantidadPares());
                estiloTallaRepository.save(et);
            }
        }

        return estiloTallaRepository.findByOrdenEstilo_IdEstilo(idEstilo)
                .stream().map(OrdenDto.EstiloTallaResponse::new).collect(Collectors.toList());
    }

    // ---- HELPERS ----

    private Orden getOrdenOrThrow(Integer id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));
    }

    private String generarFolio() {
        String año = String.valueOf(java.time.Year.now().getValue());
        long count = ordenRepository.count() + 1;
        String folio = "ORD" + año + "-" + String.format("%03d", count);
        // Asegurar unicidad
        while (ordenRepository.existsByFolio(folio)) {
            count++;
            folio = "ORD" + año + "-" + String.format("%03d", count);
        }
        return folio;
    }
}
