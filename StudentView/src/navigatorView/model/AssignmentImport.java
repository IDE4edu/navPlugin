package navigatorView.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import navigatorView.NavigatorActivator;

import org.apache.commons.io.FileUtils;
import org.eclipse.ui.dialogs.IOverwriteQuery;

public class AssignmentImport {

	private String name;
	private URL url;
	private Date date;
	
	
	public AssignmentImport(String name, URL url, Date date) {
		this.name = name;
		this.url = url;
		this.date = date;
	}
	
	
	public String getName() {
		return name;
	}
	
//	public URL getURL() {
//		return url;
//	}
//	
//	public Date getDate() {
//		return date;
//	}


	
	
	static private String DEFAULT_ROOT_NAME = "EduRide Imports";
	static private File root = null;

	static public File getImportRoot() {
		if (root == null) {
			String tmp = System.getProperty("java.io.tmpdir");
			File newroot = new File(tmp, DEFAULT_ROOT_NAME + File.separator);
			if (newroot.exists() || newroot.mkdir()) {
				// it might be a file, not a directory.   ug.
				root = newroot;
				return root;
			} else {
				String msg = "Couldn't make root import directory -- permissions? : "
						+ newroot.toString();
				System.err.println(msg);
				NavigatorActivator.getDefault().log("installFailure", msg);
				return null;
			}
		} else {
			return root;
		}
	}

	static public void emptyImportRoot() {
		try {
			if (root != null) {
				cleanDirectory(root);
			} else {
				getImportRoot();
				cleanDirectory(root);
			}
		} catch (IOException e) {
			String msg = "Couldn't delete files in import directory -- permissions? : "
					+ root.toString();
			System.err.println(msg);
			NavigatorActivator.getDefault().log("installFailure", msg);
			e.printStackTrace();
		}
	}
	
	public static boolean cleanDirectory(File path) throws FileNotFoundException {
        if (!path.exists()) throw new FileNotFoundException(path.getAbsolutePath());
        boolean ret = true;
        if (path.isDirectory()){
            for (File f : path.listFiles()){
                ret = ret && deleteFiles(f);
            }
        }
        return ret;
    }
	
    public static boolean deleteFiles(File path) throws FileNotFoundException {
        if (!path.exists()) throw new FileNotFoundException(path.getAbsolutePath());
        boolean ret = true;
        if (path.isDirectory()){
            for (File f : path.listFiles()){
                ret = ret && deleteFiles(f);
            }
        }
        return ret && path.delete();
    }
	
	public void importMe() {

		try {
			// going to let the Wizard handle this, yo
//			String projectName = getName();
//			IWorkspace workspace = ResourcesPlugin.getWorkspace();
//			IProjectDescription newProjectDescription = workspace
//					.newProjectDescription(projectName);
//			IProject newProject = workspace.getRoot().getProject(projectName);
//			newProject.create(newProjectDescription, null);
//			newProject.open(null);

			
			IOverwriteQuery overwriteQuery = new IOverwriteQuery() {
				public String queryOverwrite(String file) {
					return ALL;
				}
			};
			
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			InputStream in = connection.getInputStream();
			
			
			// files on disk
			UUID tempname = UUID.randomUUID();
			File tempfile = File.createTempFile(tempname.toString(), ".zip");
			FileOutputStream out = new FileOutputStream(tempfile);
			copy(in, out, 1024);
			ZipFile zipFile = new ZipFile(tempfile);
			
			importZipFiles(zipFile, getImportRoot());
	
			
			tempfile.delete();

		} catch (IOException e) {
			String msg = "uh oh, exception while getting zipfile url, etc? : " + url.toString();
			System.err.println(msg);
			NavigatorActivator.getDefault().log("installFailure", msg);
			e.printStackTrace();
		}
	}
	
	
	
	private static void copy(InputStream input, OutputStream output,
			int bufferSize) throws IOException {
		byte[] buf = new byte[bufferSize];
		int n = input.read(buf);
		while (n >= 0) {
			output.write(buf, 0, n);
			n = input.read(buf);
		}
		output.flush();
	}
	
	
	// tweaked from http://www.java-examples.com/extract-zip-file-subdirectories-using-command-line-argument-example
	public static void importZipFiles(ZipFile zipFile, File zipPath) {
		try {
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			while (e.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				File destinationFilePath = new File(zipPath, entry.getName());
				// create directories if required.
				destinationFilePath.getParentFile().mkdirs();
				if (entry.isDirectory()) {
					continue;
				} else {
					// System.out.println("Extracting " + destinationFilePath);

					/*
					 * Get the InputStream for current entry of the zip file
					 * using
					 * 
					 * InputStream getInputStream(Entry entry) method.
					 */
					BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));

					int b;
					byte buffer[] = new byte[1024];

					/*
					 * read the current entry from the zip file, extract it and
					 * write the extracted file.
					 */
					FileOutputStream fos = new FileOutputStream(destinationFilePath);
					BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);

					while ((b = bis.read(buffer, 0, 1024)) != -1) {
						bos.write(buffer, 0, b);
					}

					// flush the output stream and close it.
					bos.flush();
					bos.close();
					// close the input stream.
					bis.close();
				}
			}

		} catch (IOException e) {
			String msg = "uh oh, exception while extracting files from zip? : " + zipFile.getName();
			System.err.println(msg);
			NavigatorActivator.getDefault().log("installFailure", msg);
			e.printStackTrace();

		}

	}

}
