package studentview.model;

import java.io.File;
import java.net.URI;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Group;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import studentview.model.Step.ExerciseType;

public class Assignment implements Comparable<Assignment> {

	String projectName;
	String name;
	String intro = "";
	String category;
	String subCategory;
	int sortOrder;
	Vector<Step> exercises;
	IFile isaFile;

	public IFile getIsaFile() {
		return isaFile;
	}

	public void setIsaFile(IFile isaFile) {
		this.isaFile = isaFile;
	}

	Group group;

	public Assignment(String projectName, String intro, String name, Vector<Step> exercises,
			String category, String subCategory, String sortOrder) {
		this.projectName = projectName;
		this.intro = intro;
		this.exercises = exercises;
		this.name = name;
		this.category = category;
		this.subCategory = subCategory;
		try {
			this.sortOrder = Integer.parseInt(sortOrder);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.err.println("hey, bad sort order " + sortOrder + " on assignment " + name + " -- couldn't convert it to int");
			this.sortOrder = 1;
		}
	}

	public int compareTo(Assignment that) {
	    final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;
		if (this.sortOrder == that.sortOrder){
			return EQUAL; 
		} else if (this.sortOrder < that.sortOrder) {
			return BEFORE;
		} else {
			return AFTER;
		}
	}

	public String getName() {
		return name;
	}

	public String getIntro() {
		return intro;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Group getGroup() {
		return this.group;
	}

	public static Assignment parseISA(IFile isa) {
		// project is that which contains the isa file
		String projectName = isa.getFullPath().segment(0);
		URI uri = isa.getLocationURI();
		File file = new File(uri);
		Handler handler;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			handler = new Handler();
			handler.projectName = projectName;
			saxParser.parse(file, handler);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return null;
		}
		Assignment s = handler.getAssignment();
		s.isaFile = isa;
		return handler.getAssignment();
	}

	public Vector<Step> getExercises() {
		return exercises;
	}

	public Step getExercise(String name) {
		for (Step e : exercises) {
			if (e.getName().equals(name))
				return e;
		}
		return null;
	}

	private static class Handler extends DefaultHandler {
		public static final String isaTag = "isa";
		public static final String exerciseTag = "exercise";
		public static final String categoryTag = "category";
		public static final String subcategoryTag = "subcategory";
		public static final String sortorderTag = "sortorder";

		public static final String nameTag = "name";
		public static final String typeTag = "type";
		public static final String introTag = "intro"; // optional
		public static final String sourceTag = "source";
		public static final String testclassTag = "testclass"; // unused now
		public static final String launchTag = "launch"; // optional
		public static final String launchButtonNameTag = "launchButtonName"; // optional

		@Override
		public void startDocument() throws SAXException {
		}

		@Override
		public void endDocument() throws SAXException {
			assign = new Assignment(projectName, isaIntro, isaName, exercises, isaCategory, isaSubCategory, isaSortOrder);
		}

		private Assignment assign;
		public String projectName;

		public Assignment getAssignment() {
			return assign;
		}

		String isaIntro = "";
		String isaName = "";
		String isaCategory = "";
		String isaSubCategory = "";
		String isaSortOrder = "1";

		boolean inExercise = false;

		String name;
		ExerciseType type; // default comes from parseExerciseType()
		String intro;
		String source;
		String testclass;
		String launch;
		String launchButtonName;

		// Defaults
		private void resetDefaults() {
			name = "";
			ExerciseType type = Step.parseExerciseType(null);
			intro = "";
			source = "";
			testclass = null;
			launch = null;
			launchButtonName = "Run Tests";
		}

		StringBuffer buffer = new StringBuffer();
		Vector<Step> exercises = new Vector<Step>();

		@Override
		public void startElement(String namespaceURI, String localName,
				String qName, Attributes atts) throws SAXException {
			if (qName.equalsIgnoreCase(exerciseTag)) {
				inExercise = true;
				resetDefaults();
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			String s = buffer.toString().trim();
			if (qName.equalsIgnoreCase(exerciseTag)) {
				exercises.add(new Step(projectName, name, source, type, intro, testclass,
						launch, launchButtonName));
				reset(false);
			} else if (!inExercise) {
				if (qName.equalsIgnoreCase(introTag)) {
					isaIntro = s;
				} else if (qName.equalsIgnoreCase(nameTag)) {
					isaName = s;
				} else if (qName.equalsIgnoreCase(categoryTag)) {
					isaCategory = s;
				} else if (qName.equalsIgnoreCase(subcategoryTag)) {
					isaSubCategory = s;
				} else if (qName.equalsIgnoreCase(sortorderTag)) {
					isaSortOrder = s;
				}
				reset(false);
			} else {
				// in the exercise tag
				if (qName.equalsIgnoreCase(introTag)) {
					intro = s;
				} else if (qName.equalsIgnoreCase(nameTag)) {
					name = s;
				} else if (qName.equalsIgnoreCase(typeTag)) {
					type = Step.parseExerciseType(s); // code is in Step
				} else if (qName.equalsIgnoreCase(sourceTag)) {
					source = s;
				} else if (qName.equalsIgnoreCase(testclassTag)) {
					testclass = s;
				} else if (qName.equalsIgnoreCase(launchTag)) {
					launch = s;
				} else if (qName.equalsIgnoreCase(launchButtonNameTag)) {
					launchButtonName = s;
				} else if (qName.equalsIgnoreCase(isaTag)) {
					// do nothing
				} else {
					System.err.println("Bad tag in isa file: " + qName
							+ " String is: " + s);
				}
				reset(inExercise);
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) {
			buffer.append(new String(ch, start, length));
		}

		private void reset(boolean inEx) {
			buffer = new StringBuffer();
			inExercise = inEx;
		}

	}

}
