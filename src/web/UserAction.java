package web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

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
}
