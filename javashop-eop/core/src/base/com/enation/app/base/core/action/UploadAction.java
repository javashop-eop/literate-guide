
package com.enation.app.base.core.action;

import java.util.HashMap;
import java.util.Map;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

/**
 * 附件上传
 * 
 * @author kingapex 2010-3-10下午04:24:47
 * @author Kanon 2015-12-16 version 1.1 添加注释
 * @author Kanon 2016-3-1;version 6.0版本
 */
@Controller
@RequestMapping("/eop/upload")
public class UploadAction {

	
	/**
	 * 上传图片
	 * @return 跳转至上传图片的页面
	 */
	@RequestMapping(value="/file")
	public ModelAndView execute(String subFolder,String createThumb,Integer width,Integer height) {
		ModelAndView view =new ModelAndView();
		
		view.addObject("createThumb", createThumb);
		view.addObject("width", width);
		view.addObject("height", height);
		view.addObject("subFolder", subFolder);
		view.setViewName("/core/admin/upload/upload_file");
		return view;
	}
	
	/**
	 * 上传附件页面   冯兴隆 2015-07-28
	 * @return
	 */
	@RequestMapping(value="file-ui")
	public String fileUI(){
		return "/core/admin/upload/upload_file";
	}
	/**
	 * 上传附件
	 * @param file 附件
	 * @param fileFileName 附件名称
	 * @param subFolder 附件存放文件夹
	 * @param path 上传后的图片路径
	 * @param ajax 是否为异步提交
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/upload-file")
	public Object uploadFile(MultipartFile file,String subFolder,Integer ajax){
		 String path=null;
		if (file != null && file.getOriginalFilename() != null) {
			try{
				path = UploadUtil.upload(file, subFolder);
			}catch(IllegalArgumentException e){
				
				return JsonResultUtil.getErrorJson(e.getMessage());
			}
			// 将本地附件路径换为静态资源服务器的地址
			if (path != null){
				path = UploadUtil.replacePath(path);
			}
			Map map=new HashMap();

			if (ajax == 1) {
				map.put("result", 1);
				map.put("path", path);
				map.put("filename",  file.getOriginalFilename());
			}
			map.put("message","上传成功");
			return map;
		}else{
			return JsonResultUtil.getErrorJson("没有文件");
		}
	}
	
	/**
	 * 上传图片
	 * @param file 附件
	 * @param fileFileName 附件名称
	 * @param createThumb 是否生成缩略图
	 * @param subFolder 上传文件夹
	 * @param width 缩略图宽度
	 * @param height 缩略图高度
	 * @param path 上传后本地路径
	 * @param ajax 是否为异步上传
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/upload")
	public Object upload(int createThumb,String subFolder,Integer ajax,MultipartFile file,Integer width,Integer height) {
		try{
			 String path=null;
			if (file != null && file.getOriginalFilename() != null) {
				try{
					if (subFolder == null) {
						subFolder = "";
					}
					if (createThumb == 1) {
						path = UploadUtil.upload(file, subFolder, width, height)[0];
					} else {
						path = UploadUtil.upload(file, subFolder);
					}
				}catch(IllegalArgumentException e){
					return JsonResultUtil.getErrorJson(e.getMessage());
				}
				// 将本地图片路径换为静态资源服务器的地址
				if (path != null)
					path = UploadUtil.replacePath(path);

				if (ajax == 1) {
					Map map= new HashMap();
					map.put("result", 1);
					map.put("path", path);
					map.put("thumbnail", UploadUtil.getThumbPath(path, "_thumbnail"));
					map.put("filename", file.getOriginalFilename());
					return map;
				}
			}
		} catch(RuntimeException e) {
			Logger logger = Logger.getLogger(getClass());
			logger.error("上传图片出错:"+e);
		}
		return JsonResultUtil.getErrorJson("请选择文件");
	}

	/**
	 * 删除图片
	 * 根据图片路径删除图片
	 * @param picname  图片路径
	 * @return 删除状态
	 */
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(String picname) {
		UploadUtil.deleteFile(picname);
		return JsonResultUtil.getErrorJson("");
	}


}
