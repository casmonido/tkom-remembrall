package remembrall.types;

public class Location {
	String houseNumber;
	String street;
	String city;
	String zipcode;
	String country;
	
	public Location(String houseNum, String street, String city,
			String zip, String country) {
		houseNumber = houseNum;
		this.street = street;
		this.city = city;
		zipcode = zip;
		this.country = country;	
	}
}
