package web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import Encryption.CommonFileManager;
import domain.FileVo;
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
	
	@RequestMapping(value = "/getSharedFileForUser.htm")
	@ResponseBody
	public byte[] getSharedFileForUser(HttpServletRequest request) {
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
}
