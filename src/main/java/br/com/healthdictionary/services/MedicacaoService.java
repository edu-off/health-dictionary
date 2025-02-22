package br.com.healthdictionary.services;

import br.com.healthdictionary.dto.MedicacaoDTO;
import br.com.healthdictionary.dto.TratamentoDTO;
import br.com.healthdictionary.models.MedicacaoModel;
import br.com.healthdictionary.models.TratamentoModel;
import br.com.healthdictionary.repositories.MedicacaoRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class MedicacaoService {

    private final MedicacaoRepository medicacaoRepository;
    private final TratamentoService tratamentoService;
    private final ModelMapper mapper;

    @Autowired
    public MedicacaoService(MedicacaoRepository medicacaoRepository,
                            @Lazy TratamentoService tratamentoService,
                            ModelMapper mapper) {
        this.medicacaoRepository = medicacaoRepository;
        this.tratamentoService = tratamentoService;
        this.mapper = mapper;
    }

    public List<MedicacaoDTO> recuperaMedicacoesPeloTratamentoId(Long tratamentoId) {
        List<MedicacaoModel> medicacoesModel = medicacaoRepository.findAllByTratamentoId(tratamentoId);
        List<MedicacaoDTO> medicacoesDto = new ArrayList<>();
        medicacoesModel.forEach(medicacao -> medicacoesDto.add(mapper.map(medicacao, MedicacaoDTO.class)));
        return medicacoesDto;
    }

    public MedicacaoDTO cadastraMedicacao(Long tratamentoId, MedicacaoDTO medicacaoDto) {
        TratamentoDTO tratamentoDto = tratamentoService.recuperaTratamentoPeloId(tratamentoId);
        MedicacaoModel medicacaoModel = mapper.map(medicacaoDto, MedicacaoModel.class);
        medicacaoModel.setId(null);
        medicacaoModel.setTratamento(mapper.map(tratamentoDto, TratamentoModel.class));
        MedicacaoModel medicacaoModelSaved = medicacaoRepository.save(medicacaoModel);
        return mapper.map(medicacaoModelSaved, MedicacaoDTO.class);
    }

    public MedicacaoDTO atualizaMedicacao(Long id, MedicacaoDTO medicacaoDto) {
        Optional<MedicacaoModel> optionalMedicacao = medicacaoRepository.findById(id);

        if (optionalMedicacao.isEmpty())
            throw new NoSuchElementException("medicação não encontrada");

        MedicacaoModel medicacaoModel = optionalMedicacao.get();
        medicacaoModel.setMedicacao(medicacaoDto.getMedicacao());
        medicacaoModel.setDosagem(medicacaoDto.getDosagem());
        medicacaoModel.setQuantidadeDias(medicacaoDto.getQuantidadeDias());
        medicacaoModel.setObservacao(medicacaoDto.getObservacao());
        MedicacaoModel medicacaoModelUpdated = medicacaoRepository.save(medicacaoModel);
        return mapper.map(medicacaoModelUpdated, MedicacaoDTO.class);
    }

    public void excluiMedicacao(Long id) {
        Optional<MedicacaoModel> optionalMedicacao = medicacaoRepository.findById(id);

        if (optionalMedicacao.isEmpty())
            throw new NoSuchElementException("medicação não encontrada");

        medicacaoRepository.deleteById(id);
    }


}
