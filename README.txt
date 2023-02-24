============
Overview
============

"SpeedDialCopy" is a utility tool to copy a dispatcher’s speed dial data including phone speed dial and radio speed dial from one database to another database. 
• The utility tool has interface for user to enter the source dispatcher id and destination dispatcher id. The source and destination dispatcher id can be same or different. 

• The source database and destination database can be configured to the same database. If so, the source dispatcher id and destination dispatcher id should be different.

• The utility tool has interface for user to choose to convert the phone speed dial number to 1 + 10 digits format using the PBX site 1 (in BNSF , Fort Worth site) dial plan defined in the source database.  The current dial plan 10 digit conversion will be like this:              
NumberLength = 7 or 8:  are code + last 7 digits, i.e. 83527000 -> 817-352-7000
NumberLength = 5:       area code + office code + last 4 digits, i.e. 27000 -> 817-352-7000
Not above:              no change



====================
Run The Utility Tool
====================
• Install Java 6 or higher.

• Edit ..\bin\mkenv.bat file
	if default java path is already set up in the system, delete below three lines,
		set JAVAPATH=
		set JAVACPATH=
		set PATH==%JAVAPATH%;%JAVACPATH%	
	 otherwise set the JAVAPATH  and JAVACPATH. (JAVACPATH is required only if the source codes need recompile)
• Edit ..\etc\ini.txt to set the source database parameter and destination database parameter.
• Double click the ..\bin\run.bat to run the utility tool. Follow the application pop up to enter the source dispatcher Id and destination dispatcher Id. The source and destination dispatcher id can be same or different. Please verify the destination dispatcher data has been already added into RT.TRTI_DSPR table in destination database.

===============================================
Run The Utility Tool with Different INI file
===============================================
• Install Java 6 or higher.
• Edit ..\bin\mkenv.bat file
	if default java path is already set up in the system, delete below three lines,
		set JAVAPATH=
		set JAVACPATH=
		set PATH==%JAVAPATH%;%JAVACPATH%	
	 otherwise set the JAVAPATH  and JAVACPATH. (JAVACPATH is required only if the source codes need recompile)
• Make a copy of ..\etc\ini.txt, rename it, e.g. ..\etc\ini_support.txt, then edit it to set the source database parameter and destination database parameter.
• Make a copy of ..\bin\run.bat, rename it, e.g. ..\bin\run_support.bat, then edit it to add the ini file name as an argument, e.g.
	java -DspeedDialCopy.home="%HOME%" SpeedDialCopy ini_support.txt
• Double click the batch file, e.g. ..\bin\run_support.bat to run the utility tool. Follow the application pop up to enter the source dispatcher Id and destination dispatcher Id. The source and destination dispatcher id can be same or different. Please verify the destination dispatcher data has been already added into RT.TRTI_DSPR table in destination database.



*Note: Verify default java path. From command line, run below command, if there is no error, it means the default java path is already set up.
		java -version