package studentview.testing;

//Andy Carle, Berkeley Institute of Design, UC Berkeley

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

import studentview.testing.Exercise.ExerciseType;

public class Segment {
	
	String name;
	String intro = "";
	Vector<Exercise> exercises;	
	
	Group group;
	
	public Segment(String intro, String name, Vector<Exercise> exercises){
		this.intro = intro;
		this.exercises = exercises;
		this.name = name;
		
		
		/*for (int i = 0; i < count; i++){
			exercises.add(new Exercise(gen.nextString(), gen.nextString(), ExerciseType.EDIT, gen.nextString(), TestResult.PASSED));
		}*/
		
		//Exercise e = new Exercise("isOK", "/Invariants.InsertionSort/src/InsertionSort.java", ExerciseType.EDIT, "/Invariants.InsertionSort/TEST.isort-1.launch", TestResult.PASSED);
		//exercises.add(e);
		
		//e = new Exercise("Sort Test", "/Invariants.InsertionSort/src/InsertionSort.java", ExerciseType.EDIT, "/Invariants.InsertionSort/TEST.isort-2.launch", TestResult.PASSED);
		//exercises.add(e);
	}
	
	public void prepend(String projectName){
		for (Exercise e : exercises) e.prepend(projectName);
	}
	
	public String getName(){
		return name;
	}
	
	public String getIntro(){
		return intro;
	}
	
	public void setGroup(Group group){
		this.group = group;
	}
	
	public Group getGroup(){
		return this.group;
	}
	
	
	public static Segment parseISA(IFile isa){
		IPath path = isa.getFullPath();
		URI uri = isa.getLocationURI();
		File file = new File(uri);
		Handler handler;
		try{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			handler = new Handler();
			saxParser.parse(file, handler);
		}catch(Exception e){
			e.printStackTrace(System.err);
			return null;
		}
		Segment s = handler.getSegment();
		s.prepend(path.segment(0));		
		return handler.getSegment(); 		
	}
	
	
	public Vector<Exercise> getExercises(){
		return exercises;
	}
	
	public Exercise getExercise(String name){
		for (Exercise e : exercises){
			if (e.getName().equals(name)) return e;
		}
		return null;
	}
	
	private static class Handler extends DefaultHandler{
		public static final String isaTag = "isa";
		public static final String introTag = "intro";
		public static final String exerciseTag = "exercise";
		public static final String nameTag = "name";
		public static final String sourceTag = "source";
		public static final String testTag = "test";
		public static final String typeTag = "type";
		
		
		
		@Override
		public void startDocument() throws SAXException{
			
		}
		
		@Override
		public void endDocument() throws SAXException{
			seg = new Segment(isaIntro, isaName, exercises);
		}
		
		private Segment seg;
		
		public Segment getSegment(){
			return seg;
		}
		
		String isaIntro = "";
		String isaName = "";
		
		boolean inExercise = false;
		boolean inName = false;
		boolean inIntro = false;
		boolean inSource = false;
		boolean inTest = false;
		boolean inType = false;
		
		String name = "";		
		String intro = "";
		String source = "";
		String test = "";
		ExerciseType type = ExerciseType.EDIT;
		 
		
		StringBuffer buffer = new StringBuffer();
		Vector<Exercise> exercises = new Vector<Exercise>();
		
		@Override
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException{
			if (qName.equalsIgnoreCase(exerciseTag)) inExercise = true;
			if (qName.equalsIgnoreCase(introTag)) inIntro = true;
			if (qName.equalsIgnoreCase(nameTag)) inName = true;
			if (qName.equalsIgnoreCase(sourceTag)) inSource = true;
			if (qName.equalsIgnoreCase(testTag)) inTest = true;			
			if (qName.equalsIgnoreCase(typeTag)) inType = true;
			//System.out.println("Starting element: " + qName);
		}
		
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException{			
			String s = buffer.toString().trim();
			//System.out.println("Ending element: " + qName + " String is: " + s);
			if (qName.equalsIgnoreCase(exerciseTag)){
				exercises.add(new Exercise(name, source, type, test, intro));
				resetStrings();
				reset(false);
			}else{
				switch (qName){
				case introTag:
					if (inExercise){
						intro = s;
					}else{
						isaIntro = s;
					}
					break;
				case nameTag:
					if (inExercise){
						name = s;
					}else{
						isaName = s;
					}
					break;
				case sourceTag:
					source = s;
					break;
				case testTag:
					test = s;
					break;
				case typeTag:
					switch (s){
					case "html":
						type = ExerciseType.HTML;
						break;
					case "code":
					default:
						type = ExerciseType.EDIT;
					}
					break;
				case isaTag:
					break;
				default:
					System.err.println("Bad tag in isa file: " + qName + " String is: " + s);
				}				
				reset(inExercise);
			}			
		}
		
		@Override
		public void characters(char[] ch, int start, int length){
			buffer.append(new String(ch, start, length));
		}
		
		private void reset(boolean inEx){
			buffer = new StringBuffer();
			inExercise = inEx;
			inName = false;
			inIntro = false;
			inSource = false;
			inTest = false;
			inType = false;			
		}
		
		private void resetStrings(){
			name = "";
			intro = "";
			source = "";
			test = "";
			type = ExerciseType.EDIT;
		}
		
	}
	

}
