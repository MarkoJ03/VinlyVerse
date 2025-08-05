package server.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import server.model.Ploca;

public interface PlocaRepository extends CrudRepository<Ploca, Long> {
	List<Ploca> findByZanrId(Long zanrId);

}
