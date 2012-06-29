package studentview.testing;

//Andy Carle, Berkeley Institute of Design, UC Berkeley

public class Exercise {

	public enum ExerciseType{
		HTML, EDIT
	}
	
	public enum TestResult{
		NOTTRIED, FAILED, PASSED
	}
	
	String name = "";
	String filename = "";
	String testname = "";
	String intro = "";
	ExerciseType type;
	TestResult result;
	
	
	
	
	public Exercise(String name, String filename, ExerciseType type, String testname, String intro){
		this.name = name;
		this.filename = filename;
		this.type = type;		
		this.testname = testname;
		this.intro = intro;		
	}

	public void prepend(String projectname){
		if (filename != null && !("".equalsIgnoreCase(filename))) filename = projectname + filename;
		if (testname != null && !("".equalsIgnoreCase(testname))) testname = projectname + testname;
	}


	public String getName() {
		return name;
	}
	
	public String getIntro(){
		return intro;
	}




	public void setName(String name) {
		this.name = name;
	}




	public String getFilename() {
		return filename;
	}




	public void setFilename(String filename) {
		this.filename = filename;
	}




	public String getTestname() {
		return testname;
	}




	public void setTestname(String testname) {
		this.testname = testname;
	}




	public ExerciseType getType() {
		return type;
	}




	public void setType(ExerciseType type) {
		this.type = type;
	}




	public TestResult getResult() {
		return result;
	}




	public void setResult(TestResult result) {
		this.result = result;
	}
	
	
}
