package gui.listeners;

//onDataChange será disparado quando atualizar a lista no formulário sempre que salvar, alterar ou deletar algum item
//sempre que os dados mudarem este evento sera disparado para atualizar a lista
//a classe DepartmentListController implementara este método tbm
public interface DataChangeListener {
	
	void onDataChanged();
//na classe DepartmeFormController tera uma lista
}
