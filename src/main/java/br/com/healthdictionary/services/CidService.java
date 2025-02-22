package br.com.healthdictionary.services;

import br.com.healthdictionary.dto.CidDTO;
import br.com.healthdictionary.models.CidModel;
import br.com.healthdictionary.repositories.CidRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class CidService {

    private final CidRepository cidRepository;
    private final ModelMapper mapper;

    @Autowired
    public CidService(CidRepository cidRepository, ModelMapper mapper) {
        this.cidRepository = cidRepository;
        this.mapper = mapper;
    }

    public List<CidDTO> recuperaTodosOsCids() {
        List<CidModel> cidsModel = cidRepository.findAll();
        List<CidDTO> cidsDto = new ArrayList<>();
        cidsModel.forEach(c -> cidsDto.add(mapper.map(c, CidDTO.class)));
        return cidsDto;
    }

    public CidDTO recuperaCidPeloCodigo(String codigo) {
        Optional<CidModel> optionalCidModel = cidRepository.findById(codigo);

        if (optionalCidModel.isEmpty())
            throw new NoSuchElementException("cid não encontrado");

        return mapper.map(optionalCidModel.get(), CidDTO.class);
    }

    public CidDTO cadastraCid(CidDTO cidDto) {
        Optional<CidModel> optionalCidModel = cidRepository.findById(cidDto.getCodigo());

        if (optionalCidModel.isPresent())
            throw new DuplicateKeyException("cid já existe");

        CidModel cidModel = cidRepository.save(mapper.map(cidDto, CidModel.class));
        return mapper.map(cidModel, CidDTO.class);
    }

    public CidDTO atualizaCid(String codigo, CidDTO cidDto) {
        Optional<CidModel> optionalCidModel = cidRepository.findById(codigo);

        if (optionalCidModel.isEmpty())
            throw new NoSuchElementException("cid não encontrado");

        CidModel cidModel = optionalCidModel.get();
        cidModel.setDescricao(cidDto.getDescricao());
        CidModel cidModelUpdated = cidRepository.save(cidModel);
        return mapper.map(cidModelUpdated, CidDTO.class);
    }

}
