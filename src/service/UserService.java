package service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import Encryption.CommonFileManager;
import Encryption.ProxyDef;
import SecretCloudProxy.Ciphertext;
import SecretCloudProxy.ShareCipher;
import domain.FileVo;
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
	
	public boolean saveFile(String id, String fileName, JSONObject o) {
		byte[] cipher;
		ShareCipher DEScipher;
		Ciphertext condition;
		try {
			cipher = (byte[])CommonFileManager.bytesToObject((o.getBytes("cipher")));
			DEScipher = (ShareCipher)CommonFileManager.bytesToObject((o.getBytes("DEScipher")));
			condition = (Ciphertext)CommonFileManager.bytesToObject((o.getBytes("condition")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		String filePath = ProxyDef.getCiphersPath(id, fileName);
		String DESkeyPath = ProxyDef.getDESkeyPath(id, fileName);
		String conditionPath = ProxyDef.getConditionPath(id, fileName);
		try {     //将各文件存入数据库(即文件夹)
			CommonFileManager.writeObjectToFile(cipher, filePath);
			CommonFileManager.writeObjectToFile(DEScipher, DESkeyPath);
			CommonFileManager.writeObjectToFile(condition, conditionPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		File file = new File(filePath);
		String size = String.valueOf(file.length());
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
	
	public List<FileVo> sharedFilesForUser(String id) {
		return userDao.findSharedFilesForUser(id);
	}
}
