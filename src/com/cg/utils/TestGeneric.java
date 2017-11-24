package com.cg.utils;

import java.util.ArrayList;
import java.util.List;

public class TestGeneric {

	public static void main(String[] args) {
		List<?> e1 = new ArrayList<Employee>();
		
		List<?> e2 = new ArrayList<Manager>();
		
		List<Employee> ee1 = (List<Employee>)e2;

	}

}
