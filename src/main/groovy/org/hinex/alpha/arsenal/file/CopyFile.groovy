
/* #####################
 * Created by AlphaHinex
 * 2010-2-2
 * 
 */

package org.hinex.alpha.arsenal.file;

public class CopyFile {
	
	private CopyFile() {
	}
	
	/**
	 * Created on 2010.02.02
	 * copy file from srcpath to despath
	 * @param srcPath absolute path of source file
	 * @param desPath absolute path of destination file
	 * @throws IOException
	 */
	public static void copy(String srcPath,String desPath) throws IOException {
		copy(srcPath, desPath, false)
	}
	
	public static void copy(File from, File to) throws IOException {
		copy(from, to, false)
	}
	
	public static void copy(String srcPath, String desPath, boolean recursion) throws IOException {
		copy(new File(srcPath), new File(desPath), recursion)
	}
	
	public static void copy(File from, File to, boolean recursion) throws IOException {
		if(from.isDirectory()) {
			if(recursion) {
				from.listFiles().each {
					copy(it.getAbsolutePath(), it.getAbsolutePath().replace(from.getAbsolutePath(), to.getAbsolutePath()), recursion)
				}
			} else {
				return
			}
		} else {
			// if the destination path is directory then return
			if(to.isDirectory()) {
				return;
			} else if(!to.getParentFile().exists()) {
				// else if destination path is not exist then make it
				to.getParentFile().mkdirs();
			}
			
			OutputStream os = new FileOutputStream(to);
			InputStream is = new FileInputStream(from);
			byte[] buffer = new byte[4096];
			int len;
			while((len = is.read(buffer)) > 0) {
				os.write(buffer, 0, len);
			}
			os.flush();
			os.close();
			is.close();
			
			println "complete copy: ${from.getAbsolutePath()}"
		}
	}
	
}
