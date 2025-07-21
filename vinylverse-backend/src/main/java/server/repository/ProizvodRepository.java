package server.repository;

import org.springframework.data.repository.CrudRepository;


import server.model.Proizvod;

public interface ProizvodRepository extends CrudRepository<Proizvod, Long> {

}
