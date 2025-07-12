package com.gestionale.mapper;

import com.gestionale.dto.*;
import com.gestionale.entity.*;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mapper per convertire tra DTO e Entity dei documenti
 */
@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DocumentoMapper {

    // Conversioni per Documento
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numero", ignore = true)
    @Mapping(target = "anno", ignore = true)
    @Mapping(target = "stato", ignore = true)
    Documento toEntity(DocumentoCreateDto dto);

    @Mapping(target = "tipoDocumento", source = "tipoDocumento")
    @Mapping(target = "statoDocumento", source = "stato")
    DocumentoResponseDto toResponseDto(Documento entity);

    List<DocumentoResponseDto> toResponseDtoList(List<Documento> entities);

    // Conversioni per RigaDocumento
    @Mapping(target = "id", ignore = true)
    RigaDocumento toRigaEntity(RigaDocumentoDto dto);

    RigaDocumentoResponseDto toRigaResponseDto(RigaDocumento entity);

    List<RigaDocumentoResponseDto> toRigaResponseDtoList(List<RigaDocumento> entities);

    // Conversioni per Scadenza
    @Mapping(target = "id", ignore = true)
    Scadenza toScadenzaEntity(ScadenzaDto dto);

    ScadenzaResponseDto toScadenzaResponseDto(Scadenza entity);

    List<ScadenzaResponseDto> toScadenzaResponseDtoList(List<Scadenza> entities);

    // Conversioni per aggiornamenti
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numero", ignore = true)
    @Mapping(target = "anno", ignore = true)
    @Mapping(target = "stato", ignore = true)
    void updateEntityFromDto(DocumentoCreateDto dto, @MappingTarget Documento entity);

    // Conversioni per AliquotaIva
    default BigDecimal map(com.gestionale.enums.AliquotaIva aliquota) {
        return aliquota != null ? BigDecimal.valueOf(aliquota.getPercentuale()) : null;
    }

    default com.gestionale.enums.AliquotaIva map(BigDecimal percentuale) {
        if (percentuale == null) return null;
        return com.gestionale.enums.AliquotaIva.byPercentuale(percentuale.doubleValue());
    }

    // Conversioni per enum to String
    default String map(TipoDocumento tipo) {
        return tipo != null ? tipo.toString() : null;
    }

    default String map(StatoDocumento stato) {
        return stato != null ? stato.toString() : null;
    }

    default String map(ModalitaPagamento modalita) {
        return modalita != null ? modalita.toString() : null;
    }

    default String map(CausaleTrasporto causale) {
        return causale != null ? causale.toString() : null;
    }
}
