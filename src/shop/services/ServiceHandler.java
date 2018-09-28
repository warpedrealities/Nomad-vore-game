package shop.services;

public class ServiceHandler {
	private int amount, cost;
	private ShipService service;
	private String prompt;
	
	
	public ServiceHandler(int amount, ShipService service, String prompt) {
		this.prompt=prompt;
		this.service=service;
		this.amount=amount;
		genPrompt();
	}
	
	private void genPrompt()
	{
		cost=amount*service.getCost();
		prompt=prompt.replace("$", Integer.toString(amount));
		prompt=prompt.replace("£", Integer.toString(cost));
	}
	
	public int getAmount() {
		return amount;
	}
	public int getCost() {
		return cost;
	}
	public ShipService getService() {
		return service;
	}
	public String getPrompt() {
		return prompt;
	}
	
}
