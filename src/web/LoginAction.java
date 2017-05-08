package web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

import service.UserService;

import domain.UserVo;

@Controller
public class LoginAction {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/logoff.htm")
	public ModelAndView logoff(HttpServletRequest request) {
		request.getSession().invalidate();
		return new ModelAndView("index");
	}

	@RequestMapping(value = "/loginCheck.htm")
	@ResponseBody
	public byte[] loginCheck(HttpServletRequest request, UserVo userVo) throws Exception {
		boolean isValidUser = userService.hasMatchUser(userVo.getId(), userVo.getPassword());
		String jsonString;
		Map<String, String> resMap = new HashMap<String, String>();
		if (!isValidUser) {
			resMap.put("error_no", "-1");
			resMap.put("error_info", "用户名或密码错误");
			jsonString = JSON.toJSONString(resMap);
			return jsonString.getBytes();
		} else {
			resMap.put("error_no", "0");
			resMap.put("error_info", "登录成功");
			jsonString = JSON.toJSONString(resMap);
			return jsonString.getBytes();
		}
	}

	@RequestMapping(value = "/registry.htm")
	@ResponseBody
	public byte[] registry(HttpServletRequest request, UserVo userVo) {
		String jsonString;
		boolean isSuccess = userService.registerUser(userVo);
		Map<String, String> resMap = new HashMap<String, String>();
		if (isSuccess) {
			resMap.put("error_no", "0");
			resMap.put("error_info", "注册成功");

			jsonString = JSON.toJSONString(resMap);
			return jsonString.getBytes();
		} else {
			resMap.put("error_no", "-1");
			resMap.put("error_info", "注册失败，用户名已存在");
			jsonString = JSON.toJSONString(resMap);
			return jsonString.getBytes();
		}
	}
}
