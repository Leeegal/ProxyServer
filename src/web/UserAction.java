package web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import Encryption.CommonFileManager;
import SecretCloudProxy.Ciphertext;
import SecretCloudProxy.ShareCipher;

@Controller
public class UserAction {
	@RequestMapping(value = "/uploadFile.htm")
	@ResponseBody
	public byte[] uploadFile(HttpServletRequest request) {
		System.out.println("进入uploadFile");
		String jsonString;
		Map<String, String> resMap = new HashMap<String, String>();
		
		String id = request.getParameter("id");
		String file = request.getParameter("file");
		JSONObject o = JSONObject.parseObject(file);
		byte[] cipher = (byte[])CommonFileManager.bytesToObject((o.getBytes("cipher")));
		ShareCipher DEScipher = (ShareCipher)CommonFileManager.bytesToObject((o.getBytes("DEScipher")));
		Ciphertext condition = (Ciphertext)CommonFileManager.bytesToObject((o.getBytes("condition")));
		
		try {
			resMap.put("error_no", "0");
			resMap.put("error_info", "成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resMap.put("error_no", "-1");
			resMap.put("error_info", "失败");
		}
		
		jsonString = JSON.toJSONString(resMap);
		return jsonString.getBytes();
	}
}
