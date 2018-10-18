package com.bjsxt.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bjsxt.mapper.FeelMapper;
import com.bjsxt.mapper.ImgMapper;
import com.bjsxt.pojo.Feel;
import com.bjsxt.pojo.Img;
import com.bjsxt.service.FeelService;
import com.bjsxt.utils.FtpUtil;
import com.bjsxt.utils.IDUtils;
@Service("feelService")
public class FeelServiceImpl implements FeelService {
	@Value("${ftpclient.host}")
	private String host;
	@Value("${ftpclient.port}")
	private int port;
	@Value("${ftpclient.username}")
	private String username;
	@Value("${ftpclient.password}")
	private String password;
	@Value("${ftpclient.basePath}")
	private String basePath;
	@Value("${ftpclient.filepath}")
	private String filePath;
	@Resource
	private FeelMapper feelMapper;
	@Resource
	private ImgMapper imgMapper;
	@Override
	public Map<String, Object> upload(MultipartFile imgFile) throws IOException {
		String fileName = UUID.randomUUID()+imgFile.getOriginalFilename().substring(imgFile.getOriginalFilename().lastIndexOf("."));
		boolean result = FtpUtil.uploadFile(host, port, username, password, basePath, filePath, fileName, imgFile.getInputStream());
		Map<String,Object> map = new HashMap<>();
		if(result){
			map.put("error", 0);
			map.put("url","http://"+host+"/"+fileName);
		}else{
			map.put("error", 1);
			map.put("message", "图片上传失败!!!");
		}
		return map;
	}

	@Override
	public int insert(Feel feel, List<String> imgs) {
		long id = IDUtils.genItemId();
		feel.setId(id);
		int index = feelMapper.insert(feel);
		if(index>0){
			for (String string : imgs) {
				Img img = new Img();
				img.setFid(id);
				img.setPath(string);
				index+=imgMapper.insertSelective(img);
			}
			if(index==imgs.size()+1){
				return 1;
			}
		}
		//img新增, 外键列  需要feel的id
		return 0;
	}

}
