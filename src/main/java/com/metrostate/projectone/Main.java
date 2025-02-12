package com.metrostate.projectone;

/*
	Phase 1 of our implementation of an inventory system for use by a company that tracks
	vehicle inventories across multiple dealerships.
	Uses https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple as a Maven dependency for json format support

	Implemented by (in random order chosen by the website wheel of names):
	GitHub ID: @joatansampaio
	GitHub ID: @dragostego
	GitHub ID: @hcxmed
	GitHub ID: @Bit2ByteGitHype
	GitHub ID: @yoyoeyes

	For the Metro State University ICS 372-02 Spring 2025 Semester taught by Timmothy Carlson
 */

//Intra-project imports
import com.metrostate.projectone.controllers.DealershipController;
import com.metrostate.projectone.models.Vehicle;
import com.metrostate.projectone.utils.Printer;

//Java API imports
import java.time.Clock;
import java.time.Instant;
import java.util.Scanner;

public class Main {

	static Scanner scan = new Scanner(System.in);
	static DealershipController controller = new DealershipController();

	public static void main(String[] args) {

		while (true) {
			showMainMenu();
			String option = scan.nextLine();
			switch (option) {
				case "1":
					listAllVehicles();
					break;
				case "2":
					listVehiclesByDealershipId();
					break;
				case "3":
					carEntryTool();
					break;
				case "4":
					handleDealerAcquisition(true);
					break;
				case "5":
					handleDealerAcquisition(false);
					break;
				case "6":
					importFile();
					break;
				case "7":
					exportDealer();
					break;
				case "0":
					System.exit(0);
				default:
					Printer.println("Invalid option! Enter one of the available options.");
			}
			waitUser();
		}
	}


	/**
	 * Displays a menu system in the console, could be replaced by graphical interface later
	 */
	public static void showMainMenu() {
		Printer.println("Tracking system v1");
		Printer.println("-".repeat(30), Printer.Color.BLUE, false);
		Printer.println("1 - List All Vehicles");
		Printer.println("2 - List Vehicles By Dealership ID");
		Printer.println("3 - Add Incoming Vehicle");
		Printer.println("4 - Enable Vehicle Acquisition");
		Printer.println("5 - Disable Vehicle Acquisition");
		Printer.println("-".repeat(30));
		Printer.println("6 - Import Json File", Printer.Color.CYAN, false);
		Printer.println("7 - Export Dealer Vehicles To Json");
		Printer.println("-".repeat(30));
		Printer.println("0 - Exit", Printer.Color.PURPLE);
		Printer.print("Enter an option: ");
	}

	/**
	 * Prints all vehicles using a method call from a DealershipController object
	 */
	public static void listAllVehicles() {
		controller.printAllVehicles();
	}

	/**
	 * Lists all current dealers and prompts user to select one to print the vehicles of.
	 */
	public static void listVehiclesByDealershipId() {
		if(!controller.printAllDealers()){
			return;
		}
		Printer.println("Enter the Dealership ID of the dealer you want to see the vehicles of:");
		String dealershipID = scan.nextLine();
		if(controller.isValidDealershipID(dealershipID)){
			controller.printVehiclesForDealershipId(dealershipID);
		}
		else {
			Printer.println("Dealership not found.");
		}
	}


	/**
	 * Prompts for information from the user about a vehicle to be added to the database
	 */
	public static void carEntryTool() {
		Printer.print("What is the make of the vehicle");
		System.out.println();
		String make = scan.nextLine();
		Printer.print("What is the model of the vehicle");
		System.out.println();
		String model = scan.nextLine();
		boolean validType = false;
		String carType = "";
		while (!validType) {
			Printer.print("What is the car type of the vehicle (sedan, pickup)");
			System.out.println();
			carType = scan.nextLine();
			validType = isValidCarType(carType);
		}
		Clock timer = Clock.systemUTC();
		Instant cally = Instant.now(timer);
		long call2 = cally.getEpochSecond();
		Printer.print("What is the price of the vehicle");
		System.out.println();
		Float price = Float.parseFloat(scan.nextLine());
		Printer.print("What is the dealer ID?");
		System.out.println();
		String dealID = scan.nextLine();
		Printer.print("What is the vehicle ID?");
		System.out.println();
		String vehiID = scan.nextLine();
		Vehicle newVehicle = new Vehicle(vehiID, make, model, call2, price, dealID, carType);
		addIncomingVehicle(newVehicle, dealID);
	}


	/**
	 * Calls to a DealershipController object to add a given vehicle to a given dealer.
	 * @param g A vehicle object to be added to the database
	 * @param dealerID The ID for the dealership to receive the vehicle
	 */
	private static void addIncomingVehicle(Vehicle g, String dealerID) {
		controller.addVehicle(g, dealerID);
		
	}

	/**
	 * Checks for a valid vehicle entry type
	 * @param carType The type of vehicle to check for validity
	 * @return True if vehicle type is valid, false if the vehicle type is invalid
	 */
	//used for the car entry tool, sorry for not using return was running late as is
	private static boolean isValidCarType(String carType) {
		if (carType.equalsIgnoreCase("sedan") || carType.equalsIgnoreCase("suv") || carType.equalsIgnoreCase("pickup") || carType.equalsIgnoreCase("sports car") || carType.equalsIgnoreCase("sportscar") )
		{
			return true;
			
		} else {
			Printer.print(
					"Unsupported car type, reminder that the valid car types are sedan, sports car, suv and pickup \n");
			Printer.println("invalid car type was " + carType);
			return false;
		}
			

	}

	/**
	 * Prompts for a dealership ID to be enabled/disabled for vehicle acquisition
	 * @param isEnable true will enable vehicle acquisition, false will disable vehicle acquisition for the given dealership ID
	 */
	private static void handleDealerAcquisition(boolean isEnable) {
		Printer.print("Enter Dealership ID: ");
		String dealershipId = scan.next();
		controller.setDealerAcquisition(isEnable, dealershipId);
		scan.nextLine();
	}

	/**
	 * Calls a method on a DealershipController object to handle importing of a json file from the src/main/resources/ folder
	 */
	public static void importFile() {
		controller.importJsonFile(scan);
	}

	/**
	 * Calls a method on a DealershipController object to handle exporting of a json file to the src/main/resources/ folder
	 */
	private static void exportDealer() {
		controller.exportDealerToJson(scan);
	}

	/**
	 * Waits for the user to be ready
	 */
	private static void waitUser() {
		Printer.println("Press \"ENTER\" to continue...");
		scan.nextLine();
	}
}