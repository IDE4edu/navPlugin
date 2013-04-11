package navigatorView.model;

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
		HTML, CODE, SELFTEST, URL, UNKNOWN
	}
	// if you add to this, there are several boolean methods you might need to tweak below

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
		} else if (type.equalsIgnoreCase("url")) {
			return ExerciseType.URL;
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
	public boolean isURL() {
		return type.equals(ExerciseType.URL);
	}
	public boolean isUNKNOWN() {
		return type.equals(ExerciseType.UNKNOWN);
	}
	
	public boolean openWithJavaEditor() {
		return isCODE();
	}

	public boolean openWithBrowser() {
		return (isHTML() || isSELFTEST() || isURL());
	}

	// to check if the path specified in <source> is local to project 
	// -- because then it has to be prepended, etc.  URL is the only absolute
	// right now.
	public boolean sourceIsProjectLocal() {
		return (isHTML() || isSELFTEST() || isCODE() || isUNKNOWN());
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
	String source;

	public boolean hasSource() {
		return ((source != null) && !(source.equalsIgnoreCase("")));
	}
	
	public String getSource() {
		return source;
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
	
	// PROJECT NAME
	String projectName;
	public String getProjectName() {
		return projectName;
	}
	
	// we assume all strings have been trimmed already.
	public Step(
			String projectName,
			String name, 
			String source, 
			ExerciseType type,
			String intro,
			String testclass, 
			String launchConfig,
			String launchButtonName
			) {
		this.projectName = projectName;
		this.name = name;
		this.source = source;
		this.type = type;
		this.intro = intro;
		this.testclass = testclass;
		this.launchConfig = launchConfig;
		this.launchButtonName = launchButtonName;
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
