package com.metrostate.projectone;

//Intra-project imports
import com.metrostate.projectone.controllers.DealershipController;
import com.metrostate.projectone.utils.FileHandler;

//Maven json-simple dependency imports
import org.json.simple.JSONObject;

//Java API imports
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        DealershipController controller = new DealershipController();
        JSONObject jsonObject = null;

        while(true) {
            showMainMenu();
            String option = scan.nextLine();
            switch(option) {
                case "1":
                    listAllVehicles();
                    break;
                case "2":
                    break;
                case "3":
                    break;
                case "4":
                    break;
                case "5":
                    break;
                case "6":
                    FileHandler handler = new FileHandler();
                    //src/main/resources/inventory.json is the initial inventory file to load
                    jsonObject = handler.read("src/main/resources/inventory.json");
                    if (jsonObject != null){
                        System.out.println("Inventory file loaded");
                    } else {
                        throw new IOException("Error loading file");
                    }

                    //To print out all the json for testing
                    //System.out.println(jsonObject.toJSONString());
                    break;
                case "7":
                    break;
                case "0":
                    System.exit(0);
                default:
                    System.out.println("Invalid option! Enter one of the available options.");
            }
        }
    }

    public static void showMainMenu() {
        System.out.println("Tracking system v1");
        System.out.println("-".repeat(30));
        System.out.println("1 - List All Vehicles");
        System.out.println("2 - List Vehicles By Dealership ID");
        System.out.println("3 - Add Incoming Vehicle");
        System.out.println("4 - Enable Vehicle Acquisition");
        System.out.println("5 - Disable Vehicle Acquisition");
        System.out.println("-".repeat(30));
        System.out.println("6 - Import Json File");
        System.out.println("7 - Export Dealer Vehicles To Json");
        System.out.println("-".repeat(30));
        System.out.println("0 - Exit");
    }

    public static void listAllVehicles() {

    }
}