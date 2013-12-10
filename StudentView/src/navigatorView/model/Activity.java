package navigatorView.model;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import navigatorView.model.Step.StepType;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.widgets.Group;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import edu.berkeley.eduride.base_plugin.util.ISAFormatException;


public class Activity implements Comparable<Activity> {

	String projectName;
	String name;
	String intro = "";
	String category;
	String subCategory;
	int sortOrder;
	ArrayList<Step> steps;
	IFile isaFile;

	public IFile getIsaFile() {
		return isaFile;
	}

	public void setIsaFile(IFile isaFile) {
		this.isaFile = isaFile;
	}

	Group group;

	public Activity(String projectName, String intro, String name, ArrayList<Step> steps,
			String category, String subCategory, String sortOrder) {
		this.projectName = projectName;
		this.intro = intro;
		this.steps = steps;
		this.name = name;
		this.category = category;
		this.subCategory = subCategory;
		try {
			this.sortOrder = Integer.parseInt(sortOrder);
		} catch (NumberFormatException e) {
			// TODO Throw exception up, please, for authors
			System.err.println("hey, bad sort order " + sortOrder + " on activity " + name + " -- couldn't convert it to int");
			this.sortOrder = 1;
		}
	}

	public int compareTo(Activity that) {
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

	public String getProjectName() {
		return projectName;
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

	public static Activity parseISA(IFile isa) {
		Handler handler;
		try {
			// project is that which contains the isa file
			String projectName = isa.getFullPath().segment(0);
			URI uri = isa.getLocationURI();
			File file = new File(uri);

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			handler = new Handler();
			handler.projectName = projectName;
			saxParser.parse(file, handler);

			Activity activity = handler.getActivity();
			activity.isaFile = isa;

			// for Feedback view
			ArrayList<Step> steps = activity.getSteps();
			for (Step step : steps) {
				if (step.isCODE() && step.hasTestClass()) {
					setupFeedbackModel(step);
				}
			}
			return handler.getActivity();

			// THESE EXCEPTIONS NEED TO WARN USER (ISA AUTHOR) SOMEHOW
		} catch (ISAFormatException e) {
			// TODO: ISA file has bad content
			e.printStackTrace();
			return null; // ?
		} catch (SAXParseException e) {
			// problem in the XML somewhere
			System.err.println("ISA File Problem: tag " + e.getPublicId()
					+ ", line " + e.getLineNumber() + ", column "
					+ e.getColumnNumber());
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}

	}

	
	
	private static void setupFeedbackModel(Step step) throws ISAFormatException {
		edu.berkeley.eduride.feedbackview.FeedbackModelProvider.setup(
				JavaCore.createCompilationUnitFrom(step.getSourceIFile()), 
				null,
				JavaCore.createCompilationUnitFrom(step.getTestClassIFile())
				);
	}
	
	
	public ArrayList<Step> getSteps() {
		return steps;
	}

	public Step getStep(String name) {
		for (Step e : steps) {
			if (e.getName().equals(name))
				return e;
		}
		return null;
	}

	private static class Handler extends DefaultHandler {
		public static final String isaTag = "isa";
		public static final String stepTag = "exercise";
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
			act = new Activity(projectName, isaIntro, isaName, steps, isaCategory, isaSubCategory, isaSortOrder);
		}

		private Activity act;
		public String projectName;

		public Activity getActivity() {
			return act;
		}

		String isaIntro = "";
		String isaName = "";
		String isaCategory = "";
		String isaSubCategory = "";
		String isaSortOrder = "1";

		boolean inStep = false;

		String name;
		StepType type; // default comes from parseStepType()
		String intro;
		String source;
		String testclass;
		String launch;
		String launchButtonName;

		// Defaults
		private void resetDefaults() {
			name = "";
			StepType type = Step.parseStepType(null);
			intro = "";
			source = "";
			testclass = null;
			launch = null;
			launchButtonName = "Run Tests";
		}

		StringBuffer buffer = new StringBuffer();
		ArrayList<Step> steps = new ArrayList<Step>();

		@Override
		public void startElement(String namespaceURI, String localName,
				String qName, Attributes atts) throws SAXException {
			if (qName.equalsIgnoreCase(stepTag)) {
				inStep = true;
				resetDefaults();
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			String s = buffer.toString().trim();
			if (qName.equalsIgnoreCase(stepTag)) {
				steps.add(new Step(projectName, name, source, type, intro, testclass,
						launch, launchButtonName));
				reset(false);
			} else if (!inStep) {
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
				// in the step tag
				if (qName.equalsIgnoreCase(introTag)) {
					intro = s;
				} else if (qName.equalsIgnoreCase(nameTag)) {
					name = s;
				} else if (qName.equalsIgnoreCase(typeTag)) {
					type = Step.parseStepType(s); // code is in Step
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
				reset(inStep);
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) {
			buffer.append(new String(ch, start, length));
		}

		private void reset(boolean inEx) {
			buffer = new StringBuffer();
			inStep = inEx;
		}

	}

}
