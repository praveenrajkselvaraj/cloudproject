/**
 * 
 */
package startup;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CreateFolderErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.GetMetadataErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.LookupError;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

/**
 * @author praveenrajkselvaraj
 *
 */
public class DropboxController {
	
	public void upload(String fileName){
		// Get your app key and secret from the Dropbox developers website.
        final String APP_KEY = "7c3txg8eevl7s7f";
        final String APP_SECRET = "qhqmvvqejbq8ath";

        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
        
        // Run through Dropbox API authorization process
        DbxRequestConfig requestConfig = new DbxRequestConfig("twin-cloud");
        DbxWebAuth webAuth = new DbxWebAuth(requestConfig, appInfo);
        DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder().withNoRedirect().build();

        String authorizeUrl = webAuth.authorize(webAuthRequest);
        System.out.println("1. Go to " + authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first).");
        System.out.println("3. Copy the authorization code.");
        System.out.print("Enter the authorization code here: ");

        String code;
		try {
			code = new BufferedReader(new InputStreamReader(System.in)).readLine();
			if (code == null) {
	            System.exit(1); return;
	        }
	        code = code.trim();
	        
	        DbxAuthFinish authFinish = webAuth.finishFromCode(code);

	        System.out.println("Authorization complete.");
	        System.out.println("- User ID: " + authFinish.getUserId());
	        System.out.println("- Access Token: " + authFinish.getAccessToken());
	        
	        DbxClientV2 client = new DbxClientV2(requestConfig, authFinish.getAccessToken());
	        FullAccount account = client.users().getCurrentAccount();
	        System.out.println(account.getName().getDisplayName());
	        
	        try{
		        Metadata folderMetadata = client.files().getMetadata("/twin-cloud");
		        System.out.println("Folder Display Path:"+ folderMetadata.getPathDisplay());
	        }
	        catch (GetMetadataErrorException e){
                if (e.errorValue.isPath()){
                    LookupError le = e.errorValue.getPathValue();
                    if (le.isNotFound()){
                        System.out.println("Path doesn't exist on Dropbox: ");
                        try{
                        	client.files().createFolder("/twin-cloud");
                        	System.out.println("Folder created on Dropbox: ");
                        }
                        catch (CreateFolderErrorException e1){
                            e1.printStackTrace();
                        }catch (DbxException e1){
                            e1.printStackTrace();
                        }
                    }
                }
            }
	        
	        /*// Get files and folder metadata from Dropbox root directory
	        ListFolderResult result = client.files().listFolder("/twin-cloud");
	        while (true) {
	            for (Metadata metadata : result.getEntries()) {
	                System.out.println(metadata.getPathLower());
	            }

	            if (!result.getHasMore()) {
	                break;
	            }

	            result = client.files().listFolderContinue(result.getCursor());
	        }*/

	        // Upload "test.txt" to Dropbox
	        try (InputStream in = new FileInputStream(fileName)) {
	            FileMetadata metadata = client.files().uploadBuilder("/twin-cloud/"+fileName).uploadAndFinish(in);
	        }

        } catch (DbxException ex) {
            System.err.println("Error in DbxWebAuth.authorize: " + ex.getMessage());
            System.exit(1); return;
            
		} catch (IOException e) {
			System.err.println("IO Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
