package gui.listeners;

//onDataChange ser� disparado quando atualizar a lista no formul�rio sempre que salvar, alterar ou deletar algum item
//sempre que os dados mudarem este evento sera disparado para atualizar a lista
//a classe DepartmentListController implementara este m�todo tbm
public interface DataChangeListener {
	
	void onDataChanged();
//na classe DepartmeFormController tera uma lista
}
