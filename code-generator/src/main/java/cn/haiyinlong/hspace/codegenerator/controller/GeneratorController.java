package cn.haiyinlong.hspace.codegenerator.controller;

import cn.haiyinlong.hspace.codegenerator.service.GeneratorService;
import cn.haiyinlong.hspace.codegenerator.utils.GenUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/generator")
@RequiredArgsConstructor
public class GeneratorController {
  final GeneratorService generatorService;

  @RequestMapping("/ddd/code")
  public void codeByDDD(HttpServletRequest request, HttpServletResponse response) throws Exception {
    List<String> tables = new ArrayList<>();
    tables.add("device_data");
    byte[] data = generatorService.generatorCode(tables, "scud_device");
    String date = GenUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
    response.reset();
    response.setHeader("Content-Disposition", "attachment; filename=\"code" + date + ".zip\"");
    response.addHeader("Content-Length", "" + data.length);
    response.setContentType("application/octet-stream; charset=" + "UTF-8");
    IOUtils.write(data, response.getOutputStream());
  }
}
