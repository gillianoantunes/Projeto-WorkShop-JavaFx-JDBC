package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {

	//declara dependencia chamada de dao e recebe a fabrica 
	private DepartmentDao dao = DaoFactory.createDepartmentDao();

	public List<Department> findAll(){
		//vai no banco e busca os departamentos
		return dao.findAll();
	}
	
	//metodo para verificar se vou inserir novo departamento no banco de dados ou atualizar
	//se id igual estou inserindo um novo caso contario alterar
	public void saveOrUpdate(Department obj) {
		if(obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
} 
