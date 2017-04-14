package startup;

import java.util.HashMap;

public class TwinCloudController {
	
	DropboxController dbController = null;
	GoogleDriveController gcController = null;
	AESFileEncryption eController = null;
	AESFileDecryption dController = null;
	
	public TwinCloudController(){
		initialize();
	}

	public void initialize(){
		try{
			dbController = new DropboxController();
			gcController = new GoogleDriveController();
			eController = new AESFileEncryption();
			dController = new AESFileDecryption();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void upload(String absoluteFileName) {
		try{
			String fileName = absoluteFileName.substring(absoluteFileName.lastIndexOf("/")+1,absoluteFileName.lastIndexOf("."));
			boolean isEncryptionSuccess = eController.encryptFile(absoluteFileName, fileName+".des", fileName+"_salt.enc", fileName+"_iv.enc");
			if(isEncryptionSuccess){
				dbController.upload(fileName+".des");
				gcController.upload(fileName+"_salt.enc", fileName+"_iv.enc");
			}else{
				System.out.println("Map is empty after encryption");
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void download(String fileName){
		try{
			//dbController.download(fileName+".des");
			gcController.download(fileName+"_salt.enc", fileName+"_iv.enc");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
