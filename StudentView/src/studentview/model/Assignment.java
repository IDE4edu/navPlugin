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

public class Assignment {

	String name;
	String intro = "";
	Vector<Step> exercises;

	Group group;

	public Assignment(String intro, String name, Vector<Step> exercises) {
		this.intro = intro;
		this.exercises = exercises;
		this.name = name;
	}

	public void prepend(String projectName) {
		for (Step e : exercises)
			e.prepend(projectName);
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
		IPath path = isa.getFullPath();
		URI uri = isa.getLocationURI();
		File file = new File(uri);
		Handler handler;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			handler = new Handler();
			saxParser.parse(file, handler);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return null;
		}
		Assignment s = handler.getSegment();
		s.prepend(path.segment(0));
		return handler.getSegment();
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
		public static final String introTag = "intro";
		public static final String exerciseTag = "exercise";
		public static final String nameTag = "name";
		public static final String sourceTag = "source";
		public static final String testTag = "test";
		public static final String typeTag = "type";

		@Override
		public void startDocument() throws SAXException {
		}

		@Override
		public void endDocument() throws SAXException {
			seg = new Assignment(isaIntro, isaName, exercises);
		}

		private Assignment seg;

		public Assignment getSegment() {
			return seg;
		}

		String isaIntro = "";
		String isaName = "";

		boolean inExercise = false;

		String name = "";
		String intro = "";
		String source = "";
		String test = "";
		ExerciseType type = ExerciseType.CODE;

		StringBuffer buffer = new StringBuffer();
		Vector<Step> exercises = new Vector<Step>();

		@Override
		public void startElement(String namespaceURI, String localName,
				String qName, Attributes atts) throws SAXException {
			if (qName.equalsIgnoreCase(exerciseTag))
				inExercise = true;
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			String s = buffer.toString().trim();
			if (qName.equalsIgnoreCase(exerciseTag)) {
				exercises.add(new Step(name, source, type, test, intro));
				resetStrings();
				reset(false);
			} else {
				if (qName.equals(introTag)) {
					if (inExercise) {
						intro = s;
					} else {
						isaIntro = s;
					}
				} else if (qName.equals(nameTag)) {
					if (inExercise) {
						name = s;
					} else {
						isaName = s;
					}
				} else if (qName.equals(sourceTag)) {
					source = s;
				} else if (qName.equals(testTag)) {
					test = s;
				} else if (qName.equals(typeTag)) {
					if (s.equals("html")) {
						type = ExerciseType.HTML;
					} if (s.equals("code")) { // s = "code"
						type = ExerciseType.CODE;
					} else {
						type = ExerciseType.SELFTEST;
					}
				} else if (qName.equals(isaTag)) {
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

		private void resetStrings() {
			name = "";
			intro = "";
			source = "";
			test = "";
			type = ExerciseType.CODE;
		}

	}

}
