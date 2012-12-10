package studentview.model;

//Andy Carle, Berkeley Institute of Design, UC Berkeley

public class Step {

	// / TYPE of step
	public enum ExerciseType {
		HTML, CODE, SELFTEST, UNKNOWN
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

	// ///// RESULT of test
	public enum TestResult {
		NOTTRIED, FAILED, PASSED
	}

	public TestResult getResult() {
		return result;
	}

	public void setResult(TestResult result) {
		this.result = result;
	}

	private String name = "";

	public String getName() {
		return name;
	}

	String filename = "";
	String rawFileName = "";

	public String getRawFileName() {
		return rawFileName;
	}

	public void setRawFileName(String rawFileName) {
		this.rawFileName = rawFileName;
	}

	String testclass = "";
	String intro = "";
	ExerciseType type;
	TestResult result;

	public Step(String name, String filename, ExerciseType type,
			String testclass, String intro) {
		this.name = name;
		this.filename = filename;
		this.rawFileName = filename;
		this.type = type;
		this.testclass = testclass;
		this.intro = intro;
	}

	public void prepend(String projectname) {
		if (filename != null && !("".equalsIgnoreCase(filename)))
			filename = projectname + filename;
		// testclass is a class not a file..., just needs the package
		// if (testclass != null && !("".equalsIgnoreCase(testclass))) testclass
		// = projectname + testclass;
	}

	public String getIntro() {
		return intro;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public boolean hasTestClass() {
		// it either doesn't exist or is just whitespace
		return ((testclass != null) && !("".equalsIgnoreCase(testclass.trim())));
	}

	public Class<?> getTestClass() {
		if ((testclass != null) && (testclass != "")) {
			try {
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

	// public void setTestname(String testname) {
	// this.testclass = testname;
	// }

}
