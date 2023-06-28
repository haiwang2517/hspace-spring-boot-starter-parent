package cn.haiyinlong.hspace.codegenerator.controller;

import cn.haiyinlong.hspace.codegenerator.service.GeneratorService;
import cn.haiyinlong.hspace.codegenerator.utils.GenUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/generator")
@RequiredArgsConstructor
public class GeneratorController {
  final GeneratorService generatorService;

  @RequestMapping("/ddd/code")
  public void codeByDDD(HttpServletRequest request, HttpServletResponse response) throws Exception {
    List<String> tables = new ArrayList<>();
    tables.add("dict");
    tables.add("dict_item");
    byte[] data = generatorService.generatorCode(tables);
    String date = GenUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
    response.reset();
    response.setHeader("Content-Disposition", "attachment; filename=\"code" + date + ".zip\"");
    response.addHeader("Content-Length", "" + data.length);
    response.setContentType("application/octet-stream; charset=" + "UTF-8");
    IOUtils.write(data, response.getOutputStream());
  }
}
