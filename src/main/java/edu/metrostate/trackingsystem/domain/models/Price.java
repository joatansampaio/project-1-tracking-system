// This is the Price object used to store price information of a vehicle object
package edu.metrostate.trackingsystem.domain.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class Price {

    @JacksonXmlText
    public double price;

    @JacksonXmlProperty(isAttribute = true, localName = "unit")
    public String currency;

    public Price() { }

    public Price(double price, String currency) {
        this.price = price;
        this.currency = currency;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        String symbol = switch (currency.toLowerCase()) {
            case "dollars" -> "$ ";  // US Dollar
            case "pounds" -> "£ ";  // British Pound
            case "euros" -> "€ ";  // Euro
            case "yen" -> "¥ ";  // Japanese Yen
            case "rupees" -> "₹ ";  // Indian Rupee
            default -> ""; // Fallback to the original string if unknown
        };

        return String.format("%s%.2f %s", (symbol), price, currency);
    }
}
