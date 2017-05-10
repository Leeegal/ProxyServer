package web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import Encryption.CommonFileManager;
import Encryption.ReencryptTask;
import Encryption.encryptionModule;
import SecretCloudProxy.Ciphertext;
import SecretCloudProxy.CommonDef;
import SecretCloudProxy.PublicKey;
import SecretCloudProxy.ReencryptionCipher;
import SecretCloudProxy.ReencryptionKey;
import SecretCloudProxy.ShareCipher;
import domain.FileVo;
import domain.RkVo;
import service.UserService;

@Controller
public class UserAction {
	@Autowired
	private UserService userService;

	@RequestMapping(value = "/uploadFile.htm")
	@ResponseBody
	public byte[] uploadFile(HttpServletRequest request) {
		String jsonString;
		Map<String, String> resMap = new HashMap<String, String>();

		String id = request.getParameter("id");
		String fileName = request.getParameter("fileName");
		String file = request.getParameter("file");
		JSONObject o = JSONObject.parseObject(file);
		if (userService.saveFile(id, fileName, o)) {
			resMap.put("error_no", "0");
			resMap.put("error_info", "上传成功");
		} else {
			resMap.put("error_no", "-1");
			resMap.put("error_info", "上传失败");
		}

		jsonString = JSON.toJSONString(resMap);
		return jsonString.getBytes();
	}

	@RequestMapping(value = "/allFilesForUser.htm")
	@ResponseBody
	public byte[] allFilesForUser(HttpServletRequest request) {
		String jsonString;
		Map<String, byte[]> resMap = new HashMap<String, byte[]>();

		String id = request.getParameter("id");
		List<FileVo> list = userService.allFilesForUser(id);
		List<Map<String, String>> fileList = new ArrayList<Map<String, String>>();
		FileVo file;
		Map<String, String> fileMap;
		for (int i = 0; i < list.size(); i++) {
			file = list.get(i);
			fileMap = new HashMap<String, String>();
			fileMap.put("fileName", file.getFileName());
			fileMap.put("size", file.getSize());
			fileMap.put("date", file.getDate());
			fileList.add(fileMap);
		}
		try {
			resMap.put("fileList", CommonFileManager.objectToByteArray(fileList));
			resMap.put("error_no", "0".getBytes());
			resMap.put("error_info", "成功获取用户云盘文件列表".getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resMap.put("error_no", "-1".getBytes());
			resMap.put("error_info", "获取用户云盘文件列表失败".getBytes());
		}

		jsonString = JSON.toJSONString(resMap);
		return jsonString.getBytes();
	}
	
	@RequestMapping(value = "/getSharedFile.htm")
	@ResponseBody
	public byte[] getSharedFile(HttpServletRequest request) {
		String jsonString;
		Map<String, byte[]> resMap = new HashMap<String, byte[]>();

		String id = request.getParameter("id");
		List<RkVo> list = userService.sharedFilesForUser(id);
		List<Map<String, String>> fileList = new ArrayList<Map<String, String>>();
		RkVo file;
		Map<String, String> fileMap;
		for (int i = 0; i < list.size(); i++) {
			file = list.get(i);
			fileMap = new HashMap<String, String>();
			fileMap.put("receiver", file.getReceiver());
			fileMap.put("fileName", file.getFileName());
			fileMap.put("size", file.getSize());
			fileMap.put("date", file.getDate());
			fileList.add(fileMap);
		}
		try {
			resMap.put("fileList", CommonFileManager.objectToByteArray(fileList));
			resMap.put("error_no", "0".getBytes());
			resMap.put("error_info", "成功获取已分享文件列表".getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resMap.put("error_no", "-1".getBytes());
			resMap.put("error_info", "获取已分享文件列表失败".getBytes());
		}

		jsonString = JSON.toJSONString(resMap);
		return jsonString.getBytes();
	}

	@RequestMapping(value = "/getReceiveFile.htm")
	@ResponseBody
	public byte[] getReceiveFile(HttpServletRequest request) {
		String jsonString;
		Map<String, byte[]> resMap = new HashMap<String, byte[]>();

		String id = request.getParameter("id");
		List<RkVo> list = userService.receiveFilesForUser(id);
		List<Map<String, String>> fileList = new ArrayList<Map<String, String>>();
		RkVo file;
		Map<String, String> fileMap;
		for (int i = 0; i < list.size(); i++) {
			file = list.get(i);
			fileMap = new HashMap<String, String>();
			fileMap.put("author", file.getAuthor());
			fileMap.put("fileName", file.getFileName());
			fileMap.put("size", file.getSize());
			fileMap.put("date", file.getDate());
			fileList.add(fileMap);
		}
		try {
			resMap.put("fileList", CommonFileManager.objectToByteArray(fileList));
			resMap.put("error_no", "0".getBytes());
			resMap.put("error_info", "成功获取收到分享文件列表".getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resMap.put("error_no", "-1".getBytes());
			resMap.put("error_info", "获取收到分享文件列表失败".getBytes());
		}

		jsonString = JSON.toJSONString(resMap);
		return jsonString.getBytes();
	}

	@RequestMapping(value = "/getParamsForReencryptionKey.htm")
	@ResponseBody
	public byte[] getParamsForReencryptionKey(HttpServletRequest request) {
		String jsonString;
		Map<String, byte[]> resMap = new HashMap<String, byte[]>();

		String id = request.getParameter("id");
		String fileName = request.getParameter("fileName");
		byte[] grt = userService.getgrt(id, fileName);
		Ciphertext condition = userService.getCondition(id, fileName);

		try {
			resMap.put("error_no", "0".getBytes());
			resMap.put("error_info", "成功获取grt和条件值".getBytes());
			resMap.put("grt", grt);
			resMap.put("condition", CommonFileManager.objectToByteArray(condition));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resMap.put("error_no", "-1".getBytes());
			resMap.put("error_info", "获取grt和条件值失败".getBytes());
		}

		jsonString = JSON.toJSONString(resMap);
		return jsonString.getBytes();
	}

	@RequestMapping(value = "/uploadReencryptionKey.htm")
	@ResponseBody
	public byte[] uploadReencryptionKey(HttpServletRequest request) {
		String jsonString;
		Map<String, String> resMap = new HashMap<String, String>();

		String id = request.getParameter("id");
		String fileName = request.getParameter("fileName");
		String data = request.getParameter("data");
		JSONObject o = JSONObject.parseObject(data);
		List<String> failList = userService.saveReencryptionKey(id, fileName, o);
		if (failList == null) {
			resMap.put("error_no", "-1");
			resMap.put("error_info", "重加密密钥上传失败");
		} else if (failList.size() == 0) {
			resMap.put("error_no", "0");
			resMap.put("error_info", "成功上传重加密密钥");
		} else if (failList.size() > 0) {
			resMap.put("error_no", "1");
			
			String failString = new String();
			for(int i = 0; i < failList.size(); i++) {
				failString = failString + failList.get(i) + ";";
			}
			resMap.put("error_info", "部分重加密密钥上传失败,名单如下" + failString);
		}

		jsonString = JSON.toJSONString(resMap);
		return jsonString.getBytes();
	}
	
	@RequestMapping(value = "/downloadShareFile.htm")
	@ResponseBody
	public byte[] downloadShareFile(HttpServletRequest request) {
		String jsonString;
		Map<String, byte[]> resMap = new HashMap<String, byte[]>();

		String receiver = request.getParameter("receiver");
		String fileName = request.getParameter("fileName");
		String author = request.getParameter("author");
		byte[] cipher = userService.getCipher(author, fileName);   //原文密文
		ShareCipher desCipher = userService.getDESCipher(author, fileName);
		ReencryptionKey rk = userService.getRk(author, receiver, fileName);
		
		encryptionModule module;
		try {
			module = new encryptionModule();
			ReencryptionCipher reencryptionCipher = ReencryptTask.reencryptMsg(module, desCipher, rk);
			resMap.put("error_no", "0".getBytes());
			resMap.put("error_info", "成功重加密".getBytes());
			resMap.put("cipher", CommonFileManager.objectToByteArray(cipher));
			resMap.put("reencryptionCipher", CommonFileManager.objectToByteArray(reencryptionCipher));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			resMap.put("error_no", "-1".getBytes());
			resMap.put("error_info", "重加密失败".getBytes());
		}

		jsonString = JSON.toJSONString(resMap);
		return jsonString.getBytes();
	}
}
