package projeto.springboot.model;

public enum Cargo {

	JUNIOR("Junior"), PLENO("Pleno"), SENIOR("Senior");

	private String nomeCargo;

	private Cargo(String nomeCargo) {
		this.nomeCargo = nomeCargo;
	}

	public String getNomeCargo() {
		return nomeCargo;
	}

	public void setNomeCargo(String nomeCargo) {
		this.nomeCargo = nomeCargo;
	}


}
