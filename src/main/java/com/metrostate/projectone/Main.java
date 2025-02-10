package com.metrostate.projectone;

//Intra-project imports

import com.metrostate.projectone.controllers.DealershipController;
import com.metrostate.projectone.models.Vehicle;
import com.metrostate.projectone.utils.Printer;

import java.time.Clock;
import java.time.Instant;

//Java API imports

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

	public static void listAllVehicles() {
		controller.printAllVehicles();
	}

	// TODO: [Not-required] Implement it
	public static void listVehiclesByDealershipId() { }

	
	
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
	
	
	private static void addIncomingVehicle(Vehicle g, String dealerID) {
		controller.addVehicle(g, dealerID);
		
	}
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
	private static void handleDealerAcquisition(boolean isEnable) {
		Printer.print("Enter Dealership ID: ");
		String dealershipId = scan.next();
		controller.setDealerAcquisition(isEnable, dealershipId);
		scan.nextLine();
	}

	public static void importFile() {
		controller.importJsonFile(scan);
	}

	private static void exportDealer() {
		controller.exportDealerToJson(scan);
	}

	private static void waitUser() {
		Printer.println("Press \"ENTER\" to continue...");
		scan.nextLine();
	}
}