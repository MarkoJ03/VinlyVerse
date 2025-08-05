package server.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import server.model.PravoPristupa;

public interface PravoPristupaRepository extends CrudRepository<PravoPristupa, Long> {
	Optional<PravoPristupa> findByNaziv(String naziv);
}
