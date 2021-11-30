package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {

	//declara dependencia chamada de dao e recebe a fabrica 
	private SellerDao dao = DaoFactory.createSellerDao();

	public List<Seller> findAll(){
		//vai no banco e busca os departamentos
		return dao.findAll();
	}
	
	//metodo para verificar se vou inserir novo departamento no banco de dados ou atualizar
	//se id igual estou inserindo um novo caso contario alterar
	public void saveOrUpdate(Seller obj) {
		if(obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	//metodo para remover um departamento
	public void remove(Seller obj) {
		dao.deleteById(obj.getId());
	}
} 
