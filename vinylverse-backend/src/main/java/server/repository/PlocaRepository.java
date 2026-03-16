package server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import server.model.Ploca;

public interface PlocaRepository extends JpaRepository<Ploca, Long> {
	List<Ploca> findByZanrId(Long zanrId);

	@Query("SELECT p FROM Ploca p ORDER BY function('RAND')")
	Page<Ploca> findRandom(Pageable pageable);
}
