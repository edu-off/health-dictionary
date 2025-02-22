package br.com.healthdictionary.services;

import br.com.healthdictionary.dto.PacienteDTO;
import br.com.healthdictionary.models.PacienteModel;
import br.com.healthdictionary.repositories.PacienteRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final ModelMapper mapper;

    @Autowired
    public PacienteService(PacienteRepository pacienteRepository, ModelMapper mapper) {
        this.pacienteRepository = pacienteRepository;
        this.mapper = mapper;
    }

    public PacienteDTO recuperaPacientePeloCpf(String cpf) {
        Optional<PacienteModel> optionalPaciente = pacienteRepository.findById(cpf);

        if (optionalPaciente.isEmpty())
            throw new NoSuchElementException("paciente não encontrado");

        return mapper.map(optionalPaciente.get(), PacienteDTO.class);
    }

    public PacienteDTO cadastraPaciente(PacienteDTO pacienteDto) {
        Optional<PacienteModel> optionalPaciente = pacienteRepository.findById(pacienteDto.getCpf());

        if (optionalPaciente.isPresent())
            throw new DuplicateKeyException("paciente já existe");

        PacienteModel pacienteModel = pacienteRepository.save(mapper.map(pacienteDto, PacienteModel.class));
        return mapper.map(pacienteModel, PacienteDTO.class);
    }

    public PacienteDTO atualizaPaciente(String cpf, PacienteDTO pacienteDto) {
        Optional<PacienteModel> optionalPaciente = pacienteRepository.findById(cpf);

        if (optionalPaciente.isEmpty())
            throw new NoSuchElementException("paciente não encontrado");

        PacienteModel pacienteModel = optionalPaciente.get();
        pacienteModel.setNome(pacienteDto.getNome());
        pacienteModel.setDataNascimento(pacienteDto.getDataNascimento());
        pacienteModel.setCodigoSus(pacienteDto.getCodigoSus());
        PacienteModel pacienteModelUpdated = pacienteRepository.save(pacienteModel);
        return mapper.map(pacienteModelUpdated, PacienteDTO.class);
    }

}
