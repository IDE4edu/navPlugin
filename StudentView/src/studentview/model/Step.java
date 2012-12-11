package studentview.model;

import javax.security.auth.callback.LanguageCallback;

//Andy Carle, Berkeley Institute of Design, UC Berkeley

public class Step {

	
	// NAME
	private String name = "";
	public String getName() {
		return name;
	}
	
	
	// TYPE 
	public enum ExerciseType {
		HTML, CODE, SELFTEST, UNKNOWN
	}

	// this gets called from the xml parser stuff in Assignment.java
	public static ExerciseType parseExerciseType (String type) {
		if (type == null) {
			return ExerciseType.UNKNOWN;
		}
		if (type.equalsIgnoreCase("code")) {
			return ExerciseType.CODE;
		} else if (type.equalsIgnoreCase("html")) {
			return ExerciseType.HTML;
		} else if (type.equalsIgnoreCase("selftest")) {
			return ExerciseType.SELFTEST;
		} else {
			return ExerciseType.UNKNOWN;
		}
	}
	
	public boolean isHTML() {
		return type.equals(ExerciseType.HTML);
	}
	public boolean isCODE() {
		return type.equals(ExerciseType.CODE);
	}
	public boolean isSELFTEST() {
		return type.equals(ExerciseType.SELFTEST);
	}
	public boolean isUNKNOWN() {
		return type.equals(ExerciseType.UNKNOWN);
	}
	
	public boolean openWithJavaEditor() {
		return isCODE();
	}

	public boolean openWithBrowser() {
		return (isHTML() || isSELFTEST());
	}

	
	// INTRO
	String intro;
	public String getIntro() {
		return intro;
	}
	

	// TYPE
	ExerciseType type;
	public ExerciseType getExerciseType() {
		return type;
	}


	// SOURCE (FILENAME)
	String fullSourcePath;
	String withinProjectSourcePath;

	public boolean hasSource() {
		return ((fullSourcePath != null) && !(fullSourcePath.equalsIgnoreCase("")));
	}
	
	public String getFullSourcePath() {
		return fullSourcePath;
	}

	public String getWithinProjectSourcePath() {
		return withinProjectSourcePath;
	}

	
	// TESTCLASS
	// THIS STUFF DOESN'T WORK YET -- BACK TO LAUNCHERS
	String testclass;
	public String getTestClassString() {
		return testclass;
	}

	public boolean hasTestClass() {
		// it either doesn't exist or is just whitespace
		return ((testclass != null) && !("".equalsIgnoreCase(testclass.trim())));
	}

	public Class<?> getTestClass() {
		if ((testclass != null) && (testclass != "")) {
			try {
				// nope...  need classloaders, yo.
				Class<?> c = Class.forName(testclass);
				return c;
			} catch (ClassNotFoundException e) {
				// TODO instructors should know about this, becase their test
				// class
				// isn't getting resolved for some reason
				System.err.println("Unable to determine test class \""
						+ testclass + "\" for step titled: " + name);
				return null;
			}
		} else {
			return null;
		}
	}

	
	// LAUNCH CONFIG
	String launchConfig;
	public String getLaunchConfig() {
		return launchConfig;
	}
	
	public boolean hasTests() {
		return ((launchConfig != null) && (!(launchConfig.equals(""))));
	}

	// LAUNCH NAME
	String launchButtonName;
	public String getLaunchButtonName() {
		return launchButtonName;
	}
	
	
	
	// we assume all strings have been trimmed already.
	public Step(
			String name, 
			String source, 
			ExerciseType type,
			String intro,
			String testclass, 
			String launchConfig,
			String launchButtonName
			) {
		this.name = name;
		this.fullSourcePath = source;
		this.withinProjectSourcePath = source;
		this.type = type;
		this.intro = intro;
		this.testclass = testclass;
		this.launchConfig = launchConfig;
		this.launchButtonName = launchButtonName;
	}

	public void prependProjectName(String projectname) {
		if (hasSource()) {
			fullSourcePath = projectname + fullSourcePath;
		}
		if (hasTests()) {
			// gotta fix up the launch configuration path
			launchConfig = projectname + launchConfig;
		}
	}



	
	
	

	
	// RESULT of test ... wha?
	TestResult result;
	
	public enum TestResult {
		NOTTRIED, FAILED, PASSED
	}

	public TestResult getResult() {
		return result;
	}

	public void setResult(TestResult result) {
		this.result = result;
	}


}
