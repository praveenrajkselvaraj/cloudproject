/**
 * 
 */
package startup;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

/**
 * @author praveenrajkselvaraj
 *
 */
public class GoogleDriveController {
	
	public void initialize(){
		
	}
	 /** Application name. */
    private static final String APPLICATION_NAME = "TwinCloud";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File("C:/Praveenraj/DemoData/cred");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/drive-java-quickstart
     */
    private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws Exception 
     */
    public static Credential authorize() throws Exception {
        // Load client secrets.
        InputStream in = new FileInputStream(new java.io.File("C:/Users/radhi/Downloads/client_secret_766528828617-65a70tgi0c7prp2fco2ebeni86js9h1m.apps.googleusercontent.com.json"));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .build();
        
        LocalServerReceiver.Builder lsrBuilder = new LocalServerReceiver.Builder();
        lsrBuilder.setHost("localhost");
        lsrBuilder.setPort(8081);
        
        Credential credential = new AuthorizationCodeInstalledApp(flow, lsrBuilder.build()).authorize("user");
        System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws IOException
     */
    public static Drive getDriveService() throws IOException {
        Credential credential;
		try {
		
			credential = authorize();
			return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
	                .setApplicationName(APPLICATION_NAME)
	                .build();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }

    public void upload(String... uploadFileList) throws IOException{
    	File fileMetadata = new File();
    	File file;
    	BufferedWriter output = null;
		try {
			for(String fileName: uploadFileList){
		    	fileMetadata.setName(fileName);
		    	java.io.File filePath = new java.io.File(fileName);
	            FileContent mediaContent = new FileContent("*/*", filePath);
				file = getDriveService().files().create(fileMetadata, mediaContent).setFields("id").execute();
				System.out.println("File ID: " + file.getId());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if ( output != null ) {
	            output.close();
	        }
		}
    }
    
    public void findFile(String fileName){
    	try{
    		String pageToken = null;
    		do {
    		    FileList result = getDriveService().files().list().setQ("mimeType='*/*'").setSpaces("drive")
    		            .setFields("nextPageToken, files(id, name)").setPageToken(pageToken).execute();
    		    for(File file: result.getFiles()) {
    		        if(file.getName().equalsIgnoreCase(fileName)){
    		        	System.out.println("Found file: " + file.getName()+" "+ file.getId());
    		        }
    		    }
    		    pageToken = result.getNextPageToken();
    		} while (pageToken != null);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public String getFileId(String fileName){
    	try{
    		String pageToken = null;
    		do {
    		    FileList result = getDriveService().files().list().setQ("mimeType='*/*'").setSpaces("drive")
    		            .setFields("nextPageToken, files(id, name)").setPageToken(pageToken).execute();
    		    for(File file: result.getFiles()) {
    		        if(file.getName().equalsIgnoreCase(fileName)){
    		        	return file.getId();
    		        }
    		    }
    		    pageToken = result.getNextPageToken();
    		} while (pageToken != null);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
		return "";
    }
    
    public void download(String... fileNameList){
    	try{
    		for(String fileName: fileNameList){
    			String fileId = getFileId(fileName);
        		if(fileId.isEmpty()){
        			System.out.println("File Id not found for the mentioned filename:"+fileName);
        		}else{
        			System.out.println("Downloaded file:"+fileName+" with fileId:"+fileId);
            		OutputStream outputStream = new ByteArrayOutputStream();
            		getDriveService().files().get(fileId).executeMediaAndDownloadTo(outputStream);
            		outputStream.close();
        		}
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    public static void main(String[] args){
    	
    	GoogleDriveController gc = new GoogleDriveController();
    	gc.download("key_salt.enc");
    }
}
