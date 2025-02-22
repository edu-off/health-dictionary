package br.com.healthdictionary.services;

import br.com.healthdictionary.dto.CidDTO;
import br.com.healthdictionary.dto.MedicacaoDTO;
import br.com.healthdictionary.dto.TratamentoDTO;
import br.com.healthdictionary.models.CidModel;
import br.com.healthdictionary.models.TratamentoModel;
import br.com.healthdictionary.repositories.TratamentoRepository;
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
public class TratamentoService {

    private final TratamentoRepository tratamentoRepository;
    private final CidService cidService;
    private final MedicacaoService medicacaoService;
    private final ModelMapper mapper;

    @Autowired
    public TratamentoService(TratamentoRepository tratamentoRepository,
                             CidService cidService,
                             @Lazy MedicacaoService medicacaoService,
                             ModelMapper mapper) {
        this.tratamentoRepository = tratamentoRepository;
        this.cidService = cidService;
        this.medicacaoService = medicacaoService;
        this.mapper = mapper;
    }

    public TratamentoDTO recuperaTratamentoPeloId(Long id) {
        Optional<TratamentoModel> optionalTratamento = tratamentoRepository.findById(id);

        if (optionalTratamento.isEmpty())
            throw new NoSuchElementException("tratamento não encontrado");

        TratamentoModel tratamentoModel = optionalTratamento.get();
        TratamentoDTO tratamentoDto = mapper.map(tratamentoModel, TratamentoDTO.class);
        tratamentoDto.setMedicacoes(medicacaoService.recuperaMedicacoesPeloTratamentoId(id));
        return tratamentoDto;
    }

    public TratamentoDTO cadastraTratamento(String cidCodigo, TratamentoDTO tratamentoDto) {
        CidDTO cidDto = cidService.recuperaCidPeloCodigo(cidCodigo);
        TratamentoModel tratamentoModel = mapper.map(tratamentoDto, TratamentoModel.class);
        tratamentoModel.setId(null);
        tratamentoModel.setCid(CidModel.builder().codigo(cidDto.getCodigo()).build());
        TratamentoModel tratamentoModelSaved = tratamentoRepository.save(tratamentoModel);
        TratamentoDTO tratamentoDtoSaved = mapper.map(tratamentoModelSaved, TratamentoDTO.class);
        tratamentoDtoSaved.setMedicacoes(new ArrayList<>());

        tratamentoDto.getMedicacoes().forEach(medicacaoDto -> {
            MedicacaoDTO medicacaoDtoSaved = medicacaoService.cadastraMedicacao(tratamentoDtoSaved.getId(), medicacaoDto);
            tratamentoDtoSaved.getMedicacoes().add(medicacaoDtoSaved);
        });

        return tratamentoDtoSaved;
    }

    public TratamentoDTO atualizaTratamento(Long id, TratamentoDTO tratamentoDto) {
        Optional<TratamentoModel> optionalTratamento = tratamentoRepository.findById(id);

        if (optionalTratamento.isEmpty())
            throw new NoSuchElementException("tratamento não encontrado");

        TratamentoModel tratamentoModel = optionalTratamento.get();
        tratamentoModel.setTratamento(tratamentoDto.getTratamento());
        tratamentoModel.setReferenciaBibliografica(tratamentoDto.getReferenciaBibliografica());
        TratamentoModel tratamentoModelUpdated = tratamentoRepository.save(tratamentoModel);
        TratamentoDTO tratamentoDtoUpdated = mapper.map(tratamentoModelUpdated, TratamentoDTO.class);
        tratamentoDtoUpdated.setMedicacoes(new ArrayList<>());

        tratamentoDto.getMedicacoes().forEach(medicacaoDto -> {
            MedicacaoDTO medicacaoDtoUpdated = medicacaoService.atualizaMedicacao(medicacaoDto.getId(), medicacaoDto);
            tratamentoDtoUpdated.getMedicacoes().add(medicacaoDtoUpdated);
        });

        return tratamentoDtoUpdated;
    }

    public MedicacaoDTO adicionaMedicacaoNoTratamento(Long tratamentoId, MedicacaoDTO medicacaoDto) {
        Optional<TratamentoModel> optionalTratamento = tratamentoRepository.findById(tratamentoId);

        if (optionalTratamento.isEmpty())
            throw new NoSuchElementException("tratamento não encontrado");

        return medicacaoService.cadastraMedicacao(tratamentoId, medicacaoDto);
    }

    public void excluiMedicacaoNoTratamento(Long medicacaoId) {
        medicacaoService.excluiMedicacao(medicacaoId);
    }

    public List<TratamentoDTO> recuperaTratamentosPeloCodigoCid(String cidCodigo) {
        List<TratamentoModel> tratamentos = tratamentoRepository.findAllByCidCodigo(cidCodigo);
        List<TratamentoDTO> tratamentosDto = new ArrayList<>();
        tratamentos.forEach(t -> tratamentosDto.add(mapper.map(t, TratamentoDTO.class)));
        return tratamentosDto;
    }

}