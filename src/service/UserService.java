package service;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import Encryption.ProxyDef;
import SecretCloudProxy.CommonDef;
import SecretCloudProxy.CommonFileManager;
import SecretCloudProxy.Ciphertext;
import SecretCloudProxy.ReencryptionKey;
import SecretCloudProxy.ShareCipher;
import domain.FileVo;
import domain.RkVo;
import domain.UserVo;

import dao.UserDao;

@Service
public class UserService {
	@Autowired
    private UserDao userDao;
	
	public boolean hasMatchUser(String id, String password) {
        return userDao.getMatchCount(id, password) > 0;
    }
	public UserVo findUserByUserName(String id) {
        return userDao.findUserById(id);
    }
	public boolean registerUser(UserVo userVo) {
		return userDao.registerUser(userVo) > 0;
	}
	public List<UserVo> allUsers() {
		return userDao.allUsers();
	}
	public void delUser(String id) {
		userDao.delUser(id);
	}
	public void toUser(String id) {
		userDao.toUser(id);
	}
	public void toAdmin(String id) {
		userDao.toAdmin(id);
	}
	
	public boolean insertFile(String id, String fileName) {
		String filePath = ProxyDef.getCiphersPath(id, fileName);
		String DESkeyPath = ProxyDef.getDESkeyPath(id, fileName);
		String conditionPath = ProxyDef.getConditionPath(id, fileName);
		File file = new File(filePath);
		String size = FormetFileSize(file.length());
		
		Date date = new Date();			//获得系统时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		String nowTime = sdf.format(date);   //转换成符合要求的格式
		
		FileVo fileVo = new FileVo();
		fileVo.setId(id);
		fileVo.setFileName(fileName);
		fileVo.setSize(size);
		fileVo.setDate(nowTime);
		fileVo.setPath(filePath);
		fileVo.setDesPath(DESkeyPath);
		fileVo.setConditionPath(conditionPath);
		
		if(!userDao.insertFile(fileVo)) {
			return false;
		}
		return true;
	}
	
	public List<FileVo> allFilesForUser(String id) {
		return userDao.findAllFilesForUser(id);
	}
	
	public List<RkVo> sharedFilesForUser(String id) {
		return userDao.findSharedFilesForUser(id);
	}
	
	public List<RkVo> receiveFilesForUser(String id) {
		return userDao.findReceiveFilesForUser(id);
	}
	
	public ShareCipher getDESCipher(String id, String fileName) {
		ShareCipher desCipher = null;
		//获取des密文路径
		Map<String, Object> map = userDao.findDESCipherPath(id, fileName);
		String desPath;
		if(map.size() > 0 && map.containsKey("desPath")) {
			desPath = (String)map.get("desPath");
			try {
				desCipher = (ShareCipher)CommonFileManager.readObjectFromFile(desPath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return desCipher;
	}
	
	public File getCipher(String id, String fileName) {
		File cipher = null;
		//获取原始密文路径
		Map<String, Object> map = userDao.findCipherPath(id, fileName);
		String path;
		if(map.size() > 0 && map.containsKey("path")) {
			path = (String)map.get("path");
			try {
				cipher = new File(path);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return cipher;
	}
	
	public byte[] getgrt(String id, String fileName) {
		byte[] grt = null;
		//获取des密文路径
		Map<String, Object> map = userDao.findDESCipherPath(id, fileName);
		String desPath;
		if(map.size() > 0 && map.containsKey("desPath")) {
			desPath = (String)map.get("desPath");
			try {
				ShareCipher desCipher = (ShareCipher)CommonFileManager.readObjectFromFile(desPath);
				grt = desCipher.getgrt();   //获取grt
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return grt;
	}
	
	public Ciphertext getCondition(String id, String fileName) {
		Ciphertext condition = null;
		//获取des密文
		Map<String, Object> map = userDao.findConditionPath(id, fileName);
		String conditionPath;
		if(map.size() > 0 && map.containsKey("conditionPath")) {
			conditionPath = (String)map.get("conditionPath");
			try {
				condition = (Ciphertext)CommonFileManager.readObjectFromFile(conditionPath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return condition;
	}
	
	public ReencryptionKey getRk(String author, String receiver, String fileName) {
		ReencryptionKey rk = null;
		//获取rk路径
		Map<String, Object> map = userDao.findRkPath(author, receiver, fileName);
		String rkPath;
		if(map.size() > 0 && map.containsKey("rkPath")) {
			rkPath = (String)map.get("rkPath");
			try {
				rk = (ReencryptionKey)CommonFileManager.readObjectFromFile(rkPath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rk;
	}
	
	//返回-1表示出错，0表示成功，1表示部分成功
	public List<String> saveReencryptionKey(String id, String fileName, JSONObject o) {
		String[] receivers;
		List<String> failList = new ArrayList<String>();;
		Map<String, byte[]> rkMap;
		try {
			receivers = (String[])CommonFileManager.bytesToObject((o.getBytes("receivers")));
			rkMap = (Map<String, byte[]>)CommonFileManager.bytesToObject((o.getBytes("rkMap")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		if(rkMap.isEmpty() || rkMap.size() != receivers.length) {
			return null;
		}
		
		for(int i = 0; i < rkMap.size(); i++) {
			String rkName = CommonDef.reencryptionKeyAffix(id, receivers[i], fileName);
			byte[] rkByte = rkMap.get(rkName);
			ReencryptionKey rk;
			try {
				rk = (ReencryptionKey)CommonFileManager.bytesToObject(rkByte);
				System.out.println();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				failList.add(receivers[i]);
				continue;
			}
			String rkPath = ProxyDef.getReencryptionKeyPath(rkName);
			try {     //将各文件存入数据库(即文件夹)
				CommonFileManager.writeObjectToFile(rk, rkPath);
				Date date = new Date();			//获得系统时间
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
				String nowTime = sdf.format(date);   //转换成符合要求的格式
				
				RkVo rkVo = userDao.findInfoForRkVo(id, fileName);
				if(rkVo == null) {
					failList.add(receivers[i]);
					continue;
				}
				rkVo.setAuthor(id);
				rkVo.setFileName(fileName);
				rkVo.setReceiver(receivers[i]);
				rkVo.setDate(nowTime);
				rkVo.setrkPath(rkPath);
				
				userDao.insertRk(rkVo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(receivers[i] + "的重加密密钥保存失败");
				failList.add(receivers[i]);
				continue;
			}
		}
		return failList;
	}
	
	private String FormetFileSize(long fileS) {//转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS == 0){
            return fileSizeString;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) +"G";
        }
        return fileSizeString;
    }
}
