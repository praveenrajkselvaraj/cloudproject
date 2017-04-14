/**
 * 
 */
package startup;

import java.util.Scanner;

/**
 * @author praveenrajkselvaraj
 *
 */
public class Main {

	static TwinCloudController twController = new TwinCloudController();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			Scanner sc = new Scanner(System.in);
			System.out.println("Enter the full file name to be uploaded: ");
			String fileName = sc.nextLine();
			
			uploadFile(fileName);
			downloadFile(fileName);
			/*EncryptionController eController = new EncryptionController();
			eController.readFile(fileName);*/
			//eController.initialize();
			
			/*DropboxController dbController = new DropboxController();
			dbController.initialize();
			
			GoogleDriveController gdController = new GoogleDriveController();
			gdController.initialize();*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void downloadFile(String fileName) {
		
	}

	private static void uploadFile(String fileName) {
		twController.upload(fileName);
	}

}
