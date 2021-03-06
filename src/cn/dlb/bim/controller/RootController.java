package cn.dlb.bim.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cn.dlb.bim.component.PlatformServer;
import cn.dlb.bim.dao.entity.OutputTemplate;
import cn.dlb.bim.ifc.emf.PackageMetaData;
import cn.dlb.bim.ifc.emf.Schema;
import cn.dlb.bim.ifc.stream.query.JsonQueryObjectModelConverter;
import cn.dlb.bim.ifc.stream.query.Query;
import cn.dlb.bim.ifc.stream.query.QueryException;
import cn.dlb.bim.service.BimService;
import cn.dlb.bim.service.ProjectService;
import cn.dlb.bim.web.ResultUtil;

@Controller
@RequestMapping("/")
public class RootController {

	private static final Logger LOGGER = LoggerFactory.getLogger(RootController.class);

	@Autowired
	private BimService bimService;
	
	@Autowired
	@Qualifier("ProjectServiceImpl")
	private ProjectService projectService;
	
	@Autowired
	@Qualifier("PlatformServer")
	private PlatformServer server;

	@RequestMapping("index")
	public String index() {
		return "index";
	}

	// 前端路由专用页面
	@RequestMapping("app")
	public String app() {
		return "app/index.jsp";
	}

	@RequestMapping(value = "jsonApi", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> jsonApi(@RequestBody ObjectNode jsonNode) {//DEMO
		
		ResultUtil result = new ResultUtil();
		PackageMetaData packageMetaData = server.getMetaDataManager()
				.getPackageMetaData(Schema.IFC2X3TC1.getEPackageName());
		JsonQueryObjectModelConverter converter = new JsonQueryObjectModelConverter(packageMetaData);
		Query query = null;
		try {
			query = converter.parseJson("query", jsonNode);
		} catch (QueryException e) {
			e.printStackTrace();
		}
		result.setSuccess(true);
		return result.getResult();
	}
	
	@RequestMapping(value = "test", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> test() {//test
		ResultUtil result = new ResultUtil();
		bimService.test();
		result.setSuccess(true);
		return result.getResult();
	} 
	
}