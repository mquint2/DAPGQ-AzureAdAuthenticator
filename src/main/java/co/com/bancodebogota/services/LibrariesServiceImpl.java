package co.com.bancodebogota.services;

import java.util.ArrayList;

import org.springframework.stereotype.Service;



@Service
public class LibrariesServiceImpl implements LibrariesService{
	
	private ArrayList<String> libraries;
	
	
	
	public LibrariesServiceImpl() {
		loadLibraries();
	}

	private void loadLibraries(){
		libraries = new ArrayList<>();
		this.libraries.add("bbrsa.js");
		this.libraries.add("libRsa.js");
	}

	@Override
	public ArrayList<String> getLibraries() {
		return this.libraries;
	}

	
	
	
	
	

}
