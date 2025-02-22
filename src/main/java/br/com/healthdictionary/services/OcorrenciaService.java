package br.com.healthdictionary.services;

import br.com.healthdictionary.dto.OcorrenciaDTO;
import br.com.healthdictionary.dto.PacienteDTO;
import br.com.healthdictionary.models.OcorrenciaModel;
import br.com.healthdictionary.models.PacienteModel;
import br.com.healthdictionary.repositories.OcorrenciaRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class OcorrenciaService {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final PacienteService pacienteService;
    private final ModelMapper mapper;

    @Autowired
    public OcorrenciaService(OcorrenciaRepository ocorrenciaRepository,
                             PacienteService pacienteService,
                             ModelMapper mapper) {
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.pacienteService = pacienteService;
        this.mapper = mapper;
    }

    public OcorrenciaDTO recuperaOcorrenciaPeloId(Long id) {
        Optional<OcorrenciaModel> optionalOcorrencia = ocorrenciaRepository.findById(id);

        if (optionalOcorrencia.isEmpty())
            throw new NoSuchElementException("ocorrência não encontrada");

        OcorrenciaModel ocorrenciaModel = optionalOcorrencia.get();
        return mapper.map(ocorrenciaModel, OcorrenciaDTO.class);
    }

    public OcorrenciaDTO cadastraOcorrencia(String cpf, OcorrenciaDTO ocorrenciaDto) {
        PacienteDTO pacienteDto = pacienteService.recuperaPacientePeloCpf(cpf);
        OcorrenciaModel ocorrenciaModel = mapper.map(ocorrenciaDto, OcorrenciaModel.class);
        ocorrenciaModel.setId(null);
        ocorrenciaModel.setRegistro(LocalDateTime.now());
        ocorrenciaModel.setPaciente(PacienteModel.builder().cpf(pacienteDto.getCpf()).build());
        OcorrenciaModel ocorrenciaModelSaved = ocorrenciaRepository.save(ocorrenciaModel);
        return mapper.map(ocorrenciaModelSaved, OcorrenciaDTO.class);
    }

    public OcorrenciaDTO atualizaOcorrencia(Long id, OcorrenciaDTO ocorrenciaDto) {
        Optional<OcorrenciaModel> optionalOcorrencia = ocorrenciaRepository.findById(id);

        if (optionalOcorrencia.isEmpty())
            throw new NoSuchElementException("ocorrência não encontrada");

        OcorrenciaModel ocorrenciaModel = optionalOcorrencia.get();
        ocorrenciaModel.setSintomas(ocorrenciaDto.getSintomas());
        ocorrenciaModel.setDiagnostico(ocorrenciaDto.getDiagnostico());
        OcorrenciaModel ocorrenciaModelUpdated = ocorrenciaRepository.save(ocorrenciaModel);
        return mapper.map(ocorrenciaModelUpdated, OcorrenciaDTO.class);
    }

    public List<OcorrenciaDTO> recuperaOcorrenciasPeloCpf(String cpf) {
        List<OcorrenciaModel> ocorrencias = ocorrenciaRepository.findAllByPacienteCpf(cpf);
        List<OcorrenciaDTO> ocorrenciasDto = new ArrayList<>();
        ocorrencias.forEach(o -> ocorrenciasDto.add(mapper.map(o, OcorrenciaDTO.class)));
        return ocorrenciasDto;
    }

}