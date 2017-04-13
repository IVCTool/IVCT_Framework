/* Copyright 2015, Reinhard Herzog (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.commander;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.LoggerFactory;

/*
 * The Factory is used to create Command objects to be executed by a user interface.
 */
public class Factory {
	
	public static Properties props = null;
	public static final String IVCT_HOME		= "C:/home";
	public static final String IVCT_TS_HOME		= "C:/home";
	public static final String IVCT_SUT_HOME	= "C:/ProjekteLokal/MSG134/DemoFolders/IVCTsut";
	public static final String RTI				= "myRTI";
	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Factory.class);

	/* 
	 * Factory has to be initialized before any commands are being created.
	 */
	public void initialize () throws FileNotFoundException, IOException{
		props = new Properties();
		try {
			props.load(new FileInputStream("IVCT.properties"));
		}
		catch (final FileNotFoundException e){
			LOGGER.warn("no properties file IVCT.properties found");
			props.setProperty(IVCT_HOME, "C:/ProjekteLokal/MSG134/IVCT_Framework");
			props.setProperty(IVCT_TS_HOME, "C:/ProjekteLokal/MSG134/DemoFolders/IVCTtestSuites");
			props.setProperty(IVCT_SUT_HOME, "C:/ProjekteLokal/MSG134/DemoFolders/IVCTsut");
			props.setProperty(RTI, "pRTI");
			props.store (new FileOutputStream("IVCT.properties"), "IVCT Properties");
			LOGGER.warn("New IVCT.properties file has been created with default values. Please verify settings!");
			LOGGER.warn(props.toString());
		}
	}
	
	public CmdListSuT createCmdListSut() {
		return new CmdListSuT();
	}
	
	public CmdListBadges createCmdListBadges () {
		return new CmdListBadges ();
	}
}
