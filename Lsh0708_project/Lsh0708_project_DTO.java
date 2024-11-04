package Lsh0708_project;

import javax.swing.JCheckBox;

public class Lsh0708_project_DTO {
	
	

	int id;
	String name;
	String department;
	String birthdate;
	String address;
	String telNum;
	String sex;
	
	public Lsh0708_project_DTO() {};
	
	public Lsh0708_project_DTO(int id, String name, String department, String birthdate, String address, String telNum,
			String sex) {
		super();
		this.id = id;
		this.name = name;
		this.department = department;
		this.birthdate = birthdate;
		this.address = address;
		this.telNum = telNum;
		this.sex = sex;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelNum() {
		return telNum;
	}

	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	
	@Override
	public String toString() {
		return "Lsh0708_project_DTO [id=" + id + ", name=" + name + ", department=" + department + ", birthdate="
				+ birthdate + ", address=" + address + ", telNum=" + telNum + ", sex=" + sex + "]";
	}
	
	
}
