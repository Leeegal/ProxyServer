package web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import Encryption.CommonFileManager;
import Encryption.ProxyDef;
import Encryption.ReencryptTask;
import Encryption.encryptionModule;
import SecretCloudProxy.Ciphertext;
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
		boolean status = true;

		String id = request.getHeader("id");
		String fileName = request.getHeader("fileName");
		try {
			id = URLDecoder.decode(id, "utf-8");
			fileName = URLDecoder.decode(fileName, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			status = false;
		}
		if (id != null && fileName != null && status && ServletFileUpload.isMultipartContent(request)) { // 检测是不是存在上传文件
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(1024 * 1024); // 指定在内存中缓存数据大小,单位为byte,这里设为1Mb
			String tempPath = ProxyDef.tempPath;
			File tempFile = new File(tempPath);
			if (!tempFile.exists()) {
				tempFile.mkdirs();
			}
			factory.setRepository(new File(tempPath)); // 设置一旦文件大小超过getSizeThreshold()的值时数据存放在硬盘的目录
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setFileSizeMax(50 * 1024 * 1024); // 指定单个上传文件的最大尺寸,单位:字节，这里设为50Mb
			upload.setSizeMax(50 * 1024 * 1024); // 指定一次上传多个文件的总尺寸,单位:字节，这里设为50Mb
			List<FileItem> items = null;
			try {
				items = upload.parseRequest(request); // 解析request请求
			} catch (FileUploadException e) {
				e.printStackTrace();
				status = false;
			}
			if (items != null) {
				Iterator<FileItem> iter = items.iterator(); // 解析表单项目
				while (iter.hasNext()) {
					FileItem item = iter.next();
					if (item.isFormField()) { // 如果是普通表单属性
						String name = item.getFieldName();
						if (name.equals("id")) {
							id = item.getString();
						} else if (name.equals("fileName")) {
							fileName = item.getString();
						}
					} else { // 如果是上传文件
						String name = item.getFieldName(); // 获得上传文件的文件名
						if (name.equals("cipher")) {
							try {
								item.write(new File(ProxyDef.getCiphersPath(id, fileName)));
							} catch (Exception e) {
								e.printStackTrace();
								status = false;
							}
						} else if (name.equals("DEScipher")) {
							try {
								item.write(new File(ProxyDef.getDESkeyPath(id, fileName)));
							} catch (Exception e) {
								e.printStackTrace();
								status = false;
							}
						} else if (name.equals("condition")) {
							try {
								item.write(new File(ProxyDef.getConditionPath(id, fileName)));
							} catch (Exception e) {
								e.printStackTrace();
								status = false;
							}
						}
					}
				}
			}
		}
		if (status && id != null && fileName != null) {
			if (userService.insertFile(id, fileName)) {
				resMap.put("error_no", "0");
				resMap.put("error_info", "上传成功");
			}
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
			for (int i = 0; i < failList.size(); i++) {
				failString = failString + failList.get(i) + ";";
			}
			resMap.put("error_info", "部分重加密密钥上传失败,名单如下" + failString);
		}

		jsonString = JSON.toJSONString(resMap);
		return jsonString.getBytes();
	}

	@RequestMapping(value = "/downloadReencryptionCipher.htm")
	@ResponseBody
	public void downloadReencryptionCipher(HttpServletRequest request, HttpServletResponse response) {
		int BUFFER_SIZE = 4096;
		InputStream in = null;
		OutputStream out = null;
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/octet-stream");

		String receiver = request.getParameter("receiver");
		String fileName = request.getParameter("fileName");
		String author = request.getParameter("author");
		ShareCipher desCipher = userService.getDESCipher(author, fileName);
		ReencryptionKey rk = userService.getRk(author, receiver, fileName);

		encryptionModule module;
		try {
			module = new encryptionModule();
			ReencryptionCipher reencryptionCipher = ReencryptTask.reencryptMsg(module, desCipher, rk);
			File file = CommonFileManager.writeObjectToFile(reencryptionCipher,
					ProxyDef.tempPath + "reencryptionCipher.dat");
			response.setContentLength((int) file.length());
			response.setHeader("Accept-Ranges", "bytes");
			int readLength = 0;
			in = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE);
			out = new BufferedOutputStream(response.getOutputStream());
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((readLength = in.read(buffer)) > 0) {
				byte[] bytes = new byte[readLength];
				System.arraycopy(buffer, 0, bytes, 0, readLength);
				out.write(bytes);
			}
			out.flush();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			response.setStatus(-1);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		response.setStatus(0);
	}
}
//
//	@RequestMapping(value = "/testDownloadFile.htm")
//	@ResponseBody
//	public void testDownloadFile(HttpServletRequest request, HttpServletResponse response) {
//		int BUFFER_SIZE = 4096;
//		InputStream in = null;
//		OutputStream out = null;
//		try {
//			response.setCharacterEncoding("utf-8");
//			response.setContentType("application/octet-stream");
//			String downloadPath = "/Users/chencaixia/files/毕设/工作成果/方案设计/";
//			File file = new File(downloadPath + "/" + fileName);
//			response.setContentLength((int) file.length());
//			response.setHeader("Accept-Ranges", "bytes");
//			int readLength = 0;
//			in = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE);
//			out = new BufferedOutputStream(response.getOutputStream());
//			byte[] buffer = new byte[BUFFER_SIZE];
//			while ((readLength = in.read(buffer)) > 0) {
//				byte[] bytes = new byte[readLength];
//				System.arraycopy(buffer, 0, bytes, 0, readLength);
//				out.write(bytes);
//			}
//			out.flush();
//		} catch (Exception e) {
//			e.printStackTrace();
//			response.setStatus(-1);
//		} finally {
//			if (in != null) {
//				try {
//					in.close();
//				} catch (IOException e) {
//				}
//			}
//			if (out != null) {
//				try {
//					out.close();
//				} catch (IOException e) {
//				}
//			}
//		}
//		response.setStatus(0);
//	}
//}
